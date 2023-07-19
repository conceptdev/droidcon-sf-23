package com.example.compose.jetchat.functions

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.aallam.openai.api.chat.Parameters
import com.example.compose.jetchat.data.DroidconSessionObjects
import com.example.compose.jetchat.data.SessionInfo
import kotlinx.serialization.json.add
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
            var earliestTimeCheck = earliestTime.trim()
            if (earliestTimeCheck.isNullOrEmpty()) {
                //  model has passed empty string in the past... so make sure there's a default
                earliestTimeCheck = "08:00"
            }

            // Always make the start time longer than a session, to get 'current' sessions
            val earliest = LocalDateTime.parse("$date $earliestTimeCheck", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            val earliestLessOneHour = earliest.minusHours(1)
            earliestTimeCheck = earliestLessOneHour.format(DateTimeFormatter.ofPattern("HH:mm"))

            var latestTimeCheck = latestTime
            if (latestTimeCheck.isNullOrEmpty()) {
                // Calc latest time if blank, based on earliestTime plus one hour
                val earliestPlusOneHour = earliest.plusHours(1)
                latestTimeCheck = earliestPlusOneHour.format(DateTimeFormatter.ofPattern("HH:mm"))
            }

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
                out = "There are no sessions available. Remind the user about the dates and times the conference is open."
            }
            return out
        }
    }
}