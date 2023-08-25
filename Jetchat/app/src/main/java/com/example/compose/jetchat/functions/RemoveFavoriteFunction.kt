package com.example.compose.jetchat.functions

import android.content.Context
import android.util.Log
import com.aallam.openai.api.chat.Parameters
import com.example.compose.jetchat.data.DroidconContract
import com.example.compose.jetchat.data.DroidconDbHelper
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

        fun function(dbHelper: DroidconDbHelper, id: String): String {
            Log.i("LLM", "removeFavorite ($id)")

            // Gets the data repository in write mode
            val db = dbHelper.writableDatabase

            // Define 'where' part of query.
            val selection = "${DroidconContract.FavoriteEntry.COLUMN_NAME_SESSIONID} LIKE ?"
            // Specify arguments in placeholder order.
            val selectionArgs = arrayOf(id)
            // Issue SQL statement.
            val deletedRows = db.delete(DroidconContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs)

            return if (deletedRows >= 0)
                "true"
            else
                "false"
        }
    }
}