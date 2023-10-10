package com.example.compose.jetchat

import android.util.Log
import com.aallam.openai.api.chat.Parameters
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
 * https://github.com/aallam/openai-kotlin/blob/main/guides/ChatFunctionCall.md
 *
 * NOTE: Other functions have been refactored into the /functions/
 * folder (eg. `AskWikipediaFunction`) but this code is left here to
 * match the public documentation (blog post) about it. */
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
         * Makes two HTTPS calls to determine the correct location
         * and then get weather data from
         * https://www.weather.gov/documentation/services-web-api
         *
         * @return WARNING: returns fake/random data if there's an
         * error because this is primarily a demo. If you see
         * "moonquakes" in the UI then the hardcoded data was used.
         */
        suspend fun currentWeather(latitude: String, longitude: String, unit: String): String {

            try {
                val httpClient = HttpClient()
                // https://www.weather.gov/documentation/services-web-api
                val gridUrl = "https://api.weather.gov/points/$latitude,$longitude"
                Log.i("LLM", "Grid URL $gridUrl")

                val gridResponse = httpClient.get(gridUrl) {
                    contentType(ContentType.Application.Json)
                    headers{
                        append(HttpHeaders.UserAgent,Constants.WEATHER_USER_AGENT)
                    }
                }
                if (gridResponse.status == HttpStatusCode.OK) {
                    val responseText = gridResponse.bodyAsText()
                    val responseJson =
                        Json.parseToJsonElement(responseText).jsonObject["properties"]!!
                    var office = responseJson.jsonObject["gridId"]?.jsonPrimitive?.content
                    var gridX = responseJson.jsonObject["gridX"]?.jsonPrimitive?.content
                    var gridY = responseJson.jsonObject["gridY"]?.jsonPrimitive?.content

                    val forecastUrl =
                        "https://api.weather.gov/gridpoints/$office/$gridX,$gridY/forecast"
                    Log.i("LLM", "Forecast URL $forecastUrl")

                    val forecastResponse = httpClient.get(forecastUrl) {
                        contentType(ContentType.Application.Json)
                        headers{
                            append(HttpHeaders.UserAgent,Constants.WEATHER_USER_AGENT)
                        }
                    }
                    if (forecastResponse.status == HttpStatusCode.OK) {
                        val responseText = forecastResponse.bodyAsText()
                        val responseJson =
                            Json.parseToJsonElement(responseText).jsonObject["properties"]!!
                        val periods = responseJson.jsonObject["periods"]!!
                        val period1 = periods.jsonArray[0]!!
                        var name = period1.jsonObject["name"]?.jsonPrimitive?.content!!
                        var temperature =
                            period1.jsonObject["temperature"]?.jsonPrimitive?.content!!
                        var unit = period1.jsonObject["temperatureUnit"]?.jsonPrimitive?.content!!
                        var detailedForecast =
                            period1.jsonObject["detailedForecast"]?.jsonPrimitive?.content!!

                        Log.i("LLM", "Forecast $detailedForecast")
                        val weatherInfo = WeatherInfo(
                            latitude,
                            longitude,
                            temperature,
                            unit,
                            listOf(name, detailedForecast)
                        )
                        return weatherInfo.toJson()
                    } else {
                        Log.e("LLM", "Http error: Forecast ${forecastResponse.status}")
                    }
                } else if (gridResponse.status == HttpStatusCode.Forbidden) { // 403
                    Log.e("LLM", "Http error: ${gridResponse.status} Grid URL access denied, probably need a string in the `WEATHER_USER_AGENT` in `Constants.kt` (don't leave it blank)")
                } else if (gridResponse.status == HttpStatusCode.NotFound) { // 404
                    Log.e("LLM", "Http error: ${gridResponse.status} Grid URL location was not within the United States, not served by weather.gov")
                } else {
                    Log.e("LLM", "Http error: ${gridResponse.status} Grid URL unexpected error")
                }
            } catch (e: Exception) {
                Log.e("LLM", "Error: ${e.message} in `currentWeather` function")
            }
            // HACK: if it gets here, return hardcoded return value (with "moonquakes")
            return hardcodedWeatherResponse(latitude, longitude, unit).toJson()
        }


        fun currentWeatherParams(): Parameters {
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

        /** Hardcoded response to ensure demo works - includes
         * a mention of "moonquakes" so if you see that in the
         * app while testing, you know it was this hardcoded response */
        private fun hardcodedWeatherResponse(
            latitude: String,
            longitude: String,
            unit: String
        ): WeatherInfo {
            var forecast = listOf("moonquakes")
            when (Random.nextInt(0, 4)) {
                0 -> forecast = listOf("sunny", "foggy", "moonquakes")
                1 -> forecast = listOf("cloudy", "rainy", "moonquakes")
                2 -> forecast = listOf("rainy", "stormy", "moonquakes")
                3 -> forecast = listOf("sunny", "foggy", "moonquakes")
                else -> {
                    forecast = listOf("clear", "sunny", "moonquakes")
                }
            }
            Log.i("LLM", "WeatherInfo $latitude, $longitude")
            return WeatherInfo(
                latitude,
                longitude,
                Random.nextInt(55, 85).toString(),
                unit,
                forecast
            )
        }
    }
}