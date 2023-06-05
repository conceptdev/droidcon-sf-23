package com.example.compose.jetchat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.generativelanguage.v1beta2.DiscussServiceClient
import com.google.ai.generativelanguage.v1beta2.DiscussServiceSettings
import com.google.ai.generativelanguage.v1beta2.Example
import com.google.ai.generativelanguage.v1beta2.GenerateMessageRequest
import com.google.ai.generativelanguage.v1beta2.Message
import com.google.ai.generativelanguage.v1beta2.MessagePrompt
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.grpc.InstantiatingGrpcChannelProvider
import com.google.api.gax.rpc.FixedHeaderProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PalmWrapper {
    /** Matches the OpenAI implementation */
    suspend fun chat(message: String): String {
        return sendMessage(message)
    }

    private val _messages = MutableStateFlow<List<Message>>(value = listOf())
    val messages: StateFlow<List<Message>>
        get() = _messages

    private var client: DiscussServiceClient

    init {
        // Initialize the Discuss Service Client
        client = initializeDiscussServiceClient(
            apiKey = "{PALM_KEY}"
        )
    }

    private suspend fun sendMessage(userInput: String): String {
        val prompt = createPrompt(userInput)

        val request = createMessageRequest(prompt)
        return generateMessage(request)
    }

    private fun initializeDiscussServiceClient(
        apiKey: String
    ): DiscussServiceClient {
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
        val discussServiceClient = DiscussServiceClient.create(settings)

        return discussServiceClient
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

        val messagePrompt = MessagePrompt.newBuilder()
             // required (or just addMessage if there's only one)
            .addAllMessages(_messages.value.toMutableList())
            .setContext("Keep responses short and concise.") // optional
            //.addExamples(createCaliforniaExample()) // use addAllExamples() to add a list of examples
            .build()

        return messagePrompt
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
        //viewModelScope.launch(Dispatchers.IO) {
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
        //}
    }
}