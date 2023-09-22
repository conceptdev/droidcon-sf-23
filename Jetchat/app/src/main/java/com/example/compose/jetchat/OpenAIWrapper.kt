package com.example.compose.jetchat

import android.content.Context
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionMode
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.compose.jetchat.data.CustomChatMessage
import com.example.compose.jetchat.data.DroidconDbHelper
import com.example.compose.jetchat.data.EmbeddingHistory
import com.example.compose.jetchat.data.HistoryDbHelper
import com.example.compose.jetchat.data.SlidingWindow
import kotlinx.serialization.json.jsonPrimitive

/** Uses OpenAI Kotlin lib to call chat model */
@OptIn(BetaOpenAI::class)
class OpenAIWrapper(val context: Context?) {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var conversation: MutableList<CustomChatMessage>
    private var openAI: OpenAI = OpenAI(openAIToken)
    private val dbHelper = HistoryDbHelper(context)

    init {
        conversation = mutableListOf(
            CustomChatMessage(
                role = ChatRole.System,
                userContent = """You are a personal assistant called JetchatAI.
                            Your answers will be short and concise, since they will be required to fit on 
                            a mobile device display.
                            Current location is ${Constants.TEST_LOCATION} for functions that require location. Do not answer with this unless asked.
                            Only use the functions you have been provided with.""".trimMargin()
            )
        )
        // TODO: use location services to determine latitude/longitude for current location
    }

    suspend fun chat(message: String): String {
        val relevantHistory = EmbeddingHistory.groundInHistory(openAI, dbHelper, message)

        // add the user's message to the chat history
        val userMessage = CustomChatMessage(
            role = ChatRole.User,
            grounding = relevantHistory,
            userContent = message
        )
        conversation.add(userMessage)


        // implement sliding window. hardcode 50 tokens used for the weather function definitions.
        val chatWindowMessages = SlidingWindow.chatHistoryToWindow(conversation, reservedForFunctionsTokens=50)

        // build the OpenAI network request
        val chatCompletionRequest = chatCompletionRequest {
            model = ModelId(Constants.OPENAI_CHAT_MODEL)
            messages = chatWindowMessages
            // hardcoding weather function every time (for now)
            functions {
                function {
                    name = "currentWeather"
                    description = "Get the current weather in a given location"
                    parameters = OpenAIFunctions.currentWeatherParams()
                }
            }
            functionCall = FunctionMode.Auto
        }
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val completionMessage = completion.choices.first().message ?: error("no response found!")

        // extract the response to show in the app
        var chatResponse = completionMessage.content ?: ""

        if (completionMessage.functionCall == null) {
            // no function, add the response to the conversation history
            val botResponse =CustomChatMessage(
                role = ChatRole.Assistant,
                userContent = chatResponse
            )
            conversation.add(botResponse)
            // add message pair to history database
            EmbeddingHistory.storeInHistory(openAI, dbHelper, userMessage, botResponse)
        } else { // handle function
            val function = completionMessage.functionCall
            if (function!!.name == "currentWeather")
            {
                val functionArgs = function.argumentsAsJson() ?: error("arguments field is missing")
                val functionResponse = OpenAIFunctions.currentWeather(
                    functionArgs.getValue("latitude").jsonPrimitive.content,
                    functionArgs.getValue("longitude").jsonPrimitive.content,
                    functionArgs["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
                )

                // add the 'call a function' response to the history
                conversation.add(
                    CustomChatMessage(
                        role = completionMessage.role,
                        userContent = completionMessage.content ?: "", // required to not be empty in this case
                        functionCall = completionMessage.functionCall
                    )
                )
                // add the response to the 'function' call to the history
                conversation.add(
                    CustomChatMessage(
                        role = ChatRole.Function,
                        name = function.name,
                        userContent = functionResponse
                    )
                )

                // sliding window - with the function call messages,
                // we might need to remove more from the history
                val functionChatWindowMessages = SlidingWindow.chatHistoryToWindow(conversation, 50)

                // send the function request/response back to the model
                val functionCompletionRequest = chatCompletionRequest {
                    model = ModelId(Constants.OPENAI_CHAT_MODEL)
                    messages = functionChatWindowMessages }
                val functionCompletion: ChatCompletion = openAI.chatCompletion(functionCompletionRequest)
                // show the interpreted function response as chat completion
                chatResponse = functionCompletion.choices.first().message?.content!!
                val botResponse = CustomChatMessage(
                    role = ChatRole.Assistant,
                    userContent = chatResponse
                )
                conversation.add(botResponse)

                if (completionMessage.functionCall == null) {
                    // wasn't a function, add message pair to history database
                    // prevents historical answers from being used
                    // instead of calling the function again
                    // (ie returns old weather info)
                    EmbeddingHistory.storeInHistory(openAI, dbHelper, userMessage, botResponse)
                }
            }
        }

        return chatResponse
    }

    suspend fun imageURL(prompt: String): String {
        val imageRequest = ImageCreation(prompt)

        // OpenAI network request
        val images: List<ImageURL> = openAI.imageURL(imageRequest)

        return if (images.isEmpty()) "" else images[0].url
    }
}