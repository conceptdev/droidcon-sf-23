package com.example.compose.jetchat.data

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.example.compose.jetchat.Constants

class SlidingWindow {
    companion object {
        /**
         * Takes the conversation history and trims older ChatMessage
         * objects (except for System messasge) from the start
         *
         * Only includes the most recent embedding, omits the additional
         * grounding information from older messages
         */
        @OptIn(BetaOpenAI::class)
        fun chatHistoryToWindow (conversation: MutableList<CustomChatMessage>): MutableList<ChatMessage> {
            Log.v("LLM-SW", "-- chatHistoryToWindow() max tokens ${Constants.OPENAI_MAX_TOKENS}")
            // set parameters for sliding window
            val tokenLimit = Constants.OPENAI_MAX_TOKENS
            /** save room for response */
            val expectedResponseSizeTokens = 500
            /** reserved for function definitions (manually calculated) */
            val reservedForFunctionsTokens = 200
            Log.v("LLM-SW", "-- tokens reserved for response $expectedResponseSizeTokens and functions $reservedForFunctionsTokens")
            var tokensUsed = 0
            var systemMessage: ChatMessage? = null
            var includeGrounding = true

            /** maximum tokens for chat, after hardcoded functions and allowing for a given response size */
            val tokenMax = tokenLimit - expectedResponseSizeTokens - reservedForFunctionsTokens

            // prepare output data structure
            var messagesInWindow = mutableListOf<ChatMessage>()

            // check for system message
            if (conversation[0].role == ChatRole.System) {
                systemMessage = conversation[0].getChatMessage()
                var systemMessageTokenCount = Tokenizer.countTokensIn(systemMessage.content)
                tokensUsed += systemMessageTokenCount
                Log.v("LLM-SW", "-- tokens used by system message: $tokensUsed")
            }

            // loop through other messages
            for (message in conversation.reversed()) {
                if (message.role != ChatRole.System) {
                    var m = CustomChatMessage(message.role, message.grounding, message.userContent, message.name, message.functionCall)

                    Log.v("LLM-SW", "-- message (${m.role.role}) ${m.summary()}")
                    Log.v("LLM-SW", "        contains tokens: ${m.getTokenCount(includeGrounding)}")
                    val tokensRemaining = tokenMax - tokensUsed
                    if ((tokensUsed + m.getTokenCount(includeGrounding)) < tokenMax) {
                        messagesInWindow.add(message.getChatMessage(includeGrounding))
                        tokensUsed += m.getTokenCount(includeGrounding)

                        if (message.role == ChatRole.User) {
                            Log.v("LLM-SW", "        added (grounding:$includeGrounding). Still available: ${tokenMax - tokensUsed}")
                            // only User messages will have grounding prepended
                            includeGrounding = false // stop subsequent user messages from including grounding
                        } else {
                            Log.v("LLM-SW", "        added. Still available: ${tokenMax - tokensUsed}")
                        }
                    } else {
                        Log.v("LLM-SW", "        NOT ADDED. Still available: ${tokenMax - tokensUsed} (inc response quota ${expectedResponseSizeTokens}) ")
                        break // could optionally keep adding subsequent, smaller messages to context up until token limit
                    }
                }
            }

            // add system message back if it existed
            if (systemMessage != null) {
                messagesInWindow.add(systemMessage)
                Log.v("LLM-SW", "System message added")
            }
            // re-order so that system message is [0]
            var orderedMessageWindow = messagesInWindow.reversed().toMutableList()

            return orderedMessageWindow
        }
    }
}