package com.example.compose.jetchat

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.aallam.openai.api.chat.Parameters
import com.example.compose.jetchat.data.DroidconSessionObjects
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.put
import kotlinx.serialization.json.add
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class SessionsByTimeFunction {
    companion object {
        fun name(): String {
            return "sessionsByTime"
        }
        fun description(): String {
            return "Given a date and time or time range, return the sessions that start on that date, during that time."
        }
        fun params(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("date") {
                        put("type", "string")
                        put("description", "Date that the conference sessions occur, eg 2023-06-08")
                    }
                    putJsonObject("earliestTime") {
                        put("type", "string")
                        put("description", "The earliest time that the conference sessions might start, eg. 09:00. Defaults to the current time.")
                    }
                    putJsonObject("latestTime") {
                        put("type", "string")
                        put("description", "The latest time that the conference sessions might start, eg. 14:00.  Defaults to one hour from current time.")
                    }
                }
                putJsonArray("required") {
                    add("date")
                }
            }
            return params
        }
        /**
         * Filter sessions by the date/time they start
         */
        @RequiresApi(Build.VERSION_CODES.O)
        fun function(date: String, earliestTime: String="08:00", latestTime: String="18:00"): String {
            Log.i("LLM", "sessionsByTime ($date, $earliestTime, $latestTime)")
            var earliestTimeCheck = earliestTime
            var latestTimeCheck = latestTime
            // Always make the start time longer than a session, to get 'current' sessions
            val earliest = LocalDateTime.parse("$date $earliestTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            val earliestLessOneHour = earliest.minusHours(1)
            earliestTimeCheck = earliestLessOneHour.format(DateTimeFormatter.ofPattern("HH:mm"))

            if (latestTimeCheck == "") { // only calc later time if blank
                val earliest = LocalDateTime.parse("$date $earliestTime", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                val earliestPlusOneHour = earliest.plusHours(1)
                latestTimeCheck = earliestPlusOneHour.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            val list = SessionInfo.hardcodedSessionsList()
            var out: String = ""
            for (session in DroidconSessionObjects.droidconSessions.values)
            {
                if (date == session.date) {
                    if (session.time in earliestTimeCheck..latestTimeCheck) {
                        out += session.toJson() + "\n"
                    }
                }
            }
            if (out == "") {
                out = "There are no sessions available."
            }
            return out
        }
    }
}

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

        fun function(id: String): String {
            Log.i("LLM", "addFavorite ($id)")
            return if (id != "")
                "true"
            else
                "false"
        }
    }
}

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

        fun function(id: String): String {
            Log.i("LLM", "removeFavorite ($id)")
            return if (id != "")
                "true"
            else
                "false"
        }
    }
}
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

/**  */
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
                out += session.toJson() + "\n"
            }
            return out
        }

    }
}


@Serializable
data class SessionInfo(
    val id: String,
    val speaker: String,
    val role: String = "",
    val location: String,
    val date: String,
    val time: String,
    val subject: String,
    val description: String = ""
) {
    fun toJson () : String {
        return "{id:\"$id\",speaker:\"$speaker\",location:\"$location\",date:\"$date\",time:\"$time\",subject:\"$subject\"}"
    }
    companion object {
        /** Hardcoded response to ensure demo works - includes
         * a mention of "moonquakes" so if you see that in the
         * app while testing, you know it was this hardcoded response */
        fun hardcodedSessionsList(
        ): List<SessionInfo> {
            var sessionList = mutableListOf<SessionInfo>()

            sessionList.add(
                SessionInfo(
                    "1",
                    "Craig Dunn",
                    "SWE",
                    "Robertson 1",
                    "2023-06-09",
                    "16:00",
                    "Android AI"
                )
            )
            sessionList.add(
                SessionInfo(
                    "2",
                    "HANSON HO",
                    "",
                    "Robertson 2",
                    "2023-06-08",
                    "13:30",
                    "Combating sampling bias in production: How to collect and interpret performance data to drive growth"
                )
            )
            sessionList.add(
                SessionInfo(
                    "3",
                    "ISTVÁN JUHOS",
                    "",
                    "Fisher West",
                    "2023-06-09",
                    "10:00",
                    "Compose-View Interop in Practice"
                )
            )

            return sessionList
        }
    }
}