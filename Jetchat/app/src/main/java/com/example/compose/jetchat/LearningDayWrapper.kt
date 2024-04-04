package com.example.compose.jetchat

import android.content.Context
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.client.OpenAI

/** Uses OpenAI Kotlin lib to call chat model */
class LearningDayWrapper(val context: Context?) {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var openAI: OpenAI = OpenAI(openAIToken)
    private var conversation: MutableList<ChatMessage> = mutableListOf(
        ChatMessage(
            role = ChatRole.System,
            content = """You are a personal assistant called Learning Day Assistant.
                            |Your answers will be short and concise, since they will be required to fit on 
                            |a mobile device display.
                            |Only use the functions you have been provided with.""".trimMargin()
        )
    )

    suspend fun chat(message: String): String {
    }
}