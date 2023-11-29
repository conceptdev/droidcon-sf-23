package com.example.compose.jetchat

import com.knuddels.jtokkit.api.EncodingType
import com.knuddels.jtokkit.api.ModelType

internal object Constants {
    internal const val OPENAI_TOKEN = "{OPENAI_KEY}"

    /** Chat model: "gpt-4-32k" or "gpt-3.5-turbo-16k" */
    internal const val OPENAI_CHAT_MODEL = "gpt-4-1106-preview"

    /** Image model: "dall-e-3" or "dall-e-2" */
    internal const val OPENAI_IMAGE_MODEL = "dall-e-3"

    /** Tokenizer model: GPT_4_32K or GPT_3_5_TURBO_16K */
    internal val OPENAI_CHAT_TOKENIZER_MODEL = ModelType.GPT_4_32K // to match above

    /** Completion model: text-davinci-003 */
    internal const val OPENAI_COMPLETION_MODEL = "text-davinci-003"

    /** Maximum token limit for model: 4,096 for "gpt-3.5-turbo"
     * 16,384 for gpt-3.5-turbo-16k
     * 32,768 for gpt-4-32k
     * (used in Sliding Window calculations) */
    internal const val OPENAI_MAX_TOKENS = 32768

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

    /** wikipedia.org https://www.mediawiki.org/wiki/API:Etiquette asks that
     * each application submit a unique user-agent to help with tracking and
     * security issues. Try to include a contact email address.
     */
    internal const val WIKIPEDIA_USER_AGENT = ""

    internal const val PALM_TOKEN = "{PALM_TOKEN}"

    /** Chat model: models/chat-bison-001 */
    internal const val PALM_CHAT_MODEL = "models/chat-bison-001"

    /** Embedding model: models/embedding-gecko-001 */
    internal const val PALM_EMBED_MODEL = "models/embedding-gecko-001"

    /** Enable or disable the speech functions (mainly for emulator debugging) */
    internal const val ENABLE_SPEECH = false
}