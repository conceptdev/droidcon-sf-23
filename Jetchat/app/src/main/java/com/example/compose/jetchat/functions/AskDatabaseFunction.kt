package com.example.compose.jetchat.functions

import android.database.Cursor.FIELD_TYPE_INTEGER
import android.util.Log
import com.aallam.openai.api.chat.Parameters
import com.example.compose.jetchat.data.DroidconDbHelper
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/* Inspired by OpenAI example
* https://github.com/openai/openai-cookbook/blob/main/examples/How_to_call_functions_with_chat_models.ipynb
*
* Uses
* https://developer.android.com/training/data-storage/sqlite
* */
/** Function that exposes conference database schema and lets the model query it
 * directly with SQL and then parse the results to answer questions */
class AskDatabaseFunction {
    companion object {
        fun name(): String {
            return "askDatabase"
        }

        fun description(): String {
            return "Answer user questions about conference sessions like what room sessions are presented in. Output should be a fully formed SQL query."
        }

        fun params(db: DroidconDbHelper): Parameters {
            val schema = db.generateSimpleSchema()
            //Log.v("LLM", "params db schema:\n$schema")
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("query") {
                        put("type", "string")
                        put(
                            "description", """
                            SQL query extracting info to answer the user's question.
                            SQL should be written using this database schema:
                            
                            $schema
                            
                            The query should be returned in plain text, not in JSON.
                            """.trimIndent()
                        )
                    }
                }
                putJsonArray("required") {
                    add("query")
                }
            }
            return params
        }
        /** hardcoded demo schema to test model's comprehension */
        @Deprecated("Method used for prototyping and observing the model's responses to schema changes")
        fun paramsTest(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("query") {
                        put("type", "string")
                        put(
                            "description", """
                            SQL query extracting info to answer the user's question.
                            SQL should be written using this database schema:
                            
                            Table: sessions
                            Columns: session_id, speaker, role, location_id, date, time, subject, description
                            
                            Table: favorites
                            Columns: session_id, is_favorite
                            
                            Table: locations
                            Columns: location_id, directions
                            
                            The query should be returned in plain text, not in JSON.
                            """.trimIndent()
                        )
                    }
                }
                putJsonArray("required") {
                    add("query")
                }
            }
            return params
        }

        /**
         * Execute arbitrary queries against a local database
         */
        fun function(dbHelper: DroidconDbHelper, query: String): String {
            Log.i("LLM", "askDatabase ($query)")

            val db = dbHelper.readableDatabase
            val cursor = db.rawQuery(query,null)
            var rowCount = 0
            var out = ""
            var needOuterComma = false
            with(cursor) {
                // mimicking the output format from the OpenAI Cookbook
                // eg. [('abc', 1),('def', 2)]
                out += "["
                var needComma = false
                while (moveToNext()) {
                    if (needOuterComma) out += "," else needOuterComma = true
                    out += "("
                    for (i in 0 until cursor.columnCount) {
                        if (needComma) out += "," else needComma = true
                        out += when (getType(i)) {
                            FIELD_TYPE_INTEGER ->
                                getString(i)
                            else -> {
                                "'${getString(i)}'"
                            }
                        }
                    }
                    out += ")\n"
                    rowCount++
                }
                out += "]"
                Log.i("LLM", "askDatabase rowCount: $rowCount")
                Log.v("LLM", "            $out")
            }
            cursor.close()

            return if (out == "") {
                Log.i("LLM", "askDatabase rowCount: 0")
                "0 rows affected"
            } else {
                out
            }
        }
    }
}