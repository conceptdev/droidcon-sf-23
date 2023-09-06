package com.example.compose.jetchat.data

/**
 * Count tokens so that we can understand how close we are
 * to the model's maximum input token limit
 *
 * https://community.openai.com/t/what-is-the-openai-algorithm-to-calculate-tokens/58237
 *
 * TODO: investigate this tool
 * https://github.com/knuddelsgmbh/jtokkit
 * linked from
 * https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb
 */
class Tokenizer {
    companion object {
        fun countTokensIn (text: String?): Int {
            if (text == null) return 0

            var chars = text.length

            var tokens = chars / 4

            return tokens
        }

        /**
         * Trim the input text to be under the number of tokens specified
         *
         * @return substring of the text that's no longer than the
         * specified number of tokens
         */
        fun trimToTokenLimit (text: String?, tokenLimit: Int): String? {
            // TODO: limit by tokens instead of the rough character approximation
            val charLimit = tokenLimit * 4
            val length = text?.length
            return if (length != null && length <= charLimit) {
                text
            } else {
                text?.substring(0, charLimit)
            }
        }
    }
}