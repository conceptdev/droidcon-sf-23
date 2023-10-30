package com.example.compose.jetchat.data

import kotlinx.serialization.Serializable

/** Class to represent droidcon sessions, with all the relevant
 * fields from https://sf.droidcon.com/speakers/ */
@Serializable
data class SessionInfo(
    val id: String,
    val speaker: String,
    val role: String = "", // TODO: don't have this optional
    val location: String,
    val date: String,
    val time: String,
    val subject: String,
    val description: String = "" // TODO: don't have this optional
) {
    /**
     * Embed JUST the information that contains relevant context for vector similarity searching
     */
    fun forEmbedding () : String {
        return "Speaker role: $role\nSubject: $subject\nDescription:$description"
    }

    /** Mimic the old way that the code added session information to the prompt
     * (from DroidconSessionData.kt) */
    fun toRagString () : String {
        return """
            Speaker: $speaker
            Role: $role
            Location: $location
            Date: $date
            Time: $time
            Subject: $subject
            Description: $description""".trimIndent()
    }

    /** Simple JSON hack, TODO: add Json compiler extension for @Serializable */
    fun toJson () : String {
        // hit some bugs in the past where long text sneaks " double-quotes in.
        // wouldn't be a problem with proper serialization probably...
        val validDescription = description.replace("\"", "'")
        return "{session_id:\"$id\",subject:\"$subject\",speaker:\"$speaker\",role:\"$role\",location_id:\"$location\",date:\"$date\",time:\"$time\",description:\"$validDescription\"}"
    }

    /** Omits Role and Description */
    fun toShortJson () : String {
        // hit some bugs in the past where long text sneaks " double-quotes in.
        // wouldn't be a problem with proper serialization probably...
        val validDescription = description.replace("\"", "'")
        return "{session_id:\"$id\",subject:\"$subject\",speaker:\"$speaker\",location_id:\"$location\",date:\"$date\",time:\"$time\"}"
    }

    companion object {
        /** Returns three hardcoded sessions for testing */
        @Deprecated("Hardcoded response to test with")
        fun hardcodedSessionsList(
        ): List<SessionInfo> {
            var sessionList = mutableListOf<SessionInfo>()

            sessionList.add(
                SessionInfo(
                    "craig-dunn-1",
                    "Craig Dunn",
                    "Software Engineer at Microsoft",
                    "Robertson 1",
                    "2023-06-09",
                    "16:00",
                    "Android AI",
                    "AI and ML bring powerful new features to app developers, for processing text, images, audio, video, and more. In this session we’ll compare and contrast the opportunities available with on-device models using ONNX and the ChatGPT model running in the cloud."
                )
            )
            sessionList.add(
                SessionInfo(
                    "hanson-ho-2",
                    "HANSON HO",
                    "Android Architect at Embrace",
                    "Robertson 2",
                    "2023-06-08",
                    "13:30",
                    "Combating sampling bias in production: How to collect and interpret performance data to drive growth",
                    "App performance data collected in production can be affected by several forms of sampling biases, including the oversampling pf heavy users, undersampling of churned users, and other forms of survivorship bias. This results in an incorrect picture of how your app is actually being experienced in the wild. In this session, we will discuss these biases, including techniques you can use today to mitigate them. You’ll learn what you need to get a clear understanding of how all folks in the *very* heterogeneous Android ecosystem are experiencing your app. That way, you can tailor your app to enhance everyone’s user experience and drive growth for all segments and device cohorts"
                )
            )
            sessionList.add(
                SessionInfo(
                    "istvan-juhos-3",
                    "ISTVÁN JUHOS",
                    "Senior Android Engineer @ TIER",
                    "Fisher West",
                    "2023-06-09",
                    "10:00",
                    "Compose-View Interop in Practice",
                    "If you’re working on an already established, large code base, there’s a good chance that your screens still use Views to some extent. However, these screens should still be maintained to keep UI consistency across your app. In this talk, we’ll look at how we can support the maintenance of such screens and custom UI components with Jetpack Compose’s interoperability features while discussing the ups and downs of having hybrid UIs in our apps."
                )
            )

            return sessionList
        }
    }
}