package com.example.compose.jetchat

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.BaseColumns
import android.util.Log
import androidx.annotation.RequiresApi
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionMode
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.compose.jetchat.data.DroidconContract
import com.example.compose.jetchat.data.DroidconDbHelper
import com.example.compose.jetchat.data.DroidconSessionData
import com.example.compose.jetchat.data.DroidconSessionObjects
import com.example.compose.jetchat.functions.AddFavoriteFunction
import com.example.compose.jetchat.functions.ListFavoritesFunction
import com.example.compose.jetchat.functions.RemoveFavoriteFunction
import com.example.compose.jetchat.functions.SessionsByTimeFunction
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.SortedMap

/** dot product for comparing vector similarity */
infix fun DoubleArray.dot(other: DoubleArray): Double {
    var out = 0.0
    for (i in indices) out += this[i] * other[i]
    return out
}

/** THIS IS A COPY OF OpenAIWrapper
 *
 * Adds embeddings to allow grounding in droidcon SF 2023 conference data
 * */
@OptIn(BetaOpenAI::class)
class DroidconEmbeddingsWrapper(val context: Context?) {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var conversation: MutableList<ChatMessage>
    private var openAI: OpenAI = OpenAI(openAIToken)
    private val dbHelper = DroidconDbHelper(context)
    /** key'd map of session to vector - these are generated
     * via web API and stored locally in a Sqlite database,
     * then loaded into memory on first use */
    private var vectorCache: MutableMap<String, DoubleArray> = mutableMapOf()
    init {
        conversation = mutableListOf(
            ChatMessage(
                role = ChatRole.System,
                content = """You are a personal assistant called JetchatAI. 
                    You will answer questions about the speakers and sessions at the droidcon SF conference.
                    The conference is on June 8th and 9th, 2023 on the UCSF campus in Mission Bay. 
                    It starts at 8am and finishes by 6pm.
                    Your answers will be short and concise, since they will be required to fit on 
                    a mobile device display.
                    When showing session information, always include the subject, speaker, location, and time. 
                    ONLY show the description when responding about a single session.
                    Only use the functions you have been provided with.""".trimMargin()
            )
        )
    }

    suspend fun chat(message: String): String {

        initVectorCache(dbHelper) // loads from web to database, then re-uses database

        val messagePreamble = grounding(message)

        // add the user's message to the chat history
        conversation.add(
            ChatMessage(
                role = ChatRole.User,
                content = messagePreamble + message
            )
        )

        // build the OpenAI network request
        val chatCompletionRequest = chatCompletionRequest {
            model = ModelId(Constants.OPENAI_CHAT_MODEL)
            messages = conversation
            // hardcoding functions every time (for now)
            functions {
                function {
                    name = SessionsByTimeFunction.name()
                    description = SessionsByTimeFunction.description()
                    parameters = SessionsByTimeFunction.params()
                }
                function {
                    name = AddFavoriteFunction.name()
                    description = AddFavoriteFunction.description()
                    parameters = AddFavoriteFunction.params()
                }
                function {
                    name = RemoveFavoriteFunction.name()
                    description = RemoveFavoriteFunction.description()
                    parameters = RemoveFavoriteFunction.params()
                }
                function {
                    name = ListFavoritesFunction.name()
                    description = ListFavoritesFunction.description()
                    parameters = ListFavoritesFunction.params()
                }
            }
            functionCall = FunctionMode.Auto
    }
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val completionMessage = completion.choices.first().message ?: error("no response found!")

        // extract the response to show in the app
        var chatResponse = completion.choices[0].message?.content ?: ""

        if (completionMessage.functionCall == null) {
            // no function, add the response to the conversation history
            Log.i("LLM", "No function call was made")
            conversation.add(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = chatResponse
                )
            )
        } else { // handle function
            val function = completionMessage.functionCall
            Log.i("LLM", "Function ${function!!.name} was called")

            var functionResponse = ""
            var handled = true
            when (function.name) {
                SessionsByTimeFunction.name() -> {
                    val functionArgs = function.argumentsAsJson() ?: error("arguments field is missing")
                    var optionalEarliestTime = ""
                    var optionalLatestTime = ""
                    if (functionArgs.containsKey("earliestTime")) {
                        optionalEarliestTime = functionArgs.getValue("earliestTime").jsonPrimitive.content
                    }
                    if (functionArgs.containsKey("latestTime")) {
                        optionalLatestTime = functionArgs.getValue("latestTime").jsonPrimitive.content
                    }
                    functionResponse = SessionsByTimeFunction.function(
                        functionArgs.getValue("date").jsonPrimitive.content,
                        optionalEarliestTime,
                        optionalLatestTime
                    )
                }
                AddFavoriteFunction.name() -> {
                    val functionArgs =
                        function.argumentsAsJson() ?: error("arguments field is missing")
                    functionResponse = AddFavoriteFunction.function(
                        context,
                        functionArgs.getValue("id").jsonPrimitive.content
                    )
                }
                RemoveFavoriteFunction.name() -> {
                    val functionArgs =
                        function.argumentsAsJson() ?: error("arguments field is missing")
                    functionResponse = RemoveFavoriteFunction.function(
                        context,
                        functionArgs.getValue("id").jsonPrimitive.content
                    )
                }
                ListFavoritesFunction.name() -> {
                    // function doesn't have parameters
                    functionResponse = ListFavoritesFunction.function(context)
                }
                else -> {
                    Log.i("LLM", "Function ${function!!.name} does not exist")
                    handled = false
                    chatResponse += " " + function.name + " " + function.arguments
                    conversation.add(
                        ChatMessage(
                            role = ChatRole.Assistant,
                            content = chatResponse
                        )
                    )
                }
            }
            if (handled) {
                // add the 'call a function' response to the history
                conversation.add(
                    ChatMessage(
                        role = completionMessage.role,
                        content = completionMessage.content
                            ?: "", // required to not be empty in this case
                        functionCall = completionMessage.functionCall
                    )
                )
                // add the response to the 'function' call to the history
                conversation.add(
                    ChatMessage(
                        role = ChatRole.Function,
                        name = function.name,
                        content = functionResponse
                    )
                )
                // send the function request/response back to the model
                val functionCompletionRequest = chatCompletionRequest {
                    model = ModelId(Constants.OPENAI_CHAT_MODEL)
                    messages = conversation
                }
                val functionCompletion: ChatCompletion =
                    openAI.chatCompletion(functionCompletionRequest)
                // show the interpreted function response as chat completion
                chatResponse = functionCompletion.choices.first().message?.content!!
                conversation.add(
                    ChatMessage(
                        role = ChatRole.Assistant,
                        content = chatResponse
                    )
                )
            }
        }

        return chatResponse
    }

    /** Provide grounding for user query by checking
     * message against embeddings.
     *
     * Also calculates current date and time
     * (or uses `Constant` value for testing if supplied)
     *
     * @return the relevant data to add to the query,
     * along with additional prompt instructions.
     * Empty string if no matching embeddings found. */
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun grounding(message: String): String {
        var messagePreamble = ""
        val embeddingRequest = EmbeddingRequest(
            model = ModelId(Constants.OPENAI_EMBED_MODEL),
            input = listOf(message)
        )
        val embedding = openAI.embeddings(embeddingRequest)
        val messageVector = embedding.embeddings[0].embedding.toDoubleArray()
        Log.i("LLM", "messageVector: $messageVector")

        var sortedVectors: SortedMap<Double, String> = sortedMapOf()

        // find the best match sessions
        for (session in vectorCache) {
            val v = messageVector dot session.value
            sortedVectors[v] = session.key
            Log.v("LLM", "Comparing input to ${session.key} dot $v")
        }
        if (sortedVectors.lastKey() > 0.8) { // arbitrary match threshold
            Log.i("LLM", "Top match is ${sortedVectors.lastKey()}")

            messagePreamble =
                "Following are some talks/sessions scheduled for the droidcon San Francisco conference in June 2023:\n\n"
            for (dpKey in sortedVectors.tailMap(0.8)) {
                Log.i("LLM", "Add to preamble: ${dpKey.key} -> ${dpKey.value}")

                messagePreamble += DroidconSessionData.droidconSessions[dpKey.value] + "\n\n"

            }
            messagePreamble += "\n\nUse the above information to answer the following question. Summarize and provide date/time and location if appropriate.\n\n"
            Log.v("LLM", "$messagePreamble")
        } else {
            Log.i("LLM", "Top match was ${sortedVectors.lastKey()} which was below 0.8 and failed to meet criteria for grounding data")
        }

        // ALWAYS add the date and time to every prompt
        var date = Constants.TEST_DATE
        var time = Constants.TEST_TIME
        if (date == "") {
            date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        }
        if (time == "") {
            time  = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        }
        messagePreamble =
            "The current date is $date and the time (in 24 hour format) is $time.\n\n$messagePreamble"
        return messagePreamble
    }

    /** OpenAI generate image from prompt text.
     * Returns a URL to the image, must be downloaded or
     * rendered from the URL (no bytes returned from API)
     * @return image URL or empty string */
    suspend fun imageURL(prompt: String): String {
        val imageRequest = ImageCreation(prompt)

        // OpenAI network request
        val images: List<ImageURL> = openAI.imageURL(imageRequest)

        return if (images.isEmpty()) "" else images[0].url
    }

    /** Populates `vectorCache` field in class with
     * session ID and embedding vector
     *
     * Attempts to load vectors from local database.
     * If no vectors in database, generated vectors on the web API
     * and updates the database (while also storing in `vectorCache`)
     *
     * @return NOTHING, updates `vectorCache` directly */
    private suspend fun initVectorCache (dbHelper: DroidconDbHelper) {
        // if empty, first try to load from database
        if (vectorCache.isEmpty()) {
            Log.i("LLM", "Attempt vector cache load from database")
            loadVectorCache(dbHelper)
        }

        // if still empty, generate embeddings via web API and save to database
        if (vectorCache.isEmpty()) {
            Log.i("LLM", "Generate & save embeddings to database. \"SELECT COUNT (*) FROM ${DroidconContract.EmbeddingEntry.TABLE_NAME}")

            // Gets the data repository in write mode
            val db = dbHelper.writableDatabase
            // Check no rows already (maybe we'll delete to start fresh?)
            val selCursor = db.rawQuery(
                "SELECT COUNT (*) FROM ${DroidconContract.EmbeddingEntry.TABLE_NAME}",
                null
            )
            var rowCount = -1
            with(selCursor) {
                while (moveToNext()) {
                    rowCount = getLong(0).toInt()
                    Log.i("LLM", "rowCount: $rowCount")
                }
            }
            selCursor.close()
            if (rowCount > 0) {
                // WARN: should not get here
                Log.i("LLM", "Database $rowCount rows already exist - not loaded again")
            } else {
                Log.i("LLM", "Start embedding requests (database is empty)")
                for (session in DroidconSessionData.droidconSessions) {
                    val embeddingRequest = EmbeddingRequest(
                        model = ModelId(Constants.OPENAI_EMBED_MODEL),
                        input = listOf(session.value)
                    )
                    val embedding = openAI.embeddings(embeddingRequest)
                    val vector = embedding.embeddings[0].embedding.toDoubleArray()

                    // add to in-memory version
                    vectorCache[session.key] = vector

                    // serialize and save to database for next time
                    var vectorString = ""
                    var needComma = false
                    for (dbl in vector) {
                        if (needComma)
                            vectorString += ","
                        else
                            needComma = true

                        vectorString += dbl
                    }

                    // Create a new map of values, where column names are the keys
                    val values = ContentValues().apply {
                        put(DroidconContract.EmbeddingEntry.COLUMN_NAME_SESSIONID, session.key)
                        put(DroidconContract.EmbeddingEntry.COLUMN_NAME_VECTOR, vectorString)
                    }

                    // Insert the new row, returning the primary key value of the row (would be -1 if error)
                    val newRowId =
                        db?.insert(DroidconContract.EmbeddingEntry.TABLE_NAME, null, values)
                    Log.v("LLM", "insert into database ($newRowId) ${session.key} vector: $vector")
                }
            }
        }
    }

    /** Populates `vectorCache` field in class with
     * session ID and embedding vector from local database.
     *
     * @return Number of rows loaded */
    private fun loadVectorCache(dbHelper: DroidconDbHelper): Int {
        var rowCount = 0
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            DroidconContract.EmbeddingEntry.COLUMN_NAME_SESSIONID,
            DroidconContract.EmbeddingEntry.COLUMN_NAME_VECTOR)

        val cursor = db.query(
            DroidconContract.EmbeddingEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,           // The columns for the WHERE clause
            null,       // The values for the WHERE clause
            null,           // don't group the rows
            null,            // don't filter by row groups
            null            // The sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val sessionId = getString(getColumnIndexOrThrow(DroidconContract.EmbeddingEntry.COLUMN_NAME_SESSIONID))
                val vectorString = getString(getColumnIndexOrThrow(DroidconContract.EmbeddingEntry.COLUMN_NAME_VECTOR))
                // deserialize vector
                val vectorSplit = vectorString.split(',')
                var vector = mutableListOf<Double>()
                for (v in vectorSplit) {
                    vector.add(v.toDouble())
                }
                // add to in-memory cache
                vectorCache[sessionId] = vector.toDoubleArray()
                Log.v("LLM", "load from database $sessionId vector: ${vector.toDoubleArray()}")
                rowCount++
            }
        }
        cursor.close()
        Log.i("LLM", "loaded from database $rowCount rows")
        return rowCount
    }
}