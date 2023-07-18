package com.example.compose.jetchat.functions

import android.util.Log
import com.aallam.openai.api.chat.Parameters
import com.example.compose.jetchat.data.SessionInfo
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.apache.http.annotation.Obsolete

/** This would work okay for a 'keyword search' but it
 * makes more sense to use the embeddings and not
 * have this function exposed to the prompt
 *
 * Not used but provided for reference */
@Obsolete
class TimeForSessionFunctions {
    companion object {
        fun name(): String {
            return "timeForSessions"
        }
        fun description() : String {
            return "Find sessions that match the subject and list the time and location they start"
        }
        fun params(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("subject") {
                        put("type", "string")
                        put("description", "Keywords that identify one or more sessions")
                    }
                }
                putJsonArray("required") {
                    add("subject")
                }
            }
            return params
        }

        /**
         * Filter sessions by a keyword (why is this different to embedding?)
         */
        fun function(subject: String): String {
            Log.i("LLM", "timeForSessions ($subject)")
            val list = SessionInfo.hardcodedSessionsList()
            var out: String = ""
            for (session in list)
            {
                // TODO: replace with a real search
                if (session.subject.indexOf(subject, 0, true) >= 0||
                    session.description.indexOf(subject, 0, true) >= 0) {
                    out += session.toJson() + "\n"
                }
            }
            return out
        }
    }
}