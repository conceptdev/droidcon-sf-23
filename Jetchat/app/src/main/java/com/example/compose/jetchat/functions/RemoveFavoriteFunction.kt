package com.example.compose.jetchat.functions

import android.content.Context
import android.util.Log
import com.aallam.openai.api.chat.Parameters
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class RemoveFavoriteFunction {
    companion object {
        fun name(): String {
            return "removeFavorite"
        }

        fun description(): String {
            return "Remove a session from the list of favorites using the unique session Id"
        }

        fun params(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("id") {
                        put("type", "string")
                        put("description", "Unique session Id")
                    }
                }
                putJsonArray("required") {
                    add("id")
                }
            }
            return params
        }

        fun function(context: Context?, id: String): String {
            Log.i("LLM", "removeFavorite ($id)")
            return if (id != "")
                "true"
            else
                "false"
        }
    }
}