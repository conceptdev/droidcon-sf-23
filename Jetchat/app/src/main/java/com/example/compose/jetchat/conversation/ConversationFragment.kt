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

package com.example.compose.jetchat.conversation

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.ui.platform.ComposeView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.compose.jetchat.MainViewModel
import com.example.compose.jetchat.R
import com.example.compose.jetchat.components.Channel
import com.example.compose.jetchat.theme.JetchatTheme

class ConversationFragment : Fragment(), RecognitionListener {

    private val activityViewModel: MainViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // --------------
        // Speech to Text
        speechToText = SpeechRecognizer.createSpeechRecognizer(this.context)
        val isOkay = this.context?.let { SpeechRecognizer.isRecognitionAvailable(it) }
        Log.i("LLM", "isRecognitionAvailable: $isOkay")
        speechToText.setRecognitionListener(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        // speech.startListening(recognizerIntent);
        // speech.stopListening();
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(inflater.context).apply {
        layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT)

        arguments?.getString("channelName")?.let { channelName ->
            activityViewModel.currentChannel = Channel.values().first { it.name == channelName }
        }

        setContent {
            JetchatTheme {
                ConversationContent(
                    uiState = activityViewModel.uiState,
                    navigateToProfile = { user ->
                        // Click callback
                        val bundle = bundleOf("userId" to user)
                        findNavController().navigate(
                            R.id.nav_profile,
                            bundle
                        )
                    },
                    onNavIconPressed = {
                        activityViewModel.openDrawer()
                    },
                    onMessageSent = activityViewModel::onMessageSent,
                    botIsTyping = activityViewModel.botIsTyping,
                    onListenPressed = {
                        Log.i("LLM", "SpeechToText start listening...")
                        listen()
                    }
                )
            }
        }
    }

    private fun listen () {
        speechToText.startListening(recognizerIntent)
        // calls onResults() which calls activityViewModel.setSpeech()
    }

    /**
     * Implement `RecognitionListener`
     */
    private lateinit var speechToText: SpeechRecognizer
    private lateinit var recognizerIntent: Intent

    override fun onReadyForSpeech(p0: Bundle?) {
        Log.i("LLM", "onReadyForSpeech")
    }

    override fun onBeginningOfSpeech() {
        Log.i("LLM", "onBeginningOfSpeech")
    }

    override fun onRmsChanged(p0: Float) {
        //Log.d("LLM", "onRmsChanged")
    }

    override fun onBufferReceived(p0: ByteArray?) {
        Log.d("LLM", "onBufferReceived")
    }

    override fun onEndOfSpeech() {
        Log.d("LLM", "onEndOfSpeech")
    }

    override fun onError(p0: Int) {
        val errorMessage: String = getErrorMessage(p0)
        Log.e("LLM", "onError FAILED $errorMessage")
    }

    override fun onResults(p0: Bundle?) {
        Log.i("LLM", "onResults")
        val matches = p0!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = ""
        if (matches != null) { // seems to repeat, grab one
            for (result in matches) text = result
        }
        activityViewModel.setSpeech(text)
        Log.i("LLM", "heard: $text")
    }

    override fun onPartialResults(p0: Bundle?) {
        Log.d("LLM", "onPartialResults")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        Log.d("LLM", "onEvent")
    }

    private fun getErrorMessage(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio error"
            SpeechRecognizer.ERROR_CLIENT -> "Client error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions error"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout error"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match error"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy error"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout error"
            else -> "Unknown speech recognizer error"
        }
    }
}
