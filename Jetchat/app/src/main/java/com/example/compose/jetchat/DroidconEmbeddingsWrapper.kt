package com.example.compose.jetchat

import android.util.Log
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
                            Your answers will be short and concise, since they will be required to fit on 
                            a mobile device display.""".trimMargin()
            )
        )
    }

    suspend fun chat(message: String): String {

        initVectorCache() // should only run once (HACK: wait to finish)

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
        if (sortedVectors.lastKey() > 0.8){
            Log.i("LLM", "Top match is ${sortedVectors.lastKey()}")

            messagePreamble = "Following are some talks/sessions scheduled for the droidcon San Francisco conference in June 2023:\n\n"
            for (dpKey in sortedVectors.tailMap(0.8)){
                Log.i("LLM", "${dpKey.key} -> ${dpKey.value}")

                messagePreamble += DroidconSessionData.droidconSessions[dpKey.value] + "\n\n"

            }
            messagePreamble += "\n\nUse the above information to answer the following question. Summarize and provide date/time and location if appropriate.\n\n"
            Log.v("LLM", "$messagePreamble")
        }

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
            for (session in DroidconSessionData.droidconSessions) {
                val embeddingRequest = EmbeddingRequest(
                    model = ModelId("text-embedding-ada-002"),
                    input = listOf(session.value)
                )
                val embedding = openAI.embeddings(embeddingRequest)
                val vector = embedding.embeddings[0].embedding.toDoubleArray()
                vectorCache[session.key] = vector
                Log.i("LLM", "$session.key vector: $vector")
            }
        }
    }
    /** key'd map of sessions with metadata
    private val droidconSessions: Map<String, String> = mapOf(
        "HANSON HO" to """Speaker: HANSON HO
Role: Android Architect at Embrace
Location: Robertson 2
Date: 2023-06-08
Time: 13:30
Subject: Combating sampling bias in production: How to collect and interpret performance data to drive growth
Description: App performance data collected in production can be affected by several forms of sampling biases, including the oversampling pf heavy users, undersampling of churned users, and other forms of survivorship bias. This results in an incorrect picture of how your app is actually being experienced in the wild. In this session, we will discuss these biases, including techniques you can use today to mitigate them. You’ll learn what you need to get a clear understanding of how all folks in the *very* heterogeneous Android ecosystem are experiencing your app. That way, you can tailor your app to enhance everyone’s user experience and drive growth for all segments and device cohorts"""
        ,
        "ISTVÁN JUHOS" to """Speaker: ISTVÁN JUHOS
Role: Senior Android Engineer @ TIER
Location: Fisher West
Date: 2023-06-09
Time: 10:00
Subject: Compose-View Interop in Practice
Description: If you’re working on an already established, large code base, there’s a good chance that your screens still use Views to some extent. However, these screens should still be maintained to keep UI consistency across your app. In this talk, we’ll look at how we can support the maintenance of such screens and custom UI components with Jetpack Compose’s interoperability features while discussing the ups and downs of having hybrid UIs in our apps."""
        ,
        "KATERYNA SEMENOVA" to """Speaker: KATERYNA SEMENOVA
Role: Google, Android DevRel engineer
Location: Fisher East
Date: 2023-06-08
Time: 12:10
Subject: Passkeys: A passwordless future with Jetpack Credential Manager
Description: In this talk, you will learn how Jetpack Credential Manager can make it easy to: - Support Passkeys and reduce reliance on Passwords - Unify authentication journeys. I’m a software engineer with over 10 years of experience. During my career I’ve been taking on different roles in web and mobile development, leading teams and managing projects. Last 6 years I’ve been enjoying building apps for Android."""
        ,
        "IURY SOUZA" to """Speaker: IURY SOUZA
Role: Mostly Android things @ Klarna
Location: Fisher West
Date: 2023-06-08
Time: 13:30
Subject: Crash Course in building your First Gradle Plugin
Description:A Gradle plugin is something that we use every day, but have you ever considered how they’re created? What’s behind the magic of the Kotlin DSLs provided by the plugins we use daily? In this talk, we’ll try to uncover the magic behind the Gradle plugin APIs and how to use them to build your own plugin. We’ll explore the process of developing, debugging, testing, and finally publishing your grade plugin just like any other piece of software. By the end of this, you’ll learn how you can turn that custom Gradle task you’ve been copying and pasting across projects into a fully-fledged Gradle plugin!"""
        ,
        "NELSON OSACKY" to """Speaker: NELSON OSACKY
Role: Solutions Engineer at Gradle
Location: Robertson 1
Date: 2023-06-08
Time: 13:30
Subject: Improving Developer Experience with Gradle Build Scans
Description: As a developer, you just want your builds to work but every now and then things go wrong and you need to investigate the build which sucks up a lot of your time not to mention context switching. In this talk we show you how build scans can help save a lot of time when you need to find build performance bottlenecks, investigate/troubleshoot CI failures, build/tool chain failures, visualize your dependencies, etc. to troubleshooting/investigating build issues and accelerate your builds/tests. From this session you will learn: - Can I make my build faster? Build performance profiling with a simple UI: what are all the performance bottlenecks in my build, how well is my build cache working, the impact of heap, garbage, dependency downloads on my build times. - How well is my build parallelized? Can I get more parallelization to speed up my builds? - What’s happening in my build? View the build timeline view to see what tasks/goals are being executed in which order. You can use this to see how well parallelized is your build - Git and CI integration for better workflow. Go from your CI job to your build scan and back. Go to the Git source repo, git commit id, show me all the builds on this git commit id - Troubleshoot CI failures way faster with a build scan by viewing failed taks/goal in consol log. Build scans pull out all the details pertaining to your failure out of the consol log so you don’t have to parse through thousands of lines of logs - Dependency Search: view, search through your projects dependencies and their versions in a few clicks - Triage Test failures: The test dashboard sorts your projects test failures by failure count with a nice UI. With a few clicks you can see which test classes failed the most, sort by your longest tests, test methods with the most test failures, link to your test report - Tool chain and build related failures: What’s breaking my build?"""
        ,
        "NELSON OSACKY, INAKI VILLAR" to """Speaker: NELSON OSACKY, INAKI VILLAR
Role: Solutions Engineer at Gradle
Location: Robertson 1
Date: 2023-06-08
Time: 14:55
Subject: Common Cache issues in Android projects – 2023 Edition
Description: In this talk, we will discuss some of the common Gradle build caching issues that Android developers face, and provide solutions to these problems. We will begin by explaining what the Gradle build cache is and how it works. We will then outline the common issues that developers encounter when using Gradle build cache. These issues include incorrect build cache settings, conflicts with the build cache, and incorrect configurations. Next, we will provide solutions to these issues. We will discuss how to configure the build cache settings correctly to ensure optimal performance. We will also explain how to resolve conflicts that arise when multiple developers work on the same project. Furthermore, we will discuss how to optimize the Gradle build cache for large-scale Android projects. We will provide tips on how to manage the cache for multi-module projects and how to set up a distributed cache for teams working on a shared project. In conclusion, this presentation provides a summary to common Gradle build cache issues in Android development. By understanding the issues and implementing the solutions presented in this talk, developers can optimize their build process and improve the overall efficiency of their development workflow."""
        ,
        "KENICHI KAMBARA" to """Speaker: KENICHI KAMBARA
Role: NTT TechnoCross Corporation Principal Evangelist (Tech)
Location: Robertson 1
Date: 2023-06-08
Time: 10:55
Subject: Developing Apps optimized for Wear OS with Jetpack Compose
Description:Wear OS is a version of Google’s Android operating system mainly designed for Smart Watches. Also, Jetpack Compose is an excellent approach for developing Android apps for smartphones and Wear OS devices. But when developing Wear OS Apps, we must consider some critical differences between smartphones and wearable devices and provide a suitable UI/UX. In this talk, I’ll focus on developing beautiful UIs optimized for Smart Watches with Jetpack Compose with demonstrations."""
        ,
        "AURIMAS LIUTIKAS" to """Speaker: AURIMAS LIUTIKAS
Role: Software Engineer at Google / Gradle Fellow
Location: Fisher West
Date: 2023-06-09
Time: 11:25
Subject: Gradle under a microscope: profiling and optimizing builds
Description: A peek behind the curtains on what Gradle spends time on during builds. We’ll walk through some techniques of using a JVM profiler to optimize your build logic. I’ll share areas that often have the best return on investment and how to make sure you don’t regress as your build logic evolves."""
        ,
        "BEN BORAL" to """Speaker: BEN BORAL
Role: Senior Solutions Engineer, Bitrise.io
Location: Robertson 2
Date: 2023-06-08
Time: 12:10
Subject: The Network Latency Tax (on your build cache)
Description:Claim an exemption from slow Android builds. In this talk you’ll learn how to get the fastest possible Gradle performance by avoiding the network latency tax. If your build times have crept up enough, modularizing your Gradle project and setting up a remote build cache can lead to 90% reduction in execution time. However, in reality, cache performance is heavily influenced by network latency. High latency is often a consequence of data traveling long distances. For a geographically distributed team, cache performance will vary significantly. Some developers will even see build time regressions. Join this talk to learn about the Build Cache CDN, a pragmatic strategy for mitigating the network latency tax on the cache and achieving the fastest build performance."""
        ,
        "BOON WAI HOONG" to """Speaker: BOON WAI HOONG
Role: Android Engineer @ TikTok
Location: Robertson 2
Date: 2023-06-08
Time: 15:55
Subject: Demystifying Baseline Profile
Description: Baseline Profile should be a straightforward “add it and forget it” feature on Android. But is it? In this session, we will attempt to provide context and demystify the journey of adding Baseline Profile into your app. We will cover the major dependencies of Baseline Profile: - ProfileInstaller, what it does and why do we need to add it? - Macrobenchmark, what does it do under the hood to test Baseline Profile? We will also cover the journey of validating an app with Baseline Profile - Installing an apk for testing on a test device, what to expect and lookout for? - What to expect for various distribution methods? (Google Play, other app stores etc) Through this session, I hope the audience is able to take away some learnings and become more confident with implementing Baseline Profiles."""
        ,
        "ASH DAVIES" to """
Speaker: ASH DAVIES
Role: Senior Android Developer @ Snapp Mobile GmbH
Location: Fisher Eeast
Date: 2023-06-08
Time: 13:30
Subject: Beyond the Mockery: Why We Should Embrace Testing Without Mocking Frameworks
Description: In software development, mocking is a popular technique used to simulate dependencies and test behaviour without relying on external systems. However, as with any technique, there are pros and cons to using mocks. In this talk, I’ll discuss why using mocks may not be the best approach and why we should instead use fakes or in-memory implementations of well-defined interfaces. We will explore the drawbacks of mocks, including how they can lead to brittle tests, slow down development, and make it difficult to refactor code. By contrast, we will see how using fakes or in-memory implementations can provide faster feedback, increase confidence in the code, and make it easier to maintain tests as the codebase evolves. We will look at some examples of how to implement these alternatives, and how to make them useful in different testing scenarios."""
        ,
        "GASTÓN KOSUT" to """Speaker: GASTÓN KOSUT
Role: CTO and Co-founder at Hattrick IT
Location: Fisher East
Date: 2023-06-08
Time: 14:25
Subject: Innovating with Health Connect: a deep dive into its key benefits
Description: In this session, we will discuss the key benefits of Health Connect, including reduced API complexity, standardized data schema, and centralized privacy controls. We will also dive into the first steps that developers can take to integrate Health Connect and begin to realize its benefits. By the end of this session, attendees will have a solid understanding of Health Connect and how it can help them streamline their work in the innovative area of healthcare development."""
        ,
        "KINNERA PRIYA PUTTI" to """Speaker: KINNERA PRIYA PUTTI
Role: Android developer at ioki
Location: Robertson 1
Date: 2023-06-08
Time: 14:25
Subject: Animating content changes with Jetpack Compose
Description: Have you wondered how to gracefully transition between different composables without the jolt of an instant change? Or how to use a custom transition effect as the content size changes? In this talk, we will walk through the high-level animation APIs that Compose offers and learn how to pick the right animation API to use for your content change!"""
        ,
        "CHRYSTIAN VIEYRA" to """
Speaker: CHRYSTIAN VIEYRA
Role: Engineering Manager
Location: Robertson 1
Date: 2023-06-09
Time: 15:35
Subject: Pruning Your App: Good Practices for Reducing App Size
Description: App size reduction should be a critical aspect of app development, and is especially necessary for end users who may have restricted device storage or limited data plans. However, it can be challenging for developers—especially those working with multiple contributing teams—to manage and visualize app size throughout the development flow. Using the major telecom company app, Xfinity, as an example, we will take a deep dive into how to optimize app size. This app illustrates the complexities of size management: the app is used across the world in both high- and low-resourced contexts, requires multi-language support, and draws visual assets from regional partners. This presentation will address the following tools and techniques, many of which have been used in the Xfinity development flow, and are broadly applicable to other development projects: Image compression: Using lossless compression with modern formats (.webp, vector graphics) Unused code and assets: Implementing code shrinkers (Google’s R8) Modularizing app delivery: Dividing the app into smaller modules, downloaded on-demand as per user needs (Google’s Dynamic Feature Delivery) Strings: Improving string usage, including considerations for localization Analysis: Monitoring app size throughout the development process"""
        ,
        "CRAIG DUNN" to """Speaker: CRAIG DUNN
Role: Software Engineer at Microsoft
Location: Robertson 1
Date: 2023-06-09
Time: 16:30
Subject: AI for Android on- and off-device
Description: AI and ML bring powerful new features to app developers, for processing text, images, audio, video, and more. In this session we’ll compare and contrast the opportunities available with on-device models using ONNX and the ChatGPT model running in the cloud."""
,
"COLIN MARSCH" to """Speaker: COLIN MARSCH
                Role: Android Engineer at Cash App
                Location: Robertson 1
                Date: 2023-06-08
                Time: 12:10
    Subject: Navigating the Unknown: Tips for Efficiently Learning a New Codebase
    Description: Learning new codebases quickly is an essential skill for a successful career as an Android engineer. In this talk, we’ll explore why it is important to cultivate this skill and walkthrough a systematic approach to efficiently master a new codebase, from analyzing code architecture to stepping through key flows. The real value of learning a new codebase quickly is in the ability to accelerate development and share your knowledge with others. We’ll discuss the importance of documenting what you’ve learned and sharing it broadly. You’ll leave with practical strategies and tips for accelerating your learning process and becoming a valuable member of any development team."""
,
"DANIEL GALPIN" to """Speaker: DANIEL GALPIN
    Role: Android Developer Advocate and Fast Talking YouTuber
    Location: Robertson 1
    Date: 2023-06-09
    Time: 9:00
    Subject: Android App Quality – A Google Perspective
    Description: At Google I/O, we defined what app quality means to Google Play, focusing largely on what makes core experiences great. This session dives into three of the lesser-explored pillars from that work: technical app quality, privacy and security, and user experience, focusing on the work we’re doing on the Android platform, toolkit, and Google Play. We’ll cover problem areas as well as tools we’re providing to help solve these issues. Dan leads outreach for the Android Developer Relations team at Google, where he’s written blogs, developed videos, spoken at conferences, designed Udacity courseware, and written libraries for over eight years, and Kotlin + Jetpack is the thing that makes being on the Android team fun and exciting again."""
,
"DANIEL HOROWITZ" to """Speaker: DANIEL HOROWITZ
    Role: Android @ Spotify
    Location: Fisher East
    Date: 2023-06-09
    Time: 13:40
    Subject: Fantastic tests and where to find them
    Description: The more code we write the more we raise the risk of having issues on our apps. Our job as engineers is to mitigate those risks and how can we do that? With tests. We all agree that tests are vital to build a successful app but how can we know that we are testing the right things? How do we measure the quality of our tests? In this session we will go through these questions in a pragmatic approach, diving into every type of test in every aspect of the development phase. From communicating with backend to UI components, we will go through strategies and approaches on how to properly test each piece of code."""
    )
     */
}