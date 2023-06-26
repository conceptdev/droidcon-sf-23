package com.example.compose.jetchat

internal object Constants {
    internal const val OPENAI_TOKEN = "{OPENAI_KEY}"

    /** Chat model: gpt-3.5-turbo-0613 */
    internal const val OPENAI_CHAT_MODEL = "gpt-3.5-turbo-0613"

    /** Embedding model: text-embedding-ada-002 */
    internal const val OPENAI_EMBED_MODEL = "text-embedding-ada-002"

    internal const val PALM_TOKEN = "{PALM_TOKEN}"

    /** Chat model: models/chat-bison-001 */
    internal const val PALM_CHAT_MODEL = "models/chat-bison-001"

    /** Embedding model: models/embedding-gecko-001 */
    internal const val PALM_EMBED_MODEL = "models/embedding-gecko-001"
}