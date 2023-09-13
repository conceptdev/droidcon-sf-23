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
        suspend fun chatHistoryToWindow (conversation: MutableList<CustomChatMessage>): MutableList<ChatMessage> {
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

            var messagesForSummarization = ""

            // check for system message
            if (conversation[0].role == ChatRole.System) {
                systemMessage = conversation[0].getChatMessage()
                var systemMessageTokenCount = Tokenizer.countTokensIn(systemMessage.content)
                tokensUsed += systemMessageTokenCount
                Log.v("LLM-SW", "-- tokens used by system message: $tokensUsed")
            }

            // loop through messages
            // 1. add to window until full
            // 2. then add to summarize list
            var addToWindow = true
            var addToSummary = false
            for (m in conversation.reversed()) {
                if (m.role != ChatRole.System) {
                    val tokensRemaining = tokenMax - tokensUsed

                    Log.v("LLM-SW", "-- message (${m.role.role}) ${m.summary()}")
                    Log.v("LLM-SW", "        contains tokens: ${m.getTokenCount(includeGrounding, tokensRemaining)}")



                    if (addToWindow && m.canFitInTokenLimit(includeGrounding, tokensRemaining)) {
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
                        Log.v("LLM-SW", "        NOT ADDED TO WINDOW, SUMMARIZE INSTEAD. Still available: ${tokenMax - tokensUsed} (inc response quota ${expectedResponseSizeTokens}) ")
                        addToSummary = true
                        messagesForSummarization = "${m.role.role.uppercase()}: ${m.getChatMessage(false).content}\n\n" + messagesForSummarization // we're parsing in reverse
                    }
                    addToWindow = !addToSummary
                }
            }

            messagesForSummarization = """USER: are there any jetpack compose sessions

ASSISTANT: Yes, there are several Jetpack Compose sessions at the droidcon SF conference:

1. Speaker: VINAY GABA
Role: Android @ Airbnb
Subject: Panel Discussion: Adopting Jetpack Compose @ Scale
Date: 2023-06-08
Time: 16:50
Location: Robertson 1

2. Speaker: NICK DIPATRI
Role: Comcast Principal Engineer
Subject: Practical Compose Navigation with a Red Siren
Date: 2023-06-09
Time: 15:35
Location: Fisher West

3. Speaker: ALEJANDRO SANCHEZ
Role: Android @ Uber
Subject: Panel Discussion: Adopting Jetpack Compose @ Scale
Date: 2023-06-08
Time: 16:50
Location: Robertson 1

4. Speaker: TASHA RAMESH
Role: Android @ Tinder
Subject: Creative Coding with Compose: The Next Chapter
Date: 2023-06-08
Time: 10:20
Location: Robertson 1

5. Speaker: zach klippenstein
Role: Composer at Google
Subject: Opening the shutter on snapshots
Date: 2023-06-09
Time: 10:00
Location: Robertson 2

USER: are there any gradle sessions

ASSISTANT: Yes, there are several Gradle sessions at the droidcon SF conference:

1. Speaker: NELSON OSACKY
Role: Solutions Engineer at Gradle
Subject: Improving Developer Experience with Gradle Build Scans
Date: 2023-06-08
Time: 13:30
Location: Robertson 1

2. Speaker: IURY SOUZA
Role: Mostly Android things @ Klarna
Subject: Crash Course in building your First Gradle Plugin
Date: 2023-06-08
Time: 13:30
Location: Fisher West

3. Speaker: AURIMAS LIUTIKAS
Role: Software Engineer at Google / Gradle Fellow
Subject: Gradle under a microscope: profiling and optimizing builds
Date: 2023-06-09
Time: 11:25
Location: Fisher West"""

            Log.v("LLM-TEST", "messagesForSummarization: $messagesForSummarization")
            val history = SummarizeHistory.summarize(messagesForSummarization)

            val historyContext = """These sessions have been discussed previously:
                 
                $history
                
                Only use this information if requested.""".trimIndent()
            // We can add the history in a few places:
            // 1. system message
            // 2. first message 'grounding'
            // 3. last message 'grounding'


            // set system message
            if (systemMessage == null) {
                // add history as the 'system message' if it exists
                messagesInWindow.add(
                    ChatMessage(
                        role = ChatRole.System,
                        content = historyContext
                    )
                )
                Log.v("LLM-SW", "History added as system message")
            }
            else
            {
                if (history.isNullOrEmpty()) {
                    messagesInWindow.add(systemMessage)
                } else {
                    // combine system message with history
                    messagesInWindow.add(
                        ChatMessage(
                            role = ChatRole.System,
                            content = (systemMessage.content + "\n\n" + historyContext)
                        )
                    )
                }
                Log.v("LLM-SW", "System message added")
            }
            // re-order so that system message is [0]
            var orderedMessageWindow = messagesInWindow.reversed().toMutableList()

            return orderedMessageWindow
        }
    }
}