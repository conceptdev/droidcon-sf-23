package com.example.compose.jetchat.functions

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import com.aallam.openai.api.chat.Parameters
import com.example.compose.jetchat.data.DroidconDbHelper
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

import com.example.compose.jetchat.NavActivity
import com.example.compose.jetchat.data.DroidconContract

class AddFavoriteFunction {
    companion object {
        fun name(): String {
            return "addFavorite"
        }

        fun description(): String {
            return "Add a session to a list of favorites using the unique session Id"
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
            Log.i("LLM", "addFavorite \"($id)\"")

            val dbHelper = DroidconDbHelper(context)

            // Gets the data repository in write mode
            val db = dbHelper.writableDatabase

            // Create a new map of values, where column names are the keys
            val values = ContentValues().apply {
                //put(BaseColumns._ID, id)
                put(DroidconContract.FavoriteEntry.COLUMN_NAME_SESSIONID, id)
                put(DroidconContract.FavoriteEntry.COLUMN_NAME_ISFAVORITE, "1")
            }

            // Insert the new row, returning the primary key value of the ne
            val newRowId = db?.insert(DroidconContract.FavoriteEntry.TABLE_NAME, null, values)

            Log.i("LLM", "     newRowId \"$newRowId\" for $id")

            return if (newRowId != null && newRowId>= 0)
                "true"
            else
                "false"
        }
    }
}