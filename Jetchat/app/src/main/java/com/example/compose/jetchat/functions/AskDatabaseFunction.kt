package com.example.compose.jetchat.functions

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

        fun params(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("query") {
                        put("type", "string")
                        put(
                            // TODO: dynamically generate schema from database
                            "description", """
                            SQL query extracting info to answer the user's question.
                            SQL should be written using this database schema:
                            
                            Table: sessions
                            Columns: session_id, speaker, role, location_id, date, time, subject, description
                            
                            Table: favorites
                            Columns: session_id, is_favorite
                            
                            Table: locations
                            Columns: location_id, name, description
                            
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
        fun params_test(): Parameters {
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
                            Columns: location_id, name, description
                            
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

            // TODO: implement database interaction

            return "EXECUTE $query"
        }
    }
}