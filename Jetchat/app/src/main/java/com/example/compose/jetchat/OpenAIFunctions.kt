package com.example.compose.jetchat

import android.util.Log
import com.aallam.openai.api.chat.Parameters
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.put
import kotlinx.serialization.json.add
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlin.random.Random

/* References:
* Blog announcement
* https://openai.com/blog/function-calling-and-other-api-updates
* OpenAI docs
* https://platform.openai.com/docs/guides/gpt/function-calling
* Detailed Python example
* https://github.com/openai/openai-cookbook/blob/main/examples/How_to_call_functions_with_chat_models.ipynb
*/
/** Helper class for chat-function-related code
 * based on the Kotlin example
 * https://github.com/aallam/openai-kotlin/blob/main/guides/ChatFunctionCall.md */
class OpenAIFunctions {

    @Serializable
    data class WeatherInfo(
        val latitude: String,
        val longitude: String,
        val temperature: String,
        val unit: String,
        val forecast: List<String>
    ) {
        fun toJson () : String {
            return "{latitude:\"$latitude\",longitude:\"$longitude\",temperature:\"$temperature\",unit:\"$unit\",forecast:\"$forecast\"}"
        }
    }

    companion object {
        /**
         * Example dummy function hard coded to return the same weather
         * In production, this could be your backend API or an external API
         */
        fun currentWeather(latitude: String, longitude: String, unit: String): String {
            if (true)
            {   // hardcoded return value
                var forecast = listOf("clear","sunny")
                when (Random.nextInt(0,4))
                {
                    0 -> forecast = listOf("sunny","foggy")
                    1 -> forecast = listOf("cloudy","rainy")
                    2 -> forecast = listOf("rainy","stormy")
                    3 -> forecast = listOf("sunny","foggy")
                    else -> {
                        forecast = listOf("clear","sunny")
                    }
                }
                Log.i("GPT", "WeatherInfo $latitude, $longitude")
                return WeatherInfo(latitude, longitude, Random.nextInt(55, 85).toString(), unit, forecast).toJson()
            }

            val weatherInfo = WeatherInfo(latitude, longitude, Random.nextInt(55, 85).toString(), unit, listOf("sunny", "foggy"))
            // HACK: hardcoded value
            // TODO: add a weather service backend
            //return Json.encodeToString(weatherInfo)
            return weatherInfo.toJson()
        }

        fun buildParams(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("latitude") {
                        put("type", "string")
                        put("description", "The latitude of the requested location, e.g. 37.773972 for San Francisco, CA")
                    }
                    putJsonObject("longitude") {
                        put("type", "string")
                        put("description", "The longitude of the requested location, e.g. -122.431297 for San Francisco, CA")
                    }
                    putJsonObject("unit") {
                        put("type", "string")
                        putJsonArray("enum") {
                            add("celsius")
                            add("fahrenheit")
                        }
                    }
                }
                putJsonArray("required") {
                    add("latitude")
                    add("longitude")
                }
            }
            return params
        }
    }
}