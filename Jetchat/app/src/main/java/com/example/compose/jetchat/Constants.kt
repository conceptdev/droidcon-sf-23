package com.example.compose.jetchat

internal object Constants {
    internal const val OPENAI_TOKEN = "{OPENAI_KEY}"

    /** Chat model: "gpt-4-32k" or "gpt-3.5-turbo-16k" */
    internal const val OPENAI_CHAT_MODEL = "gpt-3.5-turbo" // 4096 tokens for Sliding Window testing

    /** Maximum token limit for model: 4,096 for "gpt-3.5-turbo"
     * (used in Sliding Window calculations) */
    internal const val OPENAI_MAX_TOKENS = 4096

    /** Embedding model: text-embedding-ada-002 */
    internal const val OPENAI_EMBED_MODEL = "text-embedding-ada-002"

    /** Hardcode date "2023-06-09" for droidcon session testing, make empty string to use real date */
    internal const val TEST_DATE = "2023-06-09"
    /** Hardcode time "15:45" for droidcon session testing, make empty string to use real time */
    internal const val TEST_TIME = "15:45"

    /** Hardcode location to "37.773972,-122.431297" for weather API testing, make empty string to use real GPS location (NOTE: not yet implemented) */
    internal const val TEST_LOCATION = "37.773972,-122.431297"

    /** Weather.gov https://www.weather.gov/documentation/services-web-api asks that
     * each application submit a unique user-agent to help with tracking and
     * security issues. Try to include a contact email address.
     * NOTE: weather.gov is for US locations only. */
    internal const val WEATHER_USER_AGENT = ""

    internal const val PALM_TOKEN = "{PALM_TOKEN}"

    /** Chat model: models/chat-bison-001 */
    internal const val PALM_CHAT_MODEL = "models/chat-bison-001"

    /** Embedding model: models/embedding-gecko-001 */
    internal const val PALM_EMBED_MODEL = "models/embedding-gecko-001"

    /** Enable or disable the speech functions (mainly for emulator debugging) */
    internal const val ENABLE_SPEECH = false
}