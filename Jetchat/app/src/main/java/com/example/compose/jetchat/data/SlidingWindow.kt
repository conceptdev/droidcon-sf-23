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
            for (m in conversation.reversed()) {
                if (m.role != ChatRole.System) {
                    val tokensRemaining = tokenMax - tokensUsed

                    Log.v("LLM-SW", "-- message (${m.role.role}) ${m.summary()}")
                    Log.v("LLM-SW", "        contains tokens: ${m.getTokenCount(includeGrounding, tokensRemaining)}")

                    if (m.canFitInTokenLimit(includeGrounding, tokensRemaining)) {
                        messagesInWindow.add(m.getChatMessage(includeGrounding, tokensRemaining))
                        tokensUsed += m.getTokenCount(includeGrounding, tokensRemaining)

                        if (m.role == ChatRole.User) {
                            Log.v("LLM-SW", "        added (grounding:$includeGrounding). Still available: ${tokenMax - tokensUsed}")
                            // stop subsequent user messages from including grounding
                            includeGrounding = false
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