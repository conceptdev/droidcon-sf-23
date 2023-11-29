/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.jetchat

import android.content.Context
import android.content.Intent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.jetchat.components.Channel
import com.example.compose.jetchat.conversation.ConversationUiState
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.data.initialAssistantMessages
import com.example.compose.jetchat.data.initialDocumentMessages
import com.example.compose.jetchat.data.initialDroidconMessages
import com.example.compose.jetchat.data.initialOpenAiMessages
import com.example.compose.jetchat.data.initialPalmMessages
import com.example.compose.jetchat.data.meProfile
import com.example.compose.jetchat.data.openAiProfile
import com.example.compose.jetchat.data.palmProfile
import com.example.compose.jetchat.profile.ProfileScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Used to communicate between screens.
 */
class MainViewModel : ViewModel() {

    private val _drawerShouldBeOpened = MutableStateFlow(false)
    val drawerShouldBeOpened = _drawerShouldBeOpened.asStateFlow()

    private lateinit var textToSpeech: TextToSpeech

    /** Send transcribed speech to model */
    fun setSpeech (text: String) {
        onMessageSent(text)
    }
    /** TextToSpeech class for reading out bot responses */
    fun setSpeechGenerator (tts: TextToSpeech){
        textToSpeech = tts
    }
    /** Cache the context for passing to objects wanting
     * to instantiate database helper classes */
    private lateinit var context: Context
    /** Set `context` for Sqlite database helper on
     * OpenAI and Droidcon wrappers
     */
    fun setContext (ctx: Context){
        context = ctx
        droidconWrapper = DroidconEmbeddingsWrapper(context)
        openAIWrapper = OpenAIWrapper(context)
        documentWrapper = DocumentChatWrapper(context)
        assistantWrapper = AssistantWrapper(context)
    }

    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    //----------Below code added for OpenAI integration-------------
    private val openAiUiState by mutableStateOf(
        ConversationUiState(
            initialMessages = initialOpenAiMessages,
            channelName = Channel.OPENAI.label,
            channelMembers = 2,
            channelBotProfile = openAiProfile
        )
    )

    private val palmUiState by mutableStateOf(
        ConversationUiState(
            initialMessages = initialPalmMessages,
            channelName = Channel.PALM.label,
            channelMembers = 2,
            channelBotProfile = palmProfile
        )
    )

    private val droidconUiState by mutableStateOf(
        ConversationUiState(
            initialMessages = initialDroidconMessages,
            channelName = Channel.DROIDCON.label,
            channelMembers = 2,
            channelBotProfile = openAiProfile
        )
    )

    private val documentUiState by mutableStateOf(
        ConversationUiState(
            initialMessages = initialDocumentMessages,
            channelName = Channel.DOCUMENT.label,
            channelMembers = 2,
            channelBotProfile = openAiProfile
        )
    )

    private val assistantUiState by mutableStateOf(
        ConversationUiState(
            initialMessages = initialAssistantMessages,
            channelName = Channel.ASSISTANT.label,
            channelMembers = 2,
            channelBotProfile = openAiProfile
        )
    )

    var currentChannel by mutableStateOf(Channel.OPENAI)

    val uiState: ConversationUiState
        get() {
            return when (currentChannel) {
                Channel.PALM -> palmUiState
                Channel.OPENAI -> openAiUiState
                Channel.DROIDCON -> droidconUiState
                Channel.DOCUMENT -> documentUiState
                Channel.ASSISTANT -> assistantUiState
            }
        }
    private var openAIWrapper = OpenAIWrapper(null)
    private var palmWrapper = PalmWrapper()
    /** Requires a `context` for database operations. Set post-init with `setContext` function call */
    private var droidconWrapper = DroidconEmbeddingsWrapper(null) // no context available for database functions in DroidconEmbeddingsWrapper, added later by `setContext` function
    private var documentWrapper = DocumentChatWrapper(null)
    private var assistantWrapper = AssistantWrapper(null)

    var botIsTyping by mutableStateOf(false)
        private set

    fun onMessageSent(content: String) {
        // add user message to chat history
        addMessage(meProfile, content)

        // start typing animation while request loads
        botIsTyping = true

        // fetch openai response and add to chat history
        viewModelScope.launch(Dispatchers.IO) {
            // if user message contains "image" keyword, target image endpoint, otherwise target chat endpoint
            if (content.contains("image", ignoreCase = true)) {
                var responseContent: String
                var imageUrl: String? = null

                try {
                    imageUrl = openAIWrapper.imageURL(content)
                    responseContent = "Generated image:"
                } catch (e: Exception) {
                    responseContent = "Sorry, there was an error processing your request: ${e.message}"
                }

                botIsTyping = false
                addMessage(author = uiState.channelBotProfile, content = responseContent, imageUrl = imageUrl)
            } else {
                val chatResponse = try {
                    // HACK: cheat way to swap back-ends
                    when (currentChannel) {
                        Channel.PALM -> palmWrapper.chat(content)
                        Channel.DROIDCON -> droidconWrapper.chat(content)
                        Channel.DOCUMENT -> documentWrapper.chat(content)
                        Channel.ASSISTANT -> assistantWrapper.chat(content)
                        else -> openAIWrapper.chat(content)
                    }
                } catch (e: Exception) {
                    "Sorry, there was an error processing your request: ${e.message}"
                }

                botIsTyping = false
                addMessage(author = uiState.channelBotProfile, content = chatResponse)

                if (Constants.ENABLE_SPEECH) {
                    textToSpeech.speak(chatResponse, TextToSpeech.QUEUE_FLUSH, null, "")
                }
            }
        }
    }

    private fun addMessage(author: ProfileScreenState, content: String, imageUrl: String? = null) {
        // calculate message timestamp
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val timeNow = dateFormat.format(currentTime)

        val message = Message(
            author = author.displayName,
            content = content,
            timestamp = timeNow,
            imageUrl = imageUrl,
            authorImage = author.photo
        )

        uiState.addMessage(message)
    }
}
