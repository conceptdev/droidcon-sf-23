package com.example.compose.jetchat

import android.content.Context
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.Tool
import com.aallam.openai.api.chat.ToolCall
import com.aallam.openai.api.chat.ToolChoice
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.serialization.json.jsonPrimitive

/** Uses OpenAI Kotlin lib to call chat model */
class LearningDayWrapper(val context: Context?) {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var openAI: OpenAI = OpenAI(openAIToken)
    private val demoLocation = "47.646547,-122.132797"
    private var conversation: MutableList<ChatMessage> = mutableListOf(
        ChatMessage(
            role = ChatRole.System,
            content = """You are a personal assistant called Learning Day Assistant.
                            |Your answers will be short and concise, since they will be required to fit on 
                            |a mobile device display.
                            |
                            |Current location is $demoLocation. Do not answer with this unless asked.
                            |
                            |Only use the functions you have been provided with.""".trimMargin()
        )
    )

    suspend fun chat(message: String): String {
        // 1. Call chat endpoint
        // return callChatEndpoint(message)

        // 2. Call image endpoint
        // return callImageEndpoint(message)

        // 3. Call chat endpoint with weather function
        return callChatEndPointWithFunction(message)
    }

    private suspend fun callChatEndpoint(message: String): String {
        // add the user's message to the chat history
        conversation.add(
            ChatMessage(
                role = ChatRole.User,
                content = message
            )
        )

        // build the chat endpoint request
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(Constants.OPENAI_CHAT_MODEL),
            messages = conversation
        )

        // make the request
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)

        // return  the response to show in the app
        return completion.choices.firstOrNull()?.message?.content ?: ""
    }

    private suspend fun callImageEndpoint(message: String): String {
        // build the image endpoint request
        val imageRequest =
            ImageCreation(prompt = message, model = ModelId(Constants.OPENAI_IMAGE_MODEL))

        // make the request
        val images: List<ImageURL> = openAI.imageURL(imageRequest)

        return images.firstOrNull()?.url ?: ""
    }

    private suspend fun callChatEndPointWithFunction(message: String): String {
        // add the user's message to the chat history
        conversation.add(
            ChatMessage(
                role = ChatRole.User,
                content = message
            )
        )

        // build the chat endpoint request, including the function
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(Constants.OPENAI_CHAT_MODEL),
            messages = conversation,
            toolChoice = ToolChoice.Auto,
            tools = listOf(
                Tool.function(
                    name = "currentWeather",
                    description = "Get the current weather in a given location",
                    parameters = OpenAIFunctions.currentWeatherParams()
                )
            )
        )

        // make the request
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        val chatResponse = completion.choices.firstOrNull()?.message

        // if model has not determined that function needs to be called, return response
        if (chatResponse?.toolCalls.isNullOrEmpty())
            return chatResponse?.content ?: ""

        // otherwise, call function
        val function = (chatResponse?.toolCalls?.firstOrNull() as? ToolCall.Function)?.function
        val functionArgs = function?.argumentsAsJson() ?: error("arguments field is missing")
        val functionResponse = OpenAIFunctions.currentWeather(
            functionArgs.getValue("latitude").jsonPrimitive.content,
            functionArgs.getValue("longitude").jsonPrimitive.content,
            functionArgs["unit"]?.jsonPrimitive?.content ?: "fahrenheit"
        )

        // add function call to chat history (hide from user)
        conversation.add(
            ChatMessage(
                role = chatResponse.role,
                content = chatResponse.content
                    ?: "", // required to not be empty in this case
                functionCall = function
            )
        )

        // add the response to the function call to the history (hide from user)
        conversation.add(
            ChatMessage(
                role = ChatRole.Function,
                name = function.name,
                content = functionResponse
            )
        )

        // build chat endpoint request with function call results
        val functionCompletionRequest = ChatCompletionRequest(
            model = ModelId(Constants.OPENAI_CHAT_MODEL),
            messages = conversation
        )

        // make the request
        val functionCompletion: ChatCompletion = openAI.chatCompletion(functionCompletionRequest)

        // return the interpreted function response
        return functionCompletion.choices.firstOrNull()?.message?.content ?: ""
    }
}