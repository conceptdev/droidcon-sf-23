package com.example.compose.jetchat

import android.util.Log
import com.example.compose.jetchat.data.DroidconSessionData
import com.google.ai.generativelanguage.v1beta2.DiscussServiceClient
import com.google.ai.generativelanguage.v1beta2.DiscussServiceSettings
import com.google.ai.generativelanguage.v1beta2.EmbedTextRequest
import com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest
import com.google.ai.generativelanguage.v1beta2.Message
import com.google.ai.generativelanguage.v1beta2.MessagePrompt
import com.google.ai.generativelanguage.v1beta2.TextServiceClient
import com.google.ai.generativelanguage.v1beta2.TextServiceSettings
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider
import com.google.api.gax.rpc.FixedHeaderProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.SortedMap

class PalmWrapper {
    private val apiKey = "{PALM_KEY}"

    private val _messages = MutableStateFlow<List<Message>>(value = listOf())
    val messages: StateFlow<List<Message>>
        get() = _messages

    private var client: DiscussServiceClient
    private var textClient: TextServiceClient

    init {
        // Initialize the Discuss Service Client
        client = initializeDiscussServiceClient()

        textClient = initializeTextServiceClient()
    }

    private fun initializeDiscussServiceClient(): DiscussServiceClient {
        // (This is a workaround because GAPIC java libraries don't yet support API key auth)
        val transportChannelProvider = InstantiatingGrpcChannelProvider.newBuilder()
            .setHeaderProvider(FixedHeaderProvider.create(hashMapOf("x-goog-api-key" to apiKey)))
            .build()

        // Create DiscussServiceSettings
        val settings = DiscussServiceSettings.newBuilder()
            .setTransportChannelProvider(transportChannelProvider)
            .setCredentialsProvider(FixedCredentialsProvider.create(null))
            .build()

        // Initialize a DiscussServiceClient
        return DiscussServiceClient.create(settings)
    }

    private fun initializeTextServiceClient(): TextServiceClient {
        val headers = HashMap<String, String>()
        headers["x-goog-api-key"] = apiKey

        val provider = InstantiatingGrpcChannelProvider.newBuilder()
            .setHeaderProvider(FixedHeaderProvider.create(headers))
            .build()

        val settings = TextServiceSettings.newBuilder()
            .setTransportChannelProvider(provider)
            .setCredentialsProvider(FixedCredentialsProvider.create(null))
            .build()

        return TextServiceClient.create(settings)
    }

    /** Provide grounding for user query by checking
     * message against embeddings.
     *
     * @return the relevant data to add to the query,
     * along with additional prompt instructions.
     * Empty string if no matching embeddings found. */
    private suspend fun grounding(message: String): String {
        var messagePreamble = ""
        val embeddingRequest = EmbedTextRequest.newBuilder()
            .setModel("models/embedding-gecko-001")
            .setText(message)
            .build()

        val response = textClient.embedText(embeddingRequest)
        val messageVector = response.embedding.valueList.map { it.toDouble() }.toDoubleArray()
        Log.i("LLM", "messageVector: $messageVector")

        val sortedVectors: SortedMap<Double, String> = sortedMapOf()
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
            Log.v("LLM", messagePreamble)
        } else {
            Log.i("LLM", "No vector match found")
        }
        return messagePreamble
    }

    /** key'd map of session to vector */
    private var vectorCache: MutableMap<String, DoubleArray> = mutableMapOf()

    /** make embedding requests for each session, populate vectorCache */
    suspend fun initVectorCache() {
        if (vectorCache.isEmpty()) {
            for (session in DroidconSessionData.droidconSessions) {
                val embeddingRequest = EmbedTextRequest.newBuilder()
                    .setModel("models/embedding-gecko-001")
                    .setText(session.value)
                    .build()

                val response = textClient.embedText(embeddingRequest)
                val vector = response.embedding.valueList.map { it.toDouble() }.toDoubleArray()
                vectorCache[session.key] = vector
                Log.i("LLM", "$session.key vector: $vector")
            }
        }
    }

    /** Matches the OpenAI implementation */
    suspend fun chat(message: String): String {
        initVectorCache() // should only run once (HACK: wait to finish)

        val messagePreamble = grounding(message)

        return sendMessage(messagePreamble + message)
    }

    private suspend fun sendMessage(userInput: String): String {
        val prompt = createPrompt(userInput)

        val request = createMessageRequest(prompt)
        return generateMessage(request)
    }

    private suspend fun createPrompt(
        messageContent: String
    ): MessagePrompt {
        val palmMessage = Message.newBuilder()
            .setAuthor("0")
            .setContent(messageContent)
            .build()

        // Add the new Message to the UI
        _messages.update {
            it.toMutableList().apply {
                add(palmMessage)
            }
        }

        return MessagePrompt.newBuilder()
            // required (or just addMessage if there's only one)
            .addAllMessages(_messages.value.toMutableList())
            .setContext(
                "You are a personal assistant called JetchatAI.\n" +
                    "You will answer questions about the speakers and sessions at the droidcon SF conference.\n" +
                    "The conference is on June 8th and 9th, 2023. It starts at 9am and finishes by 6pm.\n" +
                    "Your answers will be short and concise, since they will be required to fit on\n" +
                    "a mobile device display."
            ) // optional
            //.addExamples(createCaliforniaExample()) // use addAllExamples() to add a list of examples
            .build()
    }

    private suspend fun createMessageRequest(prompt: MessagePrompt): GenerateMessageRequest {
        return GenerateMessageRequest.newBuilder()
            .setModel("models/chat-bison-001") // Required, which model to use to generate the result
            .setPrompt(prompt) // Required
            .setTemperature(0.5f) // Optional, controls the randomness of the output
            .setCandidateCount(1) // Optional, the number of generated messages to return
            .build()
    }

    private suspend fun generateMessage(
        request: GenerateMessageRequest
    ): String {
        try {
            val response = client.generateMessage(request)

            val returnedMessage = response.candidatesList.last()
            // display the returned message in the UI
            _messages.update {
                // Add the response to the list
                it.toMutableList().apply {
                    add(returnedMessage)
                }
            }
            return returnedMessage.content
        } catch (e: Exception) {
            // There was an error, let's add a new message with the details
            // HACK: we are not using a ViewModel here, just returning the error string
            _messages.update { messages ->
                val mutableList = messages.toMutableList()
                mutableList.apply {
                    add(
                        Message.newBuilder()
                            .setAuthor("500")
                            .setContent(e.message)
                            .build()
                    )
                }
            }
            return "API Error ${e.message}"
        }
    }
}