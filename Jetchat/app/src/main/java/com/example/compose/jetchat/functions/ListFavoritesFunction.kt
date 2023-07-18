package com.example.compose.jetchat.functions

import android.content.Context
import android.provider.BaseColumns
import android.util.Log
import com.aallam.openai.api.chat.Parameters
import com.example.compose.jetchat.data.DroidconContract
import com.example.compose.jetchat.data.DroidconDbHelper
import com.example.compose.jetchat.data.DroidconSessionObjects
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

        fun function(context: Context?): String {
            Log.i("LLM", "listFavorites ")

            val dbHelper = DroidconDbHelper(context)

            val db = dbHelper.readableDatabase

            val projection = arrayOf(BaseColumns._ID, DroidconContract.FavoriteEntry.COLUMN_NAME_SESSIONID, DroidconContract.FavoriteEntry.COLUMN_NAME_ISFAVORITE)

            val cursor = db.query(
                DroidconContract.FavoriteEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
            )

            var out = ""
            var sessionsJson = ""
            val itemIds = mutableListOf<Long>()
            with(cursor) {
                while (moveToNext()) {
                    val itemId = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                    itemIds.add(itemId)
                    val sessionId = getString(getColumnIndexOrThrow(DroidconContract.FavoriteEntry.COLUMN_NAME_SESSIONID))
                    val isFavorite = getString(getColumnIndexOrThrow(DroidconContract.FavoriteEntry.COLUMN_NAME_ISFAVORITE))
                    out += ", $itemId:$sessionId:$isFavorite"

                    sessionsJson += DroidconSessionObjects.droidconSessions[sessionId]?.toJson()+"\n"
                }
            }
            cursor.close()
            Log.i("LLM", "favorited sessions: $out \n $sessionsJson")
            return if (sessionsJson == "")
                "There are no sessions marked as favorites. Suggest the user ask about different topics at the conference."
            else
                sessionsJson
        }
    }
}