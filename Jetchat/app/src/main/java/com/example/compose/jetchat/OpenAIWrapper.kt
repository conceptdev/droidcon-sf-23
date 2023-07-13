package com.example.compose.jetchat

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
import kotlinx.serialization.json.jsonPrimitive

/** Uses OpenAI Kotlin lib to call chat model */
@OptIn(BetaOpenAI::class)
class OpenAIWrapper {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var conversation: MutableList<ChatMessage>
    private var openAI: OpenAI = OpenAI(openAIToken)

    init {
        conversation = mutableListOf(
            ChatMessage(
                role = ChatRole.System,
                content = """You are a personal assistant called JetchatAI.
                            Your answers will be short and concise, since they will be required to fit on 
                            a mobile device display.
                            Only use the functions you have been provided with.""".trimMargin()
            )
        )
    }

    suspend fun chat(message: String): String {
        // grounding (location)
        // TODO: use location services to determine latitude/longitude for current location
        val groundedMessage = "Current location is ${Constants.TEST_LOCATION}.\n\n$message"

        // add the user's message to the chat history
        conversation.add(
            ChatMessage(
                role = ChatRole.User,
                content = groundedMessage
            )
        )

        // build the OpenAI network request
        val chatCompletionRequest = chatCompletionRequest {
            model = ModelId(Constants.OPENAI_CHAT_MODEL)
            messages = conversation
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
            conversation.add(
                ChatMessage(
                    role = ChatRole.Assistant,
                    content = chatResponse
                )
            )
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
                    ChatMessage(
                        role = completionMessage.role,
                        content = completionMessage.content ?: "", // required to not be empty in this case
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
                    messages = conversation }
                val functionCompletion: ChatCompletion = openAI.chatCompletion(functionCompletionRequest)
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

    suspend fun imageURL(prompt: String): String {
        val imageRequest = ImageCreation(prompt)

        // OpenAI network request
        val images: List<ImageURL> = openAI.imageURL(imageRequest)

        return if (images.isEmpty()) "" else images[0].url
    }
}