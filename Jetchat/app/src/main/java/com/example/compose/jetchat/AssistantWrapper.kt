package com.example.compose.jetchat

import android.content.Context
import android.util.Log
import androidx.compose.ui.input.key.Key.Companion.Sleep
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionMode
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.client.OpenAI
import com.example.compose.jetchat.data.CustomChatMessage
import com.example.compose.jetchat.data.DroidconDbHelper
import com.example.compose.jetchat.data.EmbeddingHistory
import com.example.compose.jetchat.data.HistoryDbHelper
import com.example.compose.jetchat.data.SlidingWindow
import com.example.compose.jetchat.functions.AddFavoriteFunction
import com.example.compose.jetchat.functions.AskWikipediaFunction
import kotlinx.serialization.json.jsonPrimitive

/** Uses OpenAI Kotlin lib to call chat model */
@OptIn(BetaOpenAI::class)
class AssistantWrapper(val context: Context?) {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var openAI: OpenAI = OpenAI(openAIToken)

    private var assistant: Assistant? =null
    private var thread: com.aallam.openai.api.thread.Thread? = null

    init {

    }

    suspend fun chat(message: String): String {

        if (assistant == null) {
            assistant = openAI.assistant(id = AssistantId("asst_bykuslT6y2DWikNORnzl3ZTE"))


            thread = openAI.thread()
        }



        val userMessage = openAI.message (
            threadId = thread!!.id,
            request = MessageRequest(
                role = Role.User,
                content = message
            )
        )

        val run = openAI.createRun(
            threadId = thread!!.id,
            request = RunRequest(assistantId = assistant!!.id)
        )


        do
        {
            Thread.sleep(1_000)
            val runTest = openAI.getRun(thread!!.id, run.id)

        } while (runTest.status != Status.Completed)

        val messages = openAI.messages(thread!!.id)
        val message =messages.first()
        val messageContent = message.content[0]
        if (messageContent is MessageContent.Text) {
            return messageContent.text.value
        }
        if (messageContent is MessageContent.Image) {
            return "TODO: image file id:" + messageContent.fileId
        }
        return ""

//        // build the OpenAI network request
//        val chatCompletionRequest = chatCompletionRequest {
//            model = ModelId(Constants.OPENAI_CHAT_MODEL)
//            messages = chatWindowMessages
//            // hardcoding weather function every time (for now)
//            functions {
//                function {
//                    name = "currentWeather"
//                    description = "Get the current weather in a given location"
//                    parameters = OpenAIFunctions.currentWeatherParams()
//                }
//                function {
//                    name = AskWikipediaFunction.name()
//                    description = AskWikipediaFunction.description()
//                    parameters = AskWikipediaFunction.params()
//                }
//            }
//            functionCall = FunctionMode.Auto
//        }
//        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
//        val completionMessage = completion.choices.first().message ?: error("no response found!")
//
//        // extract the response to show in the app
//        var chatResponse = completionMessage.content ?: ""
//
//        if (completionMessage.functionCall == null) {
//            // no function, add the response to the conversation history
//            val botResponse =CustomChatMessage(
//                role = ChatRole.Assistant,
//                userContent = chatResponse
//            )
//            conversation.add(botResponse)
//            // add message pair to history database
//            EmbeddingHistory.storeInHistory(openAI, dbHelper, userMessage, botResponse)
//        } else { // handle function
//
//            val function = completionMessage.functionCall
//            Log.i("LLM", "Function ${function!!.name} was called")
//
//            var functionResponse = ""
//            var handled = true
//            when (function.name) {
//                "currentWeather" -> {
//                    val functionArgs = function.argumentsAsJson() ?: error("arguments field is missing")
//                    functionResponse = OpenAIFunctions.currentWeather(
//                        functionArgs.getValue("latitude").jsonPrimitive.content,
//                        functionArgs.getValue("longitude").jsonPrimitive.content,
//                        functionArgs["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
//                    )
//                }
//                AskWikipediaFunction.name() -> {
//                    val functionArgs =
//                        function.argumentsAsJson() ?: error("arguments field is missing")
//                    val query = functionArgs.getValue("query").jsonPrimitive.content
//
//                    Log.i("LLM-WK", "Argument $query ")
//
//                    functionResponse = AskWikipediaFunction.function(query)
//                }
//                else -> {
//                    Log.i("LLM", "Function ${function!!.name} does not exist")
//                    handled = false
//                    chatResponse += " " + function.name + " " + function.arguments
//                    conversation.add(
//                        CustomChatMessage(
//                            role = ChatRole.Assistant,
//                            userContent = chatResponse
//                        )
//                    )
//                }
//            }
//
//
//            if (handled)
//            {
//                // add the 'call a function' response to the history
//                conversation.add(
//                    CustomChatMessage(
//                        role = completionMessage.role,
//                        userContent = completionMessage.content ?: "", // required to not be empty in this case
//                        functionCall = completionMessage.functionCall
//                    )
//                )
//                // add the response to the 'function' call to the history
//                conversation.add(
//                    CustomChatMessage(
//                        role = ChatRole.Function,
//                        name = function.name,
//                        userContent = functionResponse
//                    )
//                )
//
//                // sliding window - with the function call messages,
//                // we might need to remove more from the history
//                val functionChatWindowMessages = SlidingWindow.chatHistoryToWindow(conversation, 50)
//
//                // send the function request/response back to the model
//                val functionCompletionRequest = chatCompletionRequest {
//                    model = ModelId(Constants.OPENAI_CHAT_MODEL)
//                    messages = functionChatWindowMessages }
//                val functionCompletion: ChatCompletion = openAI.chatCompletion(functionCompletionRequest)
//                // show the interpreted function response as chat completion
//                chatResponse = functionCompletion.choices.first().message?.content!!
//                val botResponse = CustomChatMessage(
//                    role = ChatRole.Assistant,
//                    userContent = chatResponse
//                )
//                conversation.add(botResponse)
//
//                if (completionMessage.functionCall == null) {
//                    // wasn't a function, add message pair to history database
//                    // prevents historical answers from being used
//                    // instead of calling the function again
//                    // (ie returns old weather info)
//                    EmbeddingHistory.storeInHistory(openAI, dbHelper, userMessage, botResponse)
//                }
//            }
//        }
//
//        return chatResponse
    }

    /** OpenAI generate image from prompt text.
     * Returns a URL to the image, must be downloaded or
     * rendered from the URL (no bytes returned from API)
     * @return image URL or empty string */
    suspend fun imageURL(prompt: String): String {
        val imageRequest = ImageCreation(prompt = prompt, model = ModelId(Constants.OPENAI_IMAGE_MODEL))

        // OpenAI network request
        val images: List<ImageURL> = openAI.imageURL(imageRequest)

        return if (images.isEmpty()) "" else images[0].url
    }
}