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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.compose.jetchat.components.JetchatDrawer
import com.example.compose.jetchat.databinding.ContentMainBinding
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Main activity for the app.
 */
class NavActivity : AppCompatActivity(), RecognitionListener, TextToSpeech.OnInitListener {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(
            ComposeView(this).apply {
                consumeWindowInsets = false
                setContent {
                    val drawerState = rememberDrawerState(initialValue = Closed)
                    val drawerOpen by viewModel.drawerShouldBeOpened
                        .collectAsStateWithLifecycle()

                    if (drawerOpen) {
                        // Open drawer and reset state in VM.
                        LaunchedEffect(Unit) {
                            // wrap in try-finally to handle interruption whiles opening drawer
                            try {
                                drawerState.open()
                            } finally {
                                viewModel.resetOpenDrawerAction()
                            }
                        }
                    }

                    // Intercepts back navigation when the drawer is open
                    val scope = rememberCoroutineScope()
                    if (drawerState.isOpen) {
                        BackHandler {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    }

                    JetchatDrawer(
                        drawerState = drawerState,
                        onChatClicked = {
                            findNavController().popBackStack(R.id.nav_home, false)
                            findNavController().navigate(R.id.nav_home, bundleOf("channelName" to it))
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        onProfileClicked = {
                            val bundle = bundleOf("userId" to it)
                            findNavController().navigate(R.id.nav_profile, bundle)
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    ) {
                        AndroidViewBinding(ContentMainBinding::inflate)
                    }
                }
            }
        )

        // Pass `context` to viewModel so that DroiconEmbeddingsWrapper can use it for Sqlite database helper
        viewModel.setContext (this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }


        // Speech to Text
        var speech = SpeechRecognizer.createSpeechRecognizer(this)
        Log.i("LLM", "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this))
        speech.setRecognitionListener(this)
        recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "US-en")
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)

        speech.startListening(recognizerIntent);
        // speech.stopListening();

        Log.d("LLM", "start TTS")
        // TextToSpeech(Context: this, OnInitListener: this)
        tts = TextToSpeech(this, this)

        //tts.speak("Welcome to Jetchat AI", TextToSpeech.QUEUE_FLUSH, null,"")

    }
    private lateinit var speech: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    val RecordAudioRequestCode = 1
    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode
            )
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return findNavController().navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * See https://issuetracker.google.com/142847973
     */
    private fun findNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        return navHostFragment.navController
    }


    /**
     * Implement `RecognitionListener`
     */
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

    /**
     * Implement speech output
     */
    private lateinit var tts: TextToSpeech
    override fun onInit(p0: Int) {
        Log.d("LLM", "onInit (TTS)")
        if (p0 == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("LLM","language not supported...")
            } else {
                // can speak! wait to get called...
                //tts.speak("Welcome to Jetchat AI", TextToSpeech.QUEUE_FLUSH, null,"")
            }
        } else {
            Log.e("LLM","Some TTS error $p0")
        }
    }
    public override fun onDestroy() {
        // Shutdown TTS when activity is destroyed
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

}
