package com.example.compose.jetchat.functions

import android.util.Log
import com.aallam.openai.api.chat.Parameters
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class ListFavoritesFunction {
    companion object {
        fun name(): String {
            return "listFavorites"
        }

        fun description(): String {
            return "List all sessions that have been marked as favorites"
        }

        fun params(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {}
            }
            return params
        }

        fun function(): String {
            Log.i("LLM", "listFavorites ")
            return "all the sessions"
        }
    }
}