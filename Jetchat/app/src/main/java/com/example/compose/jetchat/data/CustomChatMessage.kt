package com.example.compose.jetchat.data

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall



class CustomChatMessage @OptIn(BetaOpenAI::class) constructor(
    val role: ChatRole,
    val grounding: String? = null,
    val userContent: String? = null,
    val name: String? = null,
    val functionCall: FunctionCall? = null
    ) {

    public fun summary(): String? {
        return if (userContent.isNullOrEmpty()) {
            var func = functionCall?.name + " " + functionCall?.arguments
            func.replace('\n',' ')
        } else {
            var lines = userContent.split('\n')
            var lastLine = lines.last()
            lastLine
        }
    }

    public fun getTokenCount() : Int {
        var messageContent = grounding + userContent ?: ""
        if (userContent.isNullOrEmpty()) {
            messageContent = "" + functionCall?.name + functionCall?.arguments
        }
        var messageTokens = Tokenizer.countTokensIn(messageContent)

        return messageTokens
    }

    @OptIn(BetaOpenAI::class)
    public fun getChatMessage () : ChatMessage {
        val content = grounding + userContent
        return ChatMessage(role = role, content = content, name = name, functionCall = functionCall)
    }
}