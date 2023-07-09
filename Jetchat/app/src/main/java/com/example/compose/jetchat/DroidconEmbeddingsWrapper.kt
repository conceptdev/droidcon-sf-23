package com.example.compose.jetchat

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionMode
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.compose.jetchat.data.DroidconSessionData
import kotlinx.serialization.json.jsonPrimitive
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
class DroidconEmbeddingsWrapper {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var conversation: MutableList<ChatMessage>
    private var openAI: OpenAI = OpenAI(openAIToken)

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
                    When showing session information, always include the subject, speaker, location, and time. Show the date and description if needed.
                    Only use the functions you have been provided with.""".trimMargin()
            )
        )
    }

    suspend fun chat(message: String): String {

        initVectorCache() // should only run once (HACK: wait to finish)

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
//                function {
//                    name = TimeForSessionFunctions.name()
//                    description = TimeForSessionFunctions.description()
//                    parameters = TimeForSessionFunctions.params()
//                }
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
                TimeForSessionFunctions.name() -> {
                    val functionArgs =
                        function.argumentsAsJson() ?: error("arguments field is missing")
                    functionResponse = TimeForSessionFunctions.function(
                        functionArgs.getValue("subject").jsonPrimitive.content
                    )
                }
                AddFavoriteFunction.name() -> {
                    val functionArgs =
                        function.argumentsAsJson() ?: error("arguments field is missing")
                    functionResponse = AddFavoriteFunction.function(
                        functionArgs.getValue("id").jsonPrimitive.content
                    )
                }
                RemoveFavoriteFunction.name() -> {
                    val functionArgs =
                        function.argumentsAsJson() ?: error("arguments field is missing")
                    functionResponse = RemoveFavoriteFunction.function(
                        functionArgs.getValue("id").jsonPrimitive.content
                    )
                }
                ListFavoritesFunction.name() -> {
//                    val functionArgs =
//                        function.argumentsAsJson() ?: error("arguments field is missing")
                    functionResponse = ListFavoritesFunction.function()
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
     * @return the relevant data to add to the query,
     * along with additional prompt instructions.
     * Empty string if no matching embeddings found. */
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
            Log.v("LLM", "${session.key} dot $v")
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
        }

        // ALWAYS add the date and time
        messagePreamble =
            "The current date is 2023-06-09 and the time is 15:45.\n\n$messagePreamble"
        return messagePreamble
    }

    suspend fun imageURL(prompt: String): String {
        val imageRequest = ImageCreation(prompt)

        // OpenAI network request
        val images: List<ImageURL> = openAI.imageURL(imageRequest)

        return if (images.isEmpty()) "" else images[0].url
    }

    /** key'd map of session to vector */
    private var vectorCache: MutableMap<String, DoubleArray> = mutableMapOf()

    /** make embedding requests for each session, populate vectorCache */
    suspend fun initVectorCache () {
        if (vectorCache.isEmpty()) {
            for (session in DroidconSessionData.droidconSessions) {
                val embeddingRequest = EmbeddingRequest(
                    model = ModelId(Constants.OPENAI_EMBED_MODEL),
                    input = listOf(session.value)
                )
                val embedding = openAI.embeddings(embeddingRequest)
                val vector = embedding.embeddings[0].embedding.toDoubleArray()
                vectorCache[session.key] = vector
                Log.i("LLM", "$session.key vector: $vector")
            }
        }
    }
}