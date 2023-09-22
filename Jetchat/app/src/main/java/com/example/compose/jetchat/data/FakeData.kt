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

package com.example.compose.jetchat.data

import com.example.compose.jetchat.R
import com.example.compose.jetchat.conversation.ConversationUiState
import com.example.compose.jetchat.conversation.Message
import com.example.compose.jetchat.data.EMOJIS.EMOJI_CLOUDS
import com.example.compose.jetchat.data.EMOJIS.EMOJI_FLAMINGO
import com.example.compose.jetchat.data.EMOJIS.EMOJI_MELTING
import com.example.compose.jetchat.data.EMOJIS.EMOJI_PINK_HEART
import com.example.compose.jetchat.data.EMOJIS.EMOJI_POINTS
import com.example.compose.jetchat.profile.ProfileScreenState

val meProfile = ProfileScreenState(
    userId = "me",
    photo = R.drawable.conceptdev,
    name = "Craig Dunn",
    status = "Online",
    displayName = "conceptdev",
    position = "Principal Software Engineer @ Microsoft",
    twitter = "twitter.com/conceptdev",
    timeZone = "In your timezone",
    commonChannels = null
)

val openAiProfile = ProfileScreenState(
    userId = "openai",
    photo = R.drawable.openai_logomark,
    name = "JetchatAI",
    status = "Away",
    displayName = "JetchatAI bot",
    position = "Demo app for LLMs on Android, based on Google's Jetchat sample",
    twitter = "twitter.com/surfaceduodev",
    timeZone = "12:25 AM local time (Eastern Daylight Time)",
    commonChannels = "1"
)

val palmProfile = ProfileScreenState(
    userId = "palm",
    photo = R.drawable.palm_logo,
    name = "Pathways Language Model (PaLM)",
    status = "Away",
    displayName = "PaLM bot",
    position = "Next generation large language model that builds on Google’s legacy of breakthrough research in machine learning and responsible AI",
    twitter = "twitter.com/GoogleAI",
    timeZone = "12:25 AM local time (Eastern Daylight Time)",
    commonChannels = "1"
)

val onnxProfile = ProfileScreenState(
    userId = "onnx",
    photo = R.drawable.onnx_logo,
    name = "Open Neural Network Exchange (ONNX)",
    status = "Away",
    displayName = "ONNX bot",
    position = "Open format for representing machine learning models",
    twitter = "twitter.com/onnxai",
    timeZone = "12:25 AM local time (Eastern Daylight Time)",
    commonChannels = "1"
)

val profiles = listOf(meProfile, openAiProfile, palmProfile, onnxProfile)

val initialOpenAiMessages = listOf(
    Message(
        openAiProfile.displayName,
        "Welcome to #jetchat-ai!",
        "8:07 pm",
        authorImage = openAiProfile.photo
    )
)
val initialPalmMessages =
    listOf(
        Message(
            palmProfile.displayName,
            "Welcome to #jetchat-palm!",
            "8:07 pm",
            authorImage = palmProfile.photo
        )
    )
val initialDroidconMessages =
    listOf(
        Message(
            openAiProfile.displayName,
            "Welcome to #droidcon-chat! Ask questions about the schedule for droidcon SF 2023 🤖",
            "9:00 am",
            authorImage = openAiProfile.photo
        )
    )

private val initialMessages = listOf(
    Message(
        "me",
        "Check it out!",
        "8:07 PM"
    ),
    Message(
        "me",
        "Thank you!$EMOJI_PINK_HEART",
        "8:06 PM",
        R.drawable.sticker
    ),
    Message(
        "Taylor Brooks",
        "You can use all the same stuff",
        "8:05 PM"
    ),
    Message(
        "Taylor Brooks",
        "@aliconors Take a look at the `Flow.collectAsStateWithLifecycle()` APIs",
        "8:05 PM"
    ),
    Message(
        "John Glenn",
        "Compose newbie as well $EMOJI_FLAMINGO, have you looked at the JetNews sample? " +
            "Most blog posts end up out of date pretty fast but this sample is always up to " +
            "date and deals with async data loading (it's faked but the same idea " +
            "applies) $EMOJI_POINTS https://goo.gle/jetnews",
        "8:04 PM"
    ),
    Message(
        "me",
        "Compose newbie: I’ve scourged the internet for tutorials about async data " +
            "loading but haven’t found any good ones $EMOJI_MELTING $EMOJI_CLOUDS. " +
            "What’s the recommended way to load async data and emit composable widgets?",
        "8:03 PM"
    )
)
val exampleUiState = ConversationUiState(
    initialMessages = initialMessages,
    channelName = "#composers",
    channelMembers = 42,
    channelBotProfile = meProfile
)

object EMOJIS {
    // EMOJI 15
    const val EMOJI_PINK_HEART = "\uD83E\uDE77"

    // EMOJI 14 🫠
    const val EMOJI_MELTING = "\uD83E\uDEE0"

    // ANDROID 13.1 😶‍🌫️
    const val EMOJI_CLOUDS = "\uD83D\uDE36\u200D\uD83C\uDF2B️"

    // ANDROID 12.0 🦩
    const val EMOJI_FLAMINGO = "\uD83E\uDDA9"

    // ANDROID 12.0  👉
    const val EMOJI_POINTS = " \uD83D\uDC49"
}
