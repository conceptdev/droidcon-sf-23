package com.example.compose.jetchat

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
        val location: String,
        val temperature: String,
        val unit: String,
        val forecast: List<String>
    ) {
        fun toJson () : String {
            return "{location:\"$location\",temperature:\"$temperature\",unit:\"$unit\",forecast:\"$forecast\"}"
        }
    }

    companion object {
        /**
         * Example dummy function hard coded to return the same weather
         * In production, this could be your backend API or an external API
         */
        fun currentWeather(location: String, unit: String): String {
            val weatherInfo = WeatherInfo(location, Random.nextInt(55, 85).toString(), unit, listOf("sunny", "foggy"))
            // HACK: hardcoded value
            // TODO: add a weather service backend
            //return Json.encodeToString(weatherInfo)
            return weatherInfo.toJson()
        }

        fun buildParams(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("location") {
                        put("type", "string")
                        put("description", "The city and state, e.g. San Francisco, CA")
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
                    add("location")
                }
            }
            return params
        }
    }
}