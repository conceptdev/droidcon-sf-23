package com.example.compose.jetchat.functions

import android.util.Log
import com.aallam.openai.api.chat.Parameters
import com.example.compose.jetchat.Constants
import com.example.compose.jetchat.data.DroidconDbHelper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

/** Function that extracts intent from a user-query and searches
 *  Wikipedia to answer questions with up-to-date information
 *  beyond the model's training data */
class AskWikipediaFunction {
    companion object {
        /** `askWikipedia` */
        fun name(): String {
            return "askWikipedia"
        }

        fun description(): String {
            return "Answer user questions by querying the Wikipedia website. Don't call this function if you can answer from your training data."
        }

        fun params(): Parameters {
            val params = Parameters.buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("query") {
                        put("type", "string")
                        put("description", "The search term to query on the wikipedia website. Extract the subject from the sentence or phrase to use as the search term.")
                    }
                }
                putJsonArray("required") {
                    add("query")
                }
            }
            return params
        }

        /**
         * Execute arbitrary queries against Wikipedia
         */
        suspend fun function(query: String): String {
            Log.i("LLM", "askWikipedia ($query)")

            var wikipediaTitle = ""
            var wikipediaText = ""
            try {
                val httpClient = HttpClient()
                // https://en.wikipedia.org/w/api.php?action=opensearch&search=polarbear&limit=1&namespace=0&format=json
                val wikiSearchUrl = "https://en.wikipedia.org/w/api.php?action=opensearch&search=$query&limit=1&namespace=0&format=json"
                Log.i("LLM-WK", "Search JSON result: $wikiSearchUrl")
                val wikiSearchResponse = httpClient.get(wikiSearchUrl) {
                    contentType(ContentType.Application.Json)
                    headers{
                        append(HttpHeaders.UserAgent,Constants.WIKIPEDIA_USER_AGENT)
                    }
                }
                if (wikiSearchResponse.status == HttpStatusCode.OK) {
                    val responseText = wikiSearchResponse.bodyAsText()
                    Log.i("LLM", "askWikipedia search: $responseText")
                    val responseJsonElement = Json.parseToJsonElement(responseText)
                    // parse JSON like this:
                    // ["platypus",["Platypus"],[""],["https://en.wikipedia.org/wiki/Platypus"]]
                    if (responseJsonElement is JsonArray) {
                        //val searchTerm = responseJsonElement[0].jsonPrimitive.content
                        val suggestedTerm = responseJsonElement[1].jsonArray[0].jsonPrimitive.content
                        //val anotherValue = responseJsonElement[2].jsonArray // This one appears to be empty in the example
                        //val link = responseJsonElement[3].jsonArray[0].jsonPrimitive.content

                        wikipediaTitle = suggestedTerm
                    }
                }

                // https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exsentences=10&exlimit=1&titles=Polar_bear&explaintext=1&format=json
                val wikiTitleUrl =
                    "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exsentences=10&exlimit=1&titles=$wikipediaTitle&explaintext=1&format=json"
                Log.i("LLM", "Wikipedia Title URL $wikiTitleUrl")

                val wikiTitleResponse = httpClient.get(wikiTitleUrl) {
                    contentType(ContentType.Application.Json)
                    headers{
                        append(HttpHeaders.UserAgent,Constants.WIKIPEDIA_USER_AGENT)
                    }
                }
                if (wikiTitleResponse.status == HttpStatusCode.OK) {
                    val responseText = wikiTitleResponse.bodyAsText()
                    Log.i("LLM", "askWikipedia title: $responseText")
                    val responseJsonElement = Json.parseToJsonElement(responseText)
                    // parse JSON like this:
                    // {"batchcomplete":"","query":{"pages":{"23749":{"pageid":23749,"ns":0,"title":"Platypus","extract":"The platypus (Ornithorhynchus anatinus), sometimes referred to as the duck-billed platypus, is a semiaquatic, egg-laying mammal endemic to eastern Australia, including Tasmania..."}}}}
                    val pagesElement = responseJsonElement.jsonObject["query"]?.jsonObject?.get("pages")?.jsonObject
                    val itemPageElement = pagesElement?.values?.firstOrNull()?.jsonObject

                    //val pageId = itemPageElement?.get("pageid")?.jsonPrimitive?.int
                    //val title = itemPageElement?.get("title")?.jsonPrimitive?.content
                    val extract = itemPageElement?.get("extract")?.jsonPrimitive?.content

                    wikipediaText = extract ?: ""
                    if (wikipediaText.isNotEmpty())
                        wikipediaText = "\n\nIf the information below is used in the response, add [sourced from Wikipedia] to the end of the response.\n\n#####\n\n$wikipediaText\n\n#####"
                }
            } catch (e: Exception) {
                Log.e("LLM", "Error: ${e.message} in `askWikipedia` function")
            }
            return wikipediaText
        }
    }
}