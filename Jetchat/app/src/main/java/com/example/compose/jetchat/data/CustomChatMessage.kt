package com.example.compose.jetchat.data

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole

class CustomChatMessage @OptIn(BetaOpenAI::class) constructor(
    public val role: ChatRole,
    public val grounding: String? = null,
    public val userContent: String? = null,
    public val name: String? = null
    ) {
    var tokenCount = 0
    var charCount = 0

    @OptIn(BetaOpenAI::class)
    public fun getChatMessage () : ChatMessage {
        val content = grounding + userContent

        charCount = content.length
        tokenCount = charCount / 4 // TODO: implement tokenizer one day

        return ChatMessage(role = role, content = content, name = name)
    }
}