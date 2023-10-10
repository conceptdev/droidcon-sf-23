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
    val role: ChatRole,
    val grounding: String? = null,
    val userContent: String? = null,
    val name: String? = null,
    val functionCall: FunctionCall? = null
    ) {

    fun summary(): String? {
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
     * embedding data for grounding OR the functions when userContent is empty
     *
     * `userContent` can never be larger than `tokensAllowed` - if this happens in
     * message history, the message will be dropped from the window. In theory
     * it could happen if a user entered maxTokens worth of text in their
     * chat query, but in practice that seems unlikely (and should probably
     * be a validation error on the UI)
     */
    fun getTokenCount(includeGrounding: Boolean = true, tokensAllowed: Int = -1) : Int {
        var messageContent = userContent ?: ""
        if (includeGrounding && grounding != null) {
            messageContent = if (tokensAllowed < 0) {
                grounding + messageContent
            } else { // only include as much of the grounding as will fit
                Tokenizer.trimToTokenLimit(grounding, tokensAllowed) + messageContent
            }
        }

        if (userContent.isNullOrEmpty()) {
            messageContent = "" + functionCall?.name ?: "" + functionCall?.arguments ?: ""
        }
        var messageTokens = Tokenizer.countTokensIn(messageContent)

        return messageTokens
    }

    /**
     * Whether this message can fit within the token limit specified
     */
    fun canFitInTokenLimit(includeGrounding: Boolean = true, tokensAllowed: Int = -1): Boolean {
        if (tokensAllowed < 0) return true
        return getTokenCount(includeGrounding, tokensAllowed) <= tokensAllowed
    }

    /**
     * Create `ChatMessage` instance to add to completion request
     */
    @OptIn(BetaOpenAI::class)
    fun getChatMessage (includeGrounding: Boolean = true, tokensAllowed: Int = -1) : ChatMessage {
        var content = userContent
        if (includeGrounding && grounding != null) {
            content = if (tokensAllowed < 0) {
                grounding + userContent
            } else {
                // only include as much of the grounding as will fit
                // allow for user query length in max allowed
                val maxTokens = tokensAllowed - Tokenizer.countTokensIn(userContent)
                // TODO: preserve leading and trailing grounding instructions
                Tokenizer.trimToTokenLimit(grounding, maxTokens) + "\n\n" + userContent
            }
        }
        return ChatMessage(role = role, content = content, name = name, functionCall = functionCall)
    }
}