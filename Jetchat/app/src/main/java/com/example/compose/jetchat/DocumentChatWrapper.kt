package com.example.compose.jetchat

import android.content.ContentValues
import android.content.Context
import android.os.Build
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
import com.example.compose.jetchat.data.CustomChatMessage
import com.example.compose.jetchat.data.DocumentContract
import com.example.compose.jetchat.data.DocumentDbHelper
import com.example.compose.jetchat.data.SlidingWindow
import com.example.compose.jetchat.functions.AskDocumentFunction
import kotlinx.serialization.json.JsonNull.content
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.SortedMap

/** THIS IS A COPY OF OpenAIWrapper
 *
 * Adds embeddings to allow grounding in embedded text documents from
 * https://github.com/Azure-Samples/azure-search-openai-demo/
 *
 * `context` comes from NavActivity > MainViewModel to
 * enable Sqlite database helper
 * */
@OptIn(BetaOpenAI::class)
class DocumentChatWrapper(val context: Context?) {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var conversation: MutableList<CustomChatMessage> = mutableListOf()
    private var openAI: OpenAI = OpenAI(openAIToken)
    /** Sqlite access for favorites, embeddings, and SQL queries */
    private val dbHelper = DocumentDbHelper(context)
    /** key'd map of document sentence to vector - these are generated
     * via web API and stored locally in a Sqlite database,
     * then loaded into memory on first use */
    private var vectorCache: MutableMap<String, DoubleArray> = mutableMapOf()
    /** key'd map of document sentence, so we can match to
     * embedding vectors */
    private var documentCache: MutableMap<String, String> = mutableMapOf()

    private var systemMessage: CustomChatMessage
    init {
        systemMessage = CustomChatMessage (
            role = ChatRole.System,
            grounding = """
                    You are a personal assistant for Contoso employees. 
                    You will answer questions about Contoso employee benefits from various employee manuals.
                    Your answers will be short and concise, since they will be required to fit on 
                    a mobile device display.
                    Only use the functions you have been provided with.""".trimMargin()
        )
        conversation.add(systemMessage)
    }

    suspend fun chat(message: String): String {

        initVectorCache(dbHelper) // loads from web to database, then re-uses database

        val messagePreamble = grounding(message)

        // add the user's message to the chat history
        conversation.add(
            CustomChatMessage(
                role = ChatRole.User,
                grounding = messagePreamble,
                userContent = message
            )
        )

        // implement sliding window. hardcode 200 tokens used for functions definitions.
        val chatWindowMessages = SlidingWindow.chatHistoryToWindow(conversation, reservedForFunctionsTokens=200)

        // build the OpenAI network request
        val chatCompletionRequest = chatCompletionRequest {
            model = ModelId(Constants.OPENAI_CHAT_MODEL)
            messages = chatWindowMessages //  previously sent the entire conversation
            // hardcoding functions every time (for now)
            functions {
                function {
                    name = AskDocumentFunction.name()
                    description = AskDocumentFunction.description()
                    parameters = AskDocumentFunction.params(dbHelper)
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
            Log.i("LLM", "No function call was made, showing LLM response")
            conversation.add(
                CustomChatMessage(
                    role = ChatRole.Assistant,
                    userContent = chatResponse
                )
            )
        } else { // handle function call
            val function = completionMessage.functionCall
            Log.i("LLM", "Function ${function!!.name} was called")

            var functionResponse = ""
            var handled = true
            when (function.name) {
                AskDocumentFunction.name() -> {
                    val functionArgs =
                        function.argumentsAsJson() ?: error("arguments field is missing")
                    val query = functionArgs.getValue("query").jsonPrimitive.content
                    Log.i("LLM", "SQL query $query")

                    functionResponse = AskDocumentFunction.function(dbHelper, query)
                }

                else -> {
                    Log.i("LLM", "Function ${function!!.name} does not exist")
                    handled = false
                    chatResponse += " " + function.name + " " + function.arguments
                    conversation.add(
                        CustomChatMessage(
                            role = ChatRole.Assistant,
                            userContent = chatResponse
                        )
                    )
                }
            }
            if (handled) {
                // add the 'call a function' response to the history
                conversation.add(
                    CustomChatMessage(
                        role = completionMessage.role,
                        userContent = completionMessage.content
                            ?: "", // required to not be empty in this case
                        functionCall = completionMessage.functionCall
                    )
                )

                // add the response to the 'function' call to the history
                // so that the LLM can form the final user-response
                conversation.add(
                    CustomChatMessage(
                        role = ChatRole.Function,
                        name = function.name,
                        userContent = functionResponse
                    )
                )

                // sliding window - with the function call messages,
                // we might need to remove more from the history
                val functionChatWindowMessages = SlidingWindow.chatHistoryToWindow(conversation, 200)

                // send the function request/response back to the model
                val functionCompletionRequest = chatCompletionRequest {
                    model = ModelId(Constants.OPENAI_CHAT_MODEL)
                    messages = functionChatWindowMessages // previously sent the entire conversation
                }
                val functionCompletion: ChatCompletion =
                    openAI.chatCompletion(functionCompletionRequest)
                // show the interpreted function response as chat completion
                chatResponse = functionCompletion.choices.first().message?.content!!
                // ignore trimmedConversation, will be recreated
                conversation.add(
                    CustomChatMessage(
                        role = ChatRole.Assistant,
                        userContent = chatResponse
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
        // uncomment to examine vector comparison scores
        //  Log.v("LLM", "Comparing input to ${session.key} dot $v")
        }
        if (sortedVectors.lastKey() > 0.8) { // arbitrary match threshold
            Log.i("LLM", "Top match is ${sortedVectors.lastKey()}")

            messagePreamble =
                "The following information is extract from Contoso employee handbooks and help plans:\n\n"
            for (dpKey in sortedVectors.tailMap(0.8)) {
                Log.i("LLM", "Add to preamble: ${dpKey.key} -> ${dpKey.value}")

                messagePreamble += documentCache[dpKey.value]+ "\n\n"

            }
            messagePreamble += "\n\nUse the above information to answer the following question:\n\n"
            Log.v("LLM", "$messagePreamble")
        } else {
            Log.i("LLM", "Top match was ${sortedVectors.lastKey()} which was below 0.8 and failed to meet criteria for grounding data")
        }

        if (messagePreamble.isNullOrEmpty()) {
            // ONLY show date/time when embeddings are empty, as it triggers the SessionsByTime function (I THINK)
            // ALWAYS add the date and time to every prompt
            var date = Constants.TEST_DATE
            var time = Constants.TEST_TIME
            if (date == "") {
                date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
            if (time == "") {
                time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            messagePreamble =
                "The current date is $date and the time (in 24 hour format) is $time.\n\n$messagePreamble"
        }
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
    private suspend fun initVectorCache (dbHelper: DocumentDbHelper) {

        if (context == null)
            return // cannot open resources

        // if empty, first try to load from database
        if (vectorCache.isEmpty()) {
            Log.i("LLM", "Attempt vector cache load from database")
            loadVectorCache(dbHelper)
        }

        // if still empty, generate embeddings via web API and save to database
        if (vectorCache.isEmpty()) {
            Log.i("LLM", "Generate & save embeddings to database. \"SELECT COUNT (*) FROM ${DocumentContract.EmbeddingEntry.TABLE_NAME}")

            // Gets the data repository in write mode
            val db = dbHelper.writableDatabase
            // Check no rows already (maybe we'll delete to start fresh?)
            val selCursor = db.rawQuery(
                "SELECT COUNT (*) FROM ${DocumentContract.EmbeddingEntry.TABLE_NAME}",
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

                var documentId = -1
                //val rawResources = listOf(R.raw.employee_handbook, R.raw.perks_plus, R.raw.role_library, R.raw.benefit_options, R.raw.northwind_standard_benefits_details, R.raw.northwind_health_plus_benefits_details)

                val rawResources = listOf(R.raw.benefit_options)

                for (resId in rawResources) {
                    documentId++
                    val inputStream = context.resources.openRawResource(resId)
                    val documentText = inputStream.bufferedReader().use { it.readText() }

                    val documentSentences = documentText.split(Regex("[.!?]\\s*"))

                    var sentenceId = -1
                    for (sentence in documentSentences){
                        val s = sentence.trim()
                        if (s.isNotEmpty()){
                            sentenceId++
                            val embeddingRequest = EmbeddingRequest(
                                model = ModelId(Constants.OPENAI_EMBED_MODEL),
                                input = listOf(s)
                            )
                            val embedding = openAI.embeddings(embeddingRequest)
                            val vector = embedding.embeddings[0].embedding.toDoubleArray()

                            // add to in-memory version
                            vectorCache["$documentId-$sentenceId"] = vector
                            documentCache["$documentId-$sentenceId"] = s

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
                                put(DocumentContract.EmbeddingEntry.COLUMN_NAME_CHUNKID, "$documentId-$sentenceId")
                                put(DocumentContract.EmbeddingEntry.COLUMN_NAME_CONTENT, s)
                                put(DocumentContract.EmbeddingEntry.COLUMN_NAME_VECTOR, vectorString)
                            }

                            // Insert the new row, returning the primary key value of the row (would be -1 if error)
                            val newRowId =
                                db?.insert(DocumentContract.EmbeddingEntry.TABLE_NAME, null, values)
                            Log.v("LLM", "insert into database ($newRowId) \"$documentId-$sentenceId\" vector: $vector sentence: $s")
                        }
                    }
                }
            }
        }
    }

    /** Populates `vectorCache` field in class with
     * session ID and embedding vector from local database.
     *
     * @return Number of rows loaded */
    private fun loadVectorCache(dbHelper: DocumentDbHelper): Int {
        var rowCount = 0
        val db = dbHelper.readableDatabase

        val projection = arrayOf(
            DocumentContract.EmbeddingEntry.COLUMN_NAME_CHUNKID,
            DocumentContract.EmbeddingEntry.COLUMN_NAME_CONTENT,
            DocumentContract.EmbeddingEntry.COLUMN_NAME_VECTOR)

        val cursor = db.query(
            DocumentContract.EmbeddingEntry.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,           // The columns for the WHERE clause
            null,       // The values for the WHERE clause
            null,           // don't group the rows
            null,            // don't filter by row groups
            null            // The sort order
        )

        with(cursor) {
            while (moveToNext()) {
                val chunkId = getString(getColumnIndexOrThrow(DocumentContract.EmbeddingEntry.COLUMN_NAME_CHUNKID))
                val content = getString(getColumnIndexOrThrow(DocumentContract.EmbeddingEntry.COLUMN_NAME_CONTENT))
                val vectorString = getString(getColumnIndexOrThrow(DocumentContract.EmbeddingEntry.COLUMN_NAME_VECTOR))
                // deserialize vector
                val vectorSplit = vectorString.split(',')
                var vector = mutableListOf<Double>()
                for (v in vectorSplit) {
                    vector.add(v.toDouble())
                }
                // add to in-memory cache
                vectorCache[chunkId] = vector.toDoubleArray()
                documentCache[chunkId] = content
                Log.v("LLM", "load from database $chunkId vector: ${vector.toDoubleArray()} sentence: $content")
                rowCount++
            }
        }
        cursor.close()
        Log.i("LLM", "loaded from database $rowCount rows")
        return rowCount
    }
}