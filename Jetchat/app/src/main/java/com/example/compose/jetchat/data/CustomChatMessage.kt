package com.example.compose.jetchat.data

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionCall

/**
 * Wrapper for the final `ChatMessage` class so that we
 * can count tokens and split the grounding from the
 * user query for embedding-supported (RAG) requests
 */
class CustomChatMessage @OptIn(BetaOpenAI::class) constructor(
    public val role: ChatRole,
    public val grounding: String? = null,
    public val userContent: String? = null,
    public val name: String? = null,
    public val functionCall: FunctionCall? = null
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

    /**
     * Count the number of tokens in the user query PLUS the additional
     * embedding dato for grounding OR the functions when userContent is empty
     */
    public fun getTokenCount(includeGrounding: Boolean = true) : Int {
        var messageContent = userContent ?: ""
        if (includeGrounding) {
            messageContent = grounding + userContent ?: ""
        }

        if (userContent.isNullOrEmpty()) {
            messageContent = "" + functionCall?.name + functionCall?.arguments
        }
        var messageTokens = Tokenizer.countTokensIn(messageContent)

        return messageTokens
    }

    /**
     * Create `ChatMessage` instance to add to completion request
     */
    @OptIn(BetaOpenAI::class)
    public fun getChatMessage (includeGrounding: Boolean = true) : ChatMessage {
        var content = userContent
        if (includeGrounding) {
            content += grounding
        }
        return ChatMessage(role = role, content = content, name = name, functionCall = functionCall)
    }
}