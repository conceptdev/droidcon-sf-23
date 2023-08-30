package com.example.compose.jetchat.data

/**
 *
 * https://community.openai.com/t/what-is-the-openai-algorithm-to-calculate-tokens/58237
 */
class Tokenizer {
    companion object {
        fun countTokensIn (text: String?): Int {
            if (text == null) return 0

            var chars = text.length

            var tokens = chars / 4

            return tokens
        }
    }
}