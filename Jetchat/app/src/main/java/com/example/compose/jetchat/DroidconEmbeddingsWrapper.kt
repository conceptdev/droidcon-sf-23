package com.example.compose.jetchat

import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.compose.jetchat.data.DroidconSessionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import java.lang.StringBuilder
import java.util.SortedMap

/** dot product for comparing vector similarity */
infix fun DoubleArray.dot(other: DoubleArray): Double {
    var out = 0.0
    for (i in indices) out += this[i] * other[i]
    return out
}

/** THIS IS A COPY OF OpenAIWrapper
 *
 * Adds embeddings to allow grounding in droidcon SF 2023 conference data
 * */
@OptIn(BetaOpenAI::class)
class DroidconEmbeddingsWrapper {
    private val openAIToken: String = "{OPENAI-KEY}"
    private var conversation: MutableList<ChatMessage>
    private var openAI: OpenAI = OpenAI(openAIToken)

    init {
        conversation = mutableListOf(
            ChatMessage(
                role = ChatRole.System,
                content = """You are a personal assistant called JetchatAI. 
                    You will answer questions about the speakers and sessions at the droidcon SF conference.
                    The conference is on June 8th and 9th, 2023. It starts at 9am and finishes by 6pm.
                    Your answers will be short and concise, since they will be required to fit on 
                    a mobile device display.""".trimMargin()
            )
        )
    }

    suspend fun chat(message: String): String {

        initVectorCache() // should only run once (HACK: wait to finish)

        val messagePreamble = grounding(message)

        // add the user's message to the chat history
        conversation.add(
            ChatMessage(
                role = ChatRole.User,
                content = messagePreamble + message
            )
        )

        // build the OpenAI network request
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = conversation
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)

        // extract the response to show in the app
        val chatResponse = completion.choices[0].message?.content ?: ""

        // add the response to the conversation history
        conversation.add(
            ChatMessage(
                role = ChatRole.Assistant,
                content = chatResponse
            )
        )

        return chatResponse
    }

    /** Provide grounding for user query by checking
     * message against embeddings.
     *
     * @return the relevant data to add to the query,
     * along with additional prompt instructions.
     * Empty string if no matching embeddings found. */
    private suspend fun grounding(message: String): String {
        var messagePreamble = ""
        val embeddingRequest = EmbeddingRequest(
            model = ModelId("text-embedding-ada-002"),
            input = listOf(message)
        )
        val embedding = openAI.embeddings(embeddingRequest)
        val messageVector = embedding.embeddings[0].embedding.toDoubleArray()
        Log.i("LLM", "messageVector: $messageVector")

        var sortedVectors: SortedMap<Double, String> = sortedMapOf()
        // find the best match sessions
        for (session in vectorCache) {
            val v = messageVector dot session.value
            sortedVectors[v] = session.key
            Log.v("LLM", "${session.key} dot $v")
        }
        if (sortedVectors.lastKey() > 0.8) { // arbitrary match threshold
            Log.i("LLM", "Top match is ${sortedVectors.lastKey()}")

            messagePreamble =
                "Following are some talks/sessions scheduled for the droidcon San Francisco conference in June 2023:\n\n"
            for (dpKey in sortedVectors.tailMap(0.8)) {
                Log.i("LLM", "${dpKey.key} -> ${dpKey.value}")

                messagePreamble += DroidconSessionData.droidconSessions[dpKey.value] + "\n\n"

            }
            messagePreamble += "\n\nUse the above information to answer the following question. Summarize and provide date/time and location if appropriate.\n\n"
            Log.v("LLM", "$messagePreamble")
        }
        return messagePreamble
    }

    suspend fun imageURL(prompt: String): String {
        val imageRequest = ImageCreation(prompt)

        // OpenAI network request
        val images: List<ImageURL> = openAI.imageURL(imageRequest)

        return if (images.isEmpty()) "" else images[0].url
    }

    /** key'd map of session to vector */
    private var vectorCache: MutableMap<String, DoubleArray> = mutableMapOf()

    /** make embedding requests for each session, populate vectorCache */
    suspend fun initVectorCache () {
        if (vectorCache.isEmpty()) {
            // to emit as file
            var fileOut = StringBuilder()
            fileOut.append("""package com.example.compose.jetchat.data

/** GENERATED CODE: do not edit */
class DroidconSessionVectors {
    companion object {
        var vectorCache: Map<String, DoubleArray> = mapOf(""")

            // embedding API calls
            for (session in DroidconSessionData.droidconSessions) {
                val embeddingRequest = EmbeddingRequest(
                    model = ModelId("text-embedding-ada-002"),
                    input = listOf(session.value)
                )
                val embedding = openAI.embeddings(embeddingRequest)
                val vector = embedding.embeddings[0].embedding.toDoubleArray()
                vectorCache[session.key] = vector

                // emit as file
                var vectorString = ""
                var vectorKey = session.key
                var first = true
                for (v in vector)
                {
                    if (first) {
                        first = false
                    } else {
                        vectorString += ", "
                    }
                    vectorString += "$v"
                }
                // NOTE: emits commented out, this is only a demo and the method is actually too large to compile
                fileOut.append("\r\n//            \"$vectorKey\" to doubleArrayOf($vectorString),")
                Log.i("ABC", "\"$vectorKey\" to doubleArrayOf($vectorString),")
            }
            fileOut.append("""
        )
    }
}""")
            writeToLocalFile("SessionVectors.txt", fileOut.toString())
        }
    }

    /** Write to hardcoded app directory - just use this function
     * to output vectors for a hardcoded cache... otherwise
     * DO NOT USE
     */
    suspend fun writeToLocalFile(fileName: String, data: String) = withContext(
        Dispatchers.IO) {

        try {
            val outputStreamWriter = FileOutputStream("/data/data/com.example.compose.jetchat/files/" + fileName)
            outputStreamWriter.write(data.toByteArray())
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("ABC", "File write failed: $e")
        }
    }
}