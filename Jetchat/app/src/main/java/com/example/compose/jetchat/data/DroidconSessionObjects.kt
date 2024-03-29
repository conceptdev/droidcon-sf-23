package com.example.compose.jetchat.data

import com.example.compose.jetchat.data.SessionInfo

class DroidconSessionObjects {
    companion object {
        val droidconSessions: Map<String, SessionInfo> = mapOf(
            "hanson-ho" to SessionInfo(
                id = "hanson-ho",
                speaker = "HANSON HO",
                role = "Android Architect at Embrace",
                location = "Robertson 2",
                date = "2023-06-08",
                time = "13:30",
                subject = "Combating sampling bias in production: How to collect and interpret performance data to drive growth",
                description = "App performance data collected in production can be affected by several forms of sampling biases, including the oversampling pf heavy users, undersampling of churned users, and other forms of survivorship bias. This results in an incorrect picture of how your app is actually being experienced in the wild. In this session, we will discuss these biases, including techniques you can use today to mitigate them. You’ll learn what you need to get a clear understanding of how all folks in the *very* heterogeneous Android ecosystem are experiencing your app. That way, you can tailor your app to enhance everyone’s user experience and drive growth for all segments and device cohorts"
            ),
            "istvan-juhos" to SessionInfo(
                id = "istvan-juhos",
                speaker = "ISTVÁN JUHOS",
                role = "Senior Android Engineer @ TIER",
                location = "Fisher West",
                date = "2023-06-09",
                time = "10:00",
                subject = "Compose-View Interop in Practice",
                description = "If you’re working on an already established, large code base, there’s a good chance that your screens still use Views to some extent. However, these screens should still be maintained to keep UI consistency across your app. In this talk, we’ll look at how we can support the maintenance of such screens and custom UI components with Jetpack Compose’s interoperability features while discussing the ups and downs of having hybrid UIs in our apps."
            ),
            "kateryna-semenova" to SessionInfo(
                id = "kateryna-semenova",
                speaker = "KATERYNA SEMENOVA",
                role = "Google, Android DevRel engineer",
                location = "Fisher East",
                date = "2023-06-08",
                time = "12:10",
                subject = "Passkeys: A passwordless future with Jetpack Credential Manager",
                description = "In this talk, you will learn how Jetpack Credential Manager can make it easy to: - Support Passkeys and reduce reliance on Passwords - Unify authentication journeys. I’m a software engineer with over 10 years of experience. During my career I’ve been taking on different roles in web and mobile development, leading teams and managing projects. Last 6 years I’ve been enjoying building apps for Android."
            ),
            "iury-souza" to SessionInfo(
                id = "iury-souza",
                speaker = "IURY SOUZA",
                role = "Mostly Android things @ Klarna",
                location = "Fisher West",
                date = "2023-06-08",
                time = "13:30",
                subject = "Crash Course in building your First Gradle Plugin",
                description = "A Gradle plugin is something that we use every day, but have you ever considered how they’re created? What’s behind the magic of the Kotlin DSLs provided by the plugins we use daily? In this talk, we’ll try to uncover the magic behind the Gradle plugin APIs and how to use them to build your own plugin. We’ll explore the process of developing, debugging, testing, and finally publishing your grade plugin just like any other piece of software. By the end of this, you’ll learn how you can turn that custom Gradle task you’ve been copying and pasting across projects into a fully-fledged Gradle plugin!"
            ),
            "nelson-osacky" to SessionInfo(
                id = "nelson-osacky",
                speaker = "NELSON OSACKY",
                role = "Solutions Engineer at Gradle",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "13:30",
                subject = "Improving Developer Experience with Gradle Build Scans",
                description = "As a developer, you just want your builds to work but every now and then things go wrong and you need to investigate the build which sucks up a lot of your time not to mention context switching. In this talk we show you how build scans can help save a lot of time when you need to find build performance bottlenecks, investigate/troubleshoot CI failures, build/tool chain failures, visualize your dependencies, etc. to troubleshooting/investigating build issues and accelerate your builds/tests. From this session you will learn: - Can I make my build faster? Build performance profiling with a simple UI: what are all the performance bottlenecks in my build, how well is my build cache working, the impact of heap, garbage, dependency downloads on my build times. - How well is my build parallelized? Can I get more parallelization to speed up my builds? - What’s happening in my build? View the build timeline view to see what tasks/goals are being executed in which order. You can use this to see how well parallelized is your build - Git and CI integration for better workflow. Go from your CI job to your build scan and back. Go to the Git source repo, git commit id, show me all the builds on this git commit id - Troubleshoot CI failures way faster with a build scan by viewing failed taks/goal in consol log. Build scans pull out all the details pertaining to your failure out of the consol log so you don’t have to parse through thousands of lines of logs - Dependency Search: view, search through your projects dependencies and their versions in a few clicks - Triage Test failures: The test dashboard sorts your projects test failures by failure count with a nice UI. With a few clicks you can see which test classes failed the most, sort by your longest tests, test methods with the most test failures, link to your test report - Tool chain and build related failures: What’s breaking my build?"
            ),
            "inaki-villar" to SessionInfo(
                id = "inaki-villar",
                speaker = "NELSON OSACKY, INAKI VILLAR",
                role = "Solutions Engineer at Gradle",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "14:55",
                subject = "Common Cache issues in Android projects – 2023 Edition",
                description = "In this talk, we will discuss some of the common Gradle build caching issues that Android developers face, and provide solutions to these problems. We will begin by explaining what the Gradle build cache is and how it works. We will then outline the common issues that developers encounter when using Gradle build cache. These issues include incorrect build cache settings, conflicts with the build cache, and incorrect configurations. Next, we will provide solutions to these issues. We will discuss how to configure the build cache settings correctly to ensure optimal performance. We will also explain how to resolve conflicts that arise when multiple developers work on the same project. Furthermore, we will discuss how to optimize the Gradle build cache for large-scale Android projects. We will provide tips on how to manage the cache for multi-module projects and how to set up a distributed cache for teams working on a shared project. In conclusion, this presentation provides a summary to common Gradle build cache issues in Android development. By understanding the issues and implementing the solutions presented in this talk, developers can optimize their build process and improve the overall efficiency of their development workflow."
            ),
            "kenichi-kambara" to SessionInfo(
                id = "kenichi-kambara",
                speaker = "KENICHI KAMBARA",
                role = "NTT TechnoCross Corporation Principal Evangelist (Tech)",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "10:55",
                subject = "Developing Apps optimized for Wear OS with Jetpack Compose",
                description = "Wear OS is a version of Google’s Android operating system mainly designed for Smart Watches. Also, Jetpack Compose is an excellent approach for developing Android apps for smartphones and Wear OS devices. But when developing Wear OS Apps, we must consider some critical differences between smartphones and wearable devices and provide a suitable UI/UX. In this talk, I’ll focus on developing beautiful UIs optimized for Smart Watches with Jetpack Compose with demonstrations."
            ),
            "aurimas-liutikas" to SessionInfo(
                id = "aurimas-liutikas",
                speaker = "AURIMAS LIUTIKAS",
                role = "Software Engineer at Google / Gradle Fellow",
                location = "Fisher West",
                date = "2023-06-09",
                time = "11:25",
                subject = "Gradle under a microscope: profiling and optimizing builds",
                description = "A peek behind the curtains on what Gradle spends time on during builds. We’ll walk through some techniques of using a JVM profiler to optimize your build logic. I’ll share areas that often have the best return on investment and how to make sure you don’t regress as your build logic evolves."
            ),
            "ben-boral" to SessionInfo(
                id = "ben-boral",
                speaker = "BEN BORAL",
                role = "Senior Solutions Engineer, Bitrise.io",
                location = "Robertson 2",
                date = "2023-06-08",
                time = "12:10",
                subject = "The Network Latency Tax (on your build cache)",
                description = "Claim an exemption from slow Android builds. In this talk you’ll learn how to get the fastest possible Gradle performance by avoiding the network latency tax. If your build times have crept up enough, modularizing your Gradle project and setting up a remote build cache can lead to 90% reduction in execution time. However, in reality, cache performance is heavily influenced by network latency. High latency is often a consequence of data traveling long distances. For a geographically distributed team, cache performance will vary significantly. Some developers will even see build time regressions. Join this talk to learn about the Build Cache CDN, a pragmatic strategy for mitigating the network latency tax on the cache and achieving the fastest build performance."
            ),
            "boon-wai-hoong" to SessionInfo(
                id = "boon-wai-hoong",
                speaker = "BOON WAI HOONG",
                role = "Android Engineer @ TikTok",
                location = "Robertson 2",
                date = "2023-06-08",
                time = "15:55",
                subject = "Demystifying Baseline Profile",
                description = "Baseline Profile should be a straightforward “add it and forget it” feature on Android. But is it? In this session, we will attempt to provide context and demystify the journey of adding Baseline Profile into your app. We will cover the major dependencies of Baseline Profile: - ProfileInstaller, what it does and why do we need to add it? - Macrobenchmark, what does it do under the hood to test Baseline Profile? We will also cover the journey of validating an app with Baseline Profile - Installing an apk for testing on a test device, what to expect and lookout for? - What to expect for various distribution methods? (Google Play, other app stores etc) Through this session, I hope the audience is able to take away some learnings and become more confident with implementing Baseline Profiles."
            ),
            "ash-davies" to SessionInfo(
                id = "ash-davies",
                speaker = "ASH DAVIES",
                role = "Senior Android Developer @ Snapp Mobile GmbH",
                location = "Fisher Eeast",
                date = "2023-06-08",
                time = "13:30",
                subject = "Beyond the Mockery: Why We Should Embrace Testing Without Mocking Frameworks",
                description = "In software development, mocking is a popular technique used to simulate dependencies and test behaviour without relying on external systems. However, as with any technique, there are pros and cons to using mocks. In this talk, I’ll discuss why using mocks may not be the best approach and why we should instead use fakes or in-memory implementations of well-defined interfaces. We will explore the drawbacks of mocks, including how they can lead to brittle tests, slow down development, and make it difficult to refactor code. By contrast, we will see how using fakes or in-memory implementations can provide faster feedback, increase confidence in the code, and make it easier to maintain tests as the codebase evolves. We will look at some examples of how to implement these alternatives, and how to make them useful in different testing scenarios."
            ),
            "gaston-kosut" to SessionInfo(
                id = "gaston-kosut",
                speaker = "GASTÓN KOSUT",
                role = "CTO and Co-founder at Hattrick IT",
                location = "Fisher East",
                date = "2023-06-08",
                time = "14:25",
                subject = "Innovating with Health Connect: a deep dive into its key benefits",
                description = "In this session, we will discuss the key benefits of Health Connect, including reduced API complexity, standardized data schema, and centralized privacy controls. We will also dive into the first steps that developers can take to integrate Health Connect and begin to realize its benefits. By the end of this session, attendees will have a solid understanding of Health Connect and how it can help them streamline their work in the innovative area of healthcare development."
            ),
            "kinnera-priya-putti" to SessionInfo(
                id = "kinnera-priya-putti",
                speaker = "KINNERA PRIYA PUTTI",
                role = "Android developer at ioki",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "14:25",
                subject = "Animating content changes with Jetpack Compose",
                description = "Have you wondered how to gracefully transition between different composables without the jolt of an instant change? Or how to use a custom transition effect as the content size changes? In this talk, we will walk through the high-level animation APIs that Compose offers and learn how to pick the right animation API to use for your content change!"
            ),
            "chrystian-vieyra" to SessionInfo(
                id = "chrystian-vieyra",
                speaker = "CHRYSTIAN VIEYRA",
                role = "Engineering Manager",
                location = "Robertson 1",
                date = "2023-06-09",
                time = "15:35",
                subject = "Pruning Your App: Good Practices for Reducing App Size",
                description = "App size reduction should be a critical aspect of app development, and is especially necessary for end users who may have restricted device storage or limited data plans. However, it can be challenging for developers—especially those working with multiple contributing teams—to manage and visualize app size throughout the development flow. Using the major telecom company app, Xfinity, as an example, we will take a deep dive into how to optimize app size. This app illustrates the complexities of size management: the app is used across the world in both high- and low-resourced contexts, requires multi-language support, and draws visual assets from regional partners. This presentation will address the following tools and techniques, many of which have been used in the Xfinity development flow, and are broadly applicable to other development projects: Image compression: Using lossless compression with modern formats (.webp, vector graphics) Unused code and assets: Implementing code shrinkers (Google’s R8) Modularizing app delivery: Dividing the app into smaller modules, downloaded on-demand as per user needs (Google’s Dynamic Feature Delivery) Strings: Improving string usage, including considerations for localization Analysis: Monitoring app size throughout the development process"
            ),
            "craig-dunn" to SessionInfo(
                id = "craig-dunn",
                speaker = "CRAIG DUNN",
                role = "Software Engineer at Microsoft",
                location = "Robertson 1",
                date = "2023-06-09",
                time = "16:30",
                subject = "AI for Android on- and off-device",
                description = "AI and ML bring powerful new features to app developers, for processing text, images, audio, video, and more. In this session we’ll compare and contrast the opportunities available with on-device models using ONNX and the ChatGPT model running in the cloud."
            ),
            "colin-marsch" to SessionInfo(
                id = "colin-marsch",
                speaker = "COLIN MARSCH",
                role = "Android Engineer at Cash App",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "12:10",
                subject = "Navigating the Unknown: Tips for Efficiently Learning a New Codebase",
                description = "Learning new codebases quickly is an essential skill for a successful career as an Android engineer. In this talk, we’ll explore why it is important to cultivate this skill and walkthrough a systematic approach to efficiently master a new codebase, from analyzing code architecture to stepping through key flows. The real value of learning a new codebase quickly is in the ability to accelerate development and share your knowledge with others. We’ll discuss the importance of documenting what you’ve learned and sharing it broadly. You’ll leave with practical strategies and tips for accelerating your learning process and becoming a valuable member of any development team."
            ),
            "daniel-galpin" to SessionInfo(
                id = "daniel-galpin",
                speaker = "DANIEL GALPIN",
                role = "Android Developer Advocate and Fast Talking YouTuber",
                location = "Robertson 1",
                date = "2023-06-09",
                time = "9:00",
                subject = "Android App Quality – A Google Perspective",
                description = "At Google I/O, we defined what app quality means to Google Play, focusing largely on what makes core experiences great. This session dives into three of the lesser-explored pillars from that work: technical app quality, privacy and security, and user experience, focusing on the work we’re doing on the Android platform, toolkit, and Google Play. We’ll cover problem areas as well as tools we’re providing to help solve these issues. Dan leads outreach for the Android Developer Relations team at Google, where he’s written blogs, developed videos, spoken at conferences, designed Udacity courseware, and written libraries for over eight years, and Kotlin + Jetpack is the thing that makes being on the Android team fun and exciting again."
            ),
            "daniel-horowitz" to SessionInfo(
                id = "daniel-horowitz",
                speaker = "DANIEL HOROWITZ",
                role = "Android @ Spotify",
                location = "Fisher East",
                date = "2023-06-09",
                time = "13:40",
                subject = "Fantastic tests and where to find them",
                description = "The more code we write the more we raise the risk of having issues on our apps. Our job as engineers is to mitigate those risks and how can we do that? With tests. We all agree that tests are vital to build a successful app but how can we know that we are testing the right things? How do we measure the quality of our tests? In this session we will go through these questions in a pragmatic approach, diving into every type of test in every aspect of the development phase. From communicating with backend to UI components, we will go through strategies and approaches on how to properly test each piece of code."
            ),
            "eddy-gann" to SessionInfo(
                id = "eddy-gann",
                speaker = "EDDY GANN",
                role = "YML, Senior Product Designer",
                location = "Fisher West",
                date = "2023-06-09",
                time = "10:55",
                subject = "Evolution of Location Permission in Android: From Install Time to Runtime and Beyond",
                description = """In recent years, location-based apps have become an integral part of the Android ecosystem. However, as user privacy concerns continue to rise, Google has made several changes to the location permission model in Android. This session will explore the evolution of location permission in Android, from install time permission to runtime permission and beyond. We will dive deep into the location permission break up of foreground and background, and the challenges posed by UI/UX design for asking permissions in a contextual way. As part of this session, we will discuss the location permission break up of foreground and background, and its impact on the development of location-based apps. We will explore the UI/UX challenges of asking permission in a contextual way, including best practices for designing permission requests that provide clear and concise information about why the permission is needed and how it will be used. Attendees of
                this session will gain a deeper understanding of the evolution of location permission in Android, as well as insights into the current state of location permission management . This session is suitable
            for developers of all levels and will provide valuable information
            for anyone working on location -based apps for Android."""
            ),
            "adam-greenberg" to SessionInfo(
                id = "adam-greenberg",
                speaker = "ADAM GREENBERG",
                role = "Senior Engineer - Pinterest",
                location = "Fisher West",
                date = "2023-06-09",
                time = "12:20",
                subject = "Getting rid of the REST: Migrations to GraphQL from REST",
                description = "Tried and true strategies for how we are migrating our client from a RESTful architecture to one based primarily in GraphQL. Based in using the Apollo Kotlin framework but will be applicable for all."
            ),
            "akshay-chordiya" to SessionInfo(
                id = "akshay-chordiya",
                speaker = "AKSHAY CHORDIYA",
                role = "Android Developer @ Tinder | Public Speaker | Kotlin Lover | Co-Author of Kotlin Blueprints",
                location = "Fisher East",
                date = "2023-06-09",
                time = "14:35",
                subject = "Go with the Flow",
                description = "Kotlin Flow is an implementation of Reactive Stream specification made on top of coroutines for Kotlin. In this talk, we will look at Kotlin Flow, it’s internals, how to use it with on Android or any other platforms, and share how you can migrate to Flow. Agenda - Quick intro to Flow - Internals of Kotlin Flow - Using Flow on Android and other platforms - Why and how to migrate - Working with existing reactive streams ❤ Flow in single codebase - Effectively using Flows with Compose - Magic of multi-platform - Tests - Conclusion You’ll walk away with a clear idea of what Kotlin Flow and how it works internally and how you can level up your reactive game."
            ),
            "elif-bilgin" to SessionInfo(
                id = "elif-bilgin",
                speaker = "ELIF BILGIN",
                role = "Google, Software Engineer",
                location = "Fisher East",
                date = "2023-06-09",
                time = "10:55",
                subject = "Journey to the Center of the Room: Generating Kotlin",
                description = "For the past few months, we have been working on building an abstraction supporting both Java’s Annotation Processing API and KSP called “XProcessing” with the ultimate goal of generating Kotlin code in the AndroidX library Room. Building for two similar but different languages definitely is a logistical and technical challenge - in this talk, we will do a deep dive into the “lessons learned” and best practices, when it comes to designing and implementing such an abstraction. Software Engineer at Google on the Android Toolkit & Jetpack Team. Specifically working in the AndroidX Library called Room."
            ),
            "andy-boedo" to SessionInfo(
                id = "andy-boedo",
                speaker = "ANDY BOEDO",
                role = "Senior Engineering Manager, CoreSDK team @RevenueCat",
                location = "Robertson 2",
                date = "2023-06-08",
                time = "11:15",
                subject = "The joys and challenges of building a paywall with BillingClient 5",
                description = "The time to migrate away from Google Play Billing Library 4 (PBL4) is halfway done, and developers will need to switch to PBL5 or - assuming it’s announced at I/O - PBL6. There are good reasons to migrate sooner, rather than later, though: In this talk, you’ll learn how the flexibility offered by BillingClient 5 enables new paywall configurations and functionality, with working examples for you to implement yourself. We’ll also highlight the challenges we ran in while upgrading the RevenueCat library to support these changes, many of which are incredibly relevant if you’re one of the hundreds of thousands of Android apps that still need to migrate away from PBL4 or even 3. You’ll leave the talk with a better understanding of real-world implementations of Billing Library 5, its benefits and its challenges, setting you up for a more successful migration down the road. Depending on this year’s I/O, we’ll update this talk with examples and insights pulled from PBL6 as they become available"
            ),
            "arun-babu-a-s-p" to SessionInfo(
                id = "arun-babu-a-s-p",
                speaker = "ARUN BABU A S P",
                role = "Senior Staff Engineer at Uber",
                location = "Fisher East",
                date = "2023-06-08",
                time = "11:15",
                subject = "Mobile Feature Flags and Experiments at Uber",
                description = "Mobile feature flag and experimentation platform at Uber provide product teams with the ability to gradually release new features, manage remote configurations, conduct controlled experiments independently of the mobile app release cycle, and empower data-driven decision-making. In this talk, we will explore the design of the mobile feature flag and experimentation SDK, the different challenges involved in delivering feature flag values and remote configurations rapidly to millions of users, and the methods utilized by Uber to protect its business during incident mitigation. Attendees will gain valuable insights into the best practices for mobile experimentation at scale, learn techniques for overcoming challenges when designing a reliable and robust system, and discover how Uber has successfully utilized experimentation to continuously enhance its mobile apps."
            ),
            "alex-vanyo" to SessionInfo(
                id = "alex-vanyo",
                speaker = "ALEX VANYO",
                role = "Android Developer Relations Engineer @ Google",
                location = "Fisher West",
                date = "2023-06-09",
                time = "13:40",
                subject = "Deep Dive Into Size Configuration Changes",
                description = "Join for an in-depth look at the interesting and sometimes surprising behavior around Android’s size configuration changes for orientation, width, and height. Learn how to calculate the window size correctly, and the edge cases and drawbacks of other approaches, and how size configuration changes recreate activities, and how sometimes they don’t! I’m an Android developer with a love of details. As a Developer Relations Engineer, I’m trying to improve APIs, write samples and share knowledge so that everyone can create polished, testable and beautiful apps. My currently focus is on large screens and Compose."
            ),
            "alejandro-sanchez" to SessionInfo(
                id = "alejandro-sanchez",
                speaker = "ALEJANDRO SANCHEZ",
                role = "Android @ Uber",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "16:50",
                subject = "Panel Discussion: Adopting Jetpack Compose @ Scale",
                description = "Over the last couple years, thousands of apps have embraced Jetpack Compose for building their Android apps. While everyone is using the same library, the approach they`ve taken in adopting it is really different on each team. There`s a lot of nuance in how one approaches a migration of this size and the difficulty is amplified when you are doing this at scale. This panel discussion brings together engineers working on popular apps that are using Compose and their experience in coordinating it`s adoption."
            ),
            "aleksandr-efremenkov" to SessionInfo(
                id = "aleksandr-efremenkov",
                speaker = "ALEKSANDR EFREMENKOV",
                role = "Software Engineer @ Bolt, Android GDE",
                location = "Fisher West",
                date = "2023-06-08",
                time = "16:50",
                subject = "Frida as debugging tool for Android",
                description = "We all have ever used the debugger in Android Studio, but it only works in debug mode (android:debuggable=`true`). What if we need to debug our application in production without the ability to reproduce the problem without the presence of logs? Frida allows you to instrument applications regardless of your environment. In this session, we`ll tinker with the Bolt app with a fictional bug inside and figure out how to fix it."
            ),
            // MISSING: ENRIQUE LOPEZ MANAS
            "brian-gardner" to SessionInfo(
                id = "brian-gardner",
                speaker = "BRIAN GARDNER",
                role = "Android Developer at Cash App",
                location = "Fisher West",
                date = "2023-06-08",
                time = "15:55",
                subject = "Find your way with GoogleMap() {}",
                description = "Maps are a crucial piece of many mobile apps today and there is no shortage of mapping libraries one can use. If you prefer to stick with platform components, you could use the OG MapView, but its integration can be painful due to asynchronous map loading. More recently, there is a GoogleMap Composable that streamlines much of this setup. This talk will cover my experience using this map Composable to implement a new feature in Cash App, including: - Configuring the map UI and how the user can interact with the map - Displaying map markers and clusters - Pain points encountered along the way including performance issues and cluster nuances - Some open issues facing the library"
            ),
            "carlos-mota" to SessionInfo(
                id = "carlos-mota",
                speaker = "CARLOS MOTA",
                role = "Lead Software Engineer at Avancee Software",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "12:20",
                subject = "Going on a road trip with Android Auto",
                description = "Android is truly everywhere. It`s running on the phone, watch, TV, car, and there are even some fridges and toasters that want to be part of the family. While they`re still under development, Android Auto is starting to gain a lot of adoption. In this talk, we`re going to travel around the road of Android Auto, see its use cases and go through its functionalities, so you can later implement them on your app. All of this without moving your desk to be inside a car."
            ),
            "catherine-chi" to SessionInfo(
                id = "catherine-chi",
                speaker = "CATHERINE CHI",
                role = "Android Platform @ Reddit",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "15:35",
                subject = "Tactics for Moving the Needle on Broad Modernization Efforts-Case Study Android Platform @ Reddit",
                description = "Successful platform teams drive major changes within their organizations but they cannot do the work alone. Explore some of the modernization efforts the Reddit platform team has driven across a large, diverse codebase, from monolith breakups to Compose adoption at scale. What worked, what challenges were faced, and learn how you can help your organization evolve successfully over time using similar tactics, no matter its scale. Key Points: * Cover modernization efforts applicable to companies of any size * Examine different approaches to large and small scale conversion efforts * Learn about some platform migration anti-patterns to avoid"
            ),
            "christina-lee" to SessionInfo(
                id = "christina-lee",
                speaker = "CHRISTINA LEE",
                role = "Android Engineer @ Pinterest",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "16:50",
                subject = "Panel Discussion Adopting Jetpack Compose @ Scale",
                description = """Over the last couple years, thousands of apps have embraced Jetpack Compose for building their Android apps. While everyone is using the same library, the approach they`ve taken in adopting it is really different on each team. There`s a lot of nuance in how one approaches a migration of this size and the difficulty is amplified when you are doing this at scale.
        This panel discussion brings together engineers working on popular apps that are using Compose and their experience in coordinating it`s adoption."""
            ), // MISSING: KURT NELSON
            "kyle-lehman" to SessionInfo(
                id = "kyle-lehman",
                speaker = "KYLE LEHMAN",
                role = "Principal Software Engineer, Comcast",
                location = "Fisher West",
                date = "2023-06-09",
                time = "16:30",
                subject = "Gaining the Benefits of Monorepo in a Polyrepo World",
                description = "Monorepo versus Polyrepo is as debatable as tabs versus spaces is the tech community but is it possible to have your cake and eat it too? In this talk, I will demonstrate how you can employ tactics in your Gradle builds that will have your polyrepo behaving more like a monorepo. Many of these strategies can even be applied to monorepos as well. In this session, you will gain insight into: - Building convention plugins to standardize all aspects of your builds including versioning, releasing, changelogs, static analysis, test configurations, and build optimizations - Maintaining published version catalog artifacts for both your internal and external dependencies so teams can easily keep up to date on available libraries and their latest version - Set up configurable, composite builds so you can develop your libraries as if it was another module in your main application build, shortening the feedback loop that comes with publishing to maven local. - Leveraging Renovate to automate keeping all your repos up to date on the latest code"
            ),
            "lauren-darcey" to SessionInfo(
                id = "lauren-darcey",
                speaker = "LAUREN DARCEY",
                role = "Android Platform @ Reddit",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "15:35",
                subject = "Tactics for Moving the Needle on Broad Modernization Efforts-Case Study: Android Platform @ Reddit",
                description = "Successful platform teams drive major changes within their organizations but they cannot do the work alone. Explore some of the modernization efforts the Reddit platform team has driven across a large, diverse codebase, from monolith breakups to Compose adoption at scale. What worked, what challenges were faced, and learn how you can help your organization evolve successfully over time using similar tactics, no matter its scale. Key Points: * Cover modernization efforts applicable to companies of any size * Examine different approaches to large and small scale conversion efforts * Learn about some platform migration anti-patterns to avoid"
            ),
            "manual-nakamurakare" to SessionInfo(
                id = "manual-nakamurakare",
                speaker = "MANUEL NAKAMURAKARE",
                role = "Pinterest, EM Mobile Builds",
                location = "Fisher East",
                date = "2023-06-08",
                time = "13:30",
                subject = "Build Health and Velocity Score: How Pinterest tracks the state of builds",
                description = "In this session, I`ll give a breakdown on how this score is calculated, what are some initiatives that help us make the build better and what other plans we have for the future"
            ),
            "mahesh-hada" to SessionInfo(
                id = "mahesh-hada",
                speaker = "MAHESH HADA",
                role = "Senior Software Engineer at Uber",
                location = "Fisher East",
                date = "2023-06-08",
                time = "11:15",
                subject = "Mobile Feature Flags and Experiments at Uber",
                description = "Mobile feature flag and experimentation platform at Uber provide product teams with the ability to gradually release new features, manage remote configurations, conduct controlled experiments independently of the mobile app release cycle, and empower data-driven decision-making. In this talk, we will explore the design of the mobile feature flag and experimentation SDK, the different challenges involved in delivering feature flag values and remote configurations rapidly to millions of users, and the methods utilized by Uber to protect its business during incident mitigation. Attendees will gain valuable insights into the best practices for mobile experimentation at scale, learn techniques for overcoming challenges when designing a reliable and robust system, and discover how Uber has successfully utilized experimentation to continuously enhance its mobile apps."
            ),
            "matt-ramotar" to SessionInfo(
                id = "matt-ramotar",
                speaker = "MATT RAMOTAR",
                role = "Dropboxer",
                location = "Robertson 1",
                date = "2023-06-09",
                time = "10:00",
                subject = "Meet Store5 – A Kotlin Multiplatform Library For Building Network-Resilient Applications",
                description = "Three years ago Store4, the little library that could, was released at KotlinConf`19. Store has been simplifying data loading on Android for close to a decade and was supercharged by being 100% Kotlin. Today we`re here to talk about the next paradigm shift in data loading Store5 - a Kotlin Multiplatform solution for reading, writing and resolving data conflicts on any platform that Kotlin supports (Android, iOS, Web and Desktop). The Android community has embraced Store for close to a decade, Kotlin is making it possible to adopt the same patterns on other mobile platforms and beyond. With the addition of support for updating remote sources, network resilience, pain free conflict resolution, and a highly extensible api - Store5 aims to make reading and writing data effortless on all Kotlin platforms. This talk will focus on Store5 foundational concepts and usage in production and at scale. We will be covering adopting KMP, applying Google`s offline-first guiding principles beyond Android and how we hope to establish a seamless way for all apps, regardless of platform, to work with local and remote data. This talk is not to be missed for folks (like us) who have battle scars from years of working on hard to fix bugs in offline first applications."
            ),
            "mark-villacampa" to SessionInfo(
                id = "mark-villacampa",
                speaker = "MARK VILLACAMPA",
                role = "SDK Developer @ RevenueCat",
                location = "Robertson 2",
                date = "2023-06-08",
                time = "11:15",
                subject = "The joys and challenges of building a paywall with BillingClient 5",
                description = "The time to migrate away from Google Play Billing Library 4 (PBL4) is halfway done, and developers will need to switch to PBL5 or - assuming it`s announced at I/O - PBL6. There are good reasons to migrate sooner, rather than later, though: In this talk, you`ll learn how the flexibility offered by BillingClient 5 enables new paywall configurations and functionality, with working examples for you to implement yourself. We`ll also highlight the challenges we ran in while upgrading the RevenueCat library to support these changes, many of which are incredibly relevant if you`re one of the hundreds of thousands of Android apps that still need to migrate away from PBL4 or even 3. You`ll leave the talk with a better understanding of real-world implementations of Billing Library 5, its benefits and its challenges, setting you up for a more successful migration down the road. Depending on this year`s I/O, we`ll update this talk with examples and insights pulled from PBL6 as they become available"
            ),
            "michael-krueger" to SessionInfo(
                id = "michael-krueger",
                speaker = "MICHAEL KRUEGER",
                role = "Sr. Director of Application Security at NowSecure",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "16:30",
                subject = "Let Standards Light Your Way: Best Practices for App Developers",
                description = "With an increasing focus on privacy and security, how do we avoid common app development pitfalls that get us in hot water? Common snippets on developer forums don`t always consider repercussions of an action. Join us as we walk through five seemingly innocuous implementations that have real world security implications and how you can apply standards like the OWASP MASVS to design your app with a security first mindset. In this interactive and entertaining session, see how to prevent these from happening to you and take home practical security and privacy best practices with links to more resources for you and your team."
            ), // TWOTALKS
            "michael-krueger-2" to SessionInfo(
                id = "michael-krueger-2",
                speaker = "MICHAEL KRUEGER",
                role = "Sr. Director of Application Security at NowSecure",
                location = "Fisher West",
                date = "2023-06-08",
                time = "12:10",
                subject = "Be Aware & Prepare: Grow Downloads & User Trust with a MASA Validation",
                description = "Looking to set your app apart from the rest on the Google Play Store? For 88% of users, how much personal data they share depends on how much they trust a company. Show customers you safeguard their data and are transparent with your privacy practices by completing a new Independent Security Review to get the badge on your Google Play Store Data safety declaration. In this session, learn about the new App Defense Alliance (ADA) Mobile App Security Assessment (MASA), launched Fall 2022 and how it can drive your business: - Learn about the ADA MASA validation process - Get tips on secure coding practices to speed your validation - See how & why so many other top Android apps have been validated. Get the inside scoop from NowSecure experts who helped create the ADA MASA framework and who have conducted hundreds of MASA assessments."
            ),
            "mike-nakhimovich" to SessionInfo(
                id = "mike-nakhimovich",
                speaker = "MIKE NAKHIMOVICH",
                role = "Twitter",
                location = "Robertson 1",
                date = "2023-06-09",
                time = "10:00",
                subject = "Meet Store5 – A Kotlin Multiplatform Library For Building Network-Resilient Applications",
                description = "Three years ago Store4, the little library that could, was released at KotlinConf`19. Store has been simplifying data loading on Android for close to a decade and was supercharged by being 100% Kotlin. Today we`re here to talk about the next paradigm shift in data loading Store5 - a Kotlin Multiplatform solution for reading, writing and resolving data conflicts on any platform that Kotlin supports (Android, iOS, Web and Desktop). The Android community has embraced Store for close to a decade, Kotlin is making it possible to adopt the same patterns on other mobile platforms and beyond. With the addition of support for updating remote sources, network resilience, pain free conflict resolution, and a highly extensible api - Store5 aims to make reading and writing data effortless on all Kotlin platforms. This talk will focus on Store5 foundational concepts and usage in production and at scale. We will be covering adopting KMP, applying Google`s offline-first guiding principles beyond Android and how we hope to establish a seamless way for all apps, regardless of platform, to work with local and remote data. This talk is not to be missed for folks (like us) who have battle scars from years of working on hard to fix bugs in offline first applications."
            ),
            "mike-wolfson" to SessionInfo(
                id = "mike-wolfson",
                speaker = "MIKE WOLFSON",
                role = "Lead Android Engineer for Adobe",
                location = "Fisher West",
                date = "2023-06-08",
                time = "11:15",
                subject = "Material You Review",
                description = "The introduction of Material Design was one of the more exciting things to happen to Android in years. It established a simple design system that enabled developers to apply universal styling patterns to create pleasing UI with minimal effort. Material has evolved throughout the years. The newest generation of the standard is named Material You, and is integrated directly with Jetpack Compose. It is a natural complement to writing Kotlin code, and is the perfect dynamic design system for all Android, Web, and Desktop apps. This session will be an introduction to design systems. I will discuss how you can apply color, typography, and dimension standards across your Applications universally. This results in a UI that is easy to refactor, aesthetically pleasing, and can be made a11y compliant. Using Material, I will explain fundamental aspects of design that will empower developers to feel more confident about UI, and enable better communication with designers."
            ),
            "nav-singh" to SessionInfo(
                id = "nav-singh",
                speaker = "NAV SINGH",
                role = "Senior Mobile Software Engineer at Manulife",
                location = "Fisher East",
                date = "2023-06-09",
                time = "15:35",
                subject = "What’s up with Android’s back?",
                description = "In this session, We would walk through the history of Android`s Back and its current state. - Activity`s onBackPressed () and KeyEvent#KEYCODE_BACK are marked as deprecated so what`s the solution? - As part of Android13, we will explore how to support the predictive back gesture in Android apps. - We will cover different scenarios to migrate to new APIs from older APIs. Or Can we still use older APIs?"
            ),
            "moataz-soliman" to SessionInfo(
                id = "moataz-soliman",
                speaker = "MOATAZ SOLIMAN",
                role = "CTO at Instabug",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "10:55",
                subject = "Beyond the Basics: Performance Monitoring and User Experience for Mobile App Growth",
                description = "When interacting with a mobile app that regularly crashes or freezes, 53% of users uninstalled the app, 37% stopped using it, and 28% looked for a replacement. Users are no longer forgiving mobile app mishaps and errors. Going beyond the basic metrics of understanding your user experience and diving deep into mobile performance is key to ensuring growth and positive user experiences. We will highlight which metrics and takeaways mobile teams should track to create a superior app experience that takes into account every user interaction. Find out why app insights, proactive issue detection, advanced debugging, and alert management capabilities should matter to you and the development of your mobile app."
            ),
            "nehal-kumar" to SessionInfo(
                id = "nehal-kumar",
                speaker = "NEHAL KUMAR",
                role = "Senior Software Engineer at Uber",
                location = "Fisher West",
                date = "2023-06-08",
                time = "10:20",
                subject = "Accelerating Mobile Development with Server Driven Technologies",
                description = "Mobile release cycles can be slow, leading to delays in feature launches and user feedback. This can be especially problematic for features that require multiple iterations to tweak UI, logic, and other elements. To address this issue, Uber developed a highly opinionated, type-safe, and interoperable Server-Driven Technology framework that allows teams to adopt it easily without rebuilding every aspect of their feature. In this talk, we will discuss the importance of the right guardrails, ensuring the right versioning, the dangers of the framework getting misused, and the need for robust testing mechanisms. We will also explore how we support multiple use cases for different features and teams in a generic way across multiple apps at Uber. Attendees will leave with a deep understanding & practical knowledge of building a robust Server-Driven framework and accelerating mobile development. By sharing our experiences at Uber, we hope to inspire other organizations to embrace Server-Driven Technologies and accelerate their mobile development cycles."
            ),
            "nick-dipatri" to SessionInfo(
                id = "nick-dipatri",
                speaker = "NICK DIPATRI",
                role = "Comcast Principal Engineer",
                location = "Fisher West",
                date = "2023-06-09",
                time = "15:35",
                subject = "Practical Compose Navigation with a Red Siren",
                description = "Stay alert to the pitfalls of Jetpack Compose Navigation! Together we’ll do live coding to take control of a red siren with an Android phone. Learn about the new nav components, explicit nav, backstack nav vs. popping, and how we manage state throughout. Finally we’ll discuss how you can use Compose Navigation in the same app with legacy navigation. Avoid navigation emergencies with this powerful new tool - help is on the way."
            ),
            "neal-michie" to SessionInfo(
                id = "neal-michie",
                speaker = "NEAL MICHIE",
                role = "PACE Anti-Piracy, Director of Product Management",
                location = "Fisher West",
                date = "2023-06-08",
                time = "14:25",
                subject = "Securing Sensitive Data: Android Keystore vs Whitebox Cryptography",
                description = "The need for security in mobile apps has never been greater. Most developers are aware of the need to encrypt sensitive data at rest and in transit; but how do you protect the cryptographic keys that unlock the data? This presentation will examine the solutions available - from hardware-backed options like the Android Keystore to pure software ones like Whitebox Cryptography. It will look at the pros and cons of the different solutions and highlight the use cases each is best suited to with real world examples."
            ),
            "olivier-tuchon" to SessionInfo(
                id = "olivier-tuchon",
                speaker = "OLIVIER TUCHON",
                role = "Reverse Engineer, Google Android Security",
                location = "Fisher East",
                date = "2023-06-08",
                time = "15:55",
                subject = "Securing Android Applications – The not so secret guide explained",
                description = """By making your apps more secure, you help preserve user trust and device integrity.
        This workshop presents a set of common security issues that Android app developers face: - Learn more about how to proactively secure your apps. - Understand how to react when one of these issues is discovered in your app. This workshop will be based on articles written in https://developer.android.com/topic/security/risks. For each entry, participants will be given an Android Application and its source code to illustrate the vulnerability. We will go through these examples to introduce tools to spot vulnerabilities and how to fix them."""
            ),
            "odin-asbjornsen" to SessionInfo(
                id = "odin-asbjornsen",
                speaker = "ODIN ASBJORNSEN",
                role = "Android Software Engineer, DNB Bank",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "11:15",
                subject = "Building a component library in Compose for a large-scale banking application",
                description = "Last year, we started migrating to Jetpack Compose at DNB Bank for all our Banking applications. We saw that our applications had a lot of components that could be abstracted away as simple APIs in Compose, which made it easier to keep our design consistent across our domain. In this talk, I will talk about how we started this initiative and how we set up the architecture for our design system. Then, I will explain a robust approach when creating components as atoms & molecules and how this helps us to collaborate with the designers in our teams. Moreover, I will be going through how we ensured banking-level quality for our compose components and which technologies we use today to make it easier for our developers to work with these libraries. Then, I will touch upon the release and dependency management of these libraries, and how we introduced this to our developers. In the end, I will be discussing the challenges and learnings for creating a component library with Compose. Join me in this session, where we will explore the beauties of library development with Jetpack Compose!"
            ),
            "nikhil-ramakrishnan" to SessionInfo(
                id = "nikhil-ramakrishnan",
                speaker = "NIKHIL RAMAKRISHNAN",
                role = "Senior Software Engineer at Uber",
                location = "Robertson 1",
                date = "2023-06-09",
                time = "12:20",
                subject = "Seamless mobile code merges",
                description = "At Uber, we support thousands of developers contributing to Android code. If builds are rapid, and stable, developer productivity is high. You’re working on a new mobile feature. It’s taken you weeks. You’ve been running your tests on CI. Your manager is asking you if you’ll hit the deadline. You feel confident, so you say yes. The day comes when you need to land your code. You try to merge it, but everyone’s merging code on the same day. Your code fails to merge, and your feature is delayed. Everyone is frustrated. We’ve all been there before. Keeping developer productivity high with our monorepo and thousands of developers is what we do at Uber. In my presentation, I will be discussing how you can do seamless mobile code merges at scale. These are some of the key points I will be talking about: - Pre merge validation - Post merge validation - Cache freshness - Parallelism/sharding - Merge queue optimizations - Optimizing for cost vs performance - Success metrics"
            ),
            "ralf-wondratschek" to SessionInfo(
                id = "ralf-wondratschek",
                speaker = "RALF WONDRATSCHEK",
                role = "Principal Engineer at Amazon",
                location = "Robertson 2",
                date = "2023-06-08",
                time = "10:20",
                subject = "Managing State Beyond ViewModels and Hilt",
                description = "Separation of concerns is a common best practice followed by all successful software projects. In Android applications, there is usually a UI layer, a data layer, and a domain layer. Given the infinite number of ways to implement these layers, it`s not clear how to get started quickly. Therefore, many projects follow Google`s high-level guide to app architecture, which suggests many reasonable defaults and best practices. But how do the proposed recommendations work in practice? How do you set up an architecture that is ready for your use cases with this guide in mind? Are suggestions such as using Hilt as the backbone for your architecture really a good recommendation? This talk will discuss best practices for taking ownership of your own architecture, decoupling your business logic from Android components, and creating and managing scopes for your use cases. There will be concrete advice on how to loosely couple classes in the data and domain layers, how to prevent memory and thread leaks, and how to adopt the dependency injection framework of your choice."
            ),
            "pavlo-stavytskyt" to SessionInfo(
                id = "pavlo-stavytskyt",
                speaker = "PAVLO STAVYTSKYI",
                role = "Sr. Staff Software Engineer at Turo, Google Dev Expert for Android, Kotlin",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "13:40",
                subject = "How we reduced startup time of Turo Android app by 77%",
                description = "The startup time of a mobile app is one of the most important indicators of its performance and has a significant impact on the user experience. A fast-loading app not only provides a positive first impression, but also increases engagement and retention, reduces user churn, and can improve visibility in app store rankings. In this talk, I`m going to share how the startup time of the Turo Android app was reduced by 77%. You will learn how to apply best practices and Android developer tools to improve the startup performance of your Android apps."
            ),
            "rooz-mohazzabi" to SessionInfo(
                id = "rooz-mohazzabi",
                speaker = "ROOZ MOHAZZABI",
                role = "Android Dev Rel at Gradle",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "13:30",
                subject = "Improving Developer Experience with Gradle Build Scans",
                description = "As a developer, you just want your builds to work but every now and then things go wrong and you need to investigate the build which sucks up a lot of your time not to mention context switching. In this talk we show you how build scans can help save a lot of time when you need to find build performance bottlenecks, investigate/troubleshoot CI failures, build/tool chain failures, visualize your dependencies, etc. to troubleshooting/investigating build issues and accelerate your builds/tests. From this session you will learn: - Can I make my build faster? Build performance profiling with a simple UI: what are all the performance bottlenecks in my build, how well is my build cache working, the impact of heap, garbage, dependency downloads on my build times. - How well is my build parallelized? Can I get more parallelization to speed up my builds? - What`s happening in my build? View the build timeline view to see what tasks/goals are being executed in which order. You can use this to see how well parallelized is your build - Git and CI integration for better workflow. Go from your CI job to your build scan and back. Go to the Git source repo, git commit id, show me all the builds on this git commit id - Troubleshoot CI failures way faster with a build scan by viewing failed taks/goal in consol log. Build scans pull out all the details pertaining to your failure out of the consol log so you don’t have to parse through thousands of lines of logs - Dependency Search: view, search through your projects dependencies and their versions in a few clicks - Triage Test failures: The test dashboard sorts your projects test failures by failure count with a nice UI. With a few clicks you can see which test classes failed the most, sort by your longest tests, test methods with the most test failures, link to your test report - Tool chain and build related failures: What’s breaking my build?"
            ),
            "rikin-marfatia" to SessionInfo(
                id = "rikin-marfatia",
                speaker = "RIKIN MARFATIA",
                role = "Indie Android Developer",
                location = "Fisher West",
                date = "2023-06-09",
                time = "9:00",
                subject = "Unlocking the Power of Shaders in Android with AGSL and Jetpack Compose",
                description = "Want to learn some new ways to create engaging, interactive, and visually stunning UIs?Shaders are scary but powerful tools that, when wielded correctly, can help us make some cool and complex effects. With the new AGSL, we can create custom programmable shaders that can really spruce up our Android apps and make our UIs pop! In this talk, we will dive into the world of shaders and AGSL, and explore how we can use them in a practical way. We will cover some shader basics to build a foundation. Then we will learn a bit about AGSL and how we can setup Runtime Shaders in a Compose UI app. Finally we`ll look at some really cool examples of shader-driven UI in the wild. Key Takeaways: - Gain a deeper understanding of Shaders and AGSL - Learn how to integrate shaders with Compose - See some practical use-cases for shaders in an Android app - Become comfortable with complex tech and push the boundaries of Android UIs"
            ),
            "russell-wolf" to SessionInfo(
                id = "russell-wolf",
                speaker = "RUSSELL WOLF",
                role = "Kotlin Multiplatform at Touchlab",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "9:00",
                subject = "Kotlin Multiplatform: From “Hello World” to the Real World",
                description = "By now you’ve surely heard of Kotlin Multiplatform, and maybe tried it out in a demo. Maybe you’ve even integrated some shared code into a production app. If you have, you know that there are many subtle complications that come up when you want to ship shared Kotlin code. This includes things like modularization, translating between Kotlin and Swift, managing multiple repositories that depend on each other, and optimizing build times and binary sizes. It’s not as easy as it looks when you write your first “Hello World”! At Touchlab, we’ve been involved with Kotlin Multiplatform since the very beginning, and we’ve learned a thing or two along the way about what does and doesn’t work well. Come hear about how we’ve solved some of these difficulties to ship Multiplatform code across all sorts of organizations and environments, so you’re ready to use KMP in the real world."
            ),
            "savannah-forood" to SessionInfo(
                id = "savannah-forood",
                speaker = "SAVANNAH FOROOD",
                role = "Android Platform @ Reddit",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "15:35",
                subject = "Tactics for Moving the Needle on Broad Modernization Efforts-Case Study Android Platform @ Reddit",
                description = "Successful platform teams drive major changes within their organizations but they cannot do the work alone. Explore some of the modernization efforts the Reddit platform team has driven across a large, diverse codebase, from monolith breakups to Compose adoption at scale. What worked, what challenges were faced, and learn how you can help your organization evolve successfully over time using similar tactics, no matter its scale. Key Points: * Cover modernization efforts applicable to companies of any size * Examine different approaches to large and small scale conversion efforts"
            ),
            "shannon-lee" to SessionInfo(
                id = "shannon-lee",
                speaker = "SHANNON LEE",
                role = "Kobiton, Lead Solutions Engineer",
                location = "Robertson 2",
                date = "2023-06-08",
                time = "14:25",
                subject = "We Hear You! Audio & Voice Testing on Android Devices",
                description = "We hear you! Testing audio feedback on mobile devices can be challenging at best, and near impossible at worst. With the maturity of virtual assistants, the rise of accessibility features, and the growing popularity of AI generated chat bots, audio and voice are becoming an integral part of interacting with mobile devices. But how do you test these new technologies and features to ensure the best user experience? Join this lightning talk and see how testing audio on Android can be easier than you think, and what’s next in the world of audio testing on mobile devices."
            ),
            "sagar-das" to SessionInfo(
                id = "sagar-das",
                speaker = "SAGAR DAS",
                role = "Staff Android Engineer at Vivint Smart Home",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "14:35",
                subject = "Identify Android App Startup time issues",
                description = "Reducing App startup time is crucial for improving user experience and retaining your user base in the long run. It is important for an Android developer to understand what are the common issues for slower app startup time and how to discover those using the built int tools provided by Android Studio. This talk will cover: 1. CPU flame charts. 2. CPU profiler and Memory profiler in Android Studio. 3. How to read a CPU flame chart for an Android app and discover issues with app startup time. 4. All the slides content will be explained using Kotlin code in Android Studio. Attendees will learn: 1. What is the mental model for measuring performance of an Android app. 2. How to profile an Android app. 3. How to identify and reduce app startup time in order to improve the user experience."
            ),
            "sreekumar-anjali-bhavan-vijayan" to SessionInfo(
                id = "sreekumar-anjali-bhavan-vijayan",
                speaker = "SREEKUMAR ANJALI BHAVAN VIJAYAN",
                role = "Lead Android Engineer @ YML",
                location = "Fisher West",
                date = "2023-06-09",
                time = "10:55",
                subject = "Evolution of Location Permission in Android: From Install Time to Runtime and Beyond",
                description = "In the early days of Android, developers had to request location permission during the installation process. Users had to either grant permission for location access or not install the app altogether. However, this model was not user-friendly and left users feeling uneasy about granting location permission to all apps. To address this issue, Google introduced the concept of runtime permission, allowing users to grant or deny location permission to an app when they are using it. This improved user privacy and control over location access, but it also introduced new challenges for developers, including permission handling in the background. As part of this session, we will discuss the location permission break up of foreground and background, and its impact on the development of location-based apps. We will explore the UI/UX challenges of asking permission in a contextual way, including best practices for designing permission requests that provide clear and concise information about why the permission is needed and how it will be used. Attendees of this session will gain a deeper understanding of the evolution of location permission in Android, as well as insights into the current state of location permission management. This session is suitable for developers of all levels and will provide valuable information for anyone working on location-based apps for Android. Key takeaways: - The evolution of location permission in Android - Location permission break up of foreground and background - UI/UX challenges for asking permissions in a contextual way - Best practices for designing permission requests"
            ),
            "shrikant-ballal" to SessionInfo(
                id = "shrikant-ballal",
                speaker = "SHRIKANT BALLAL",
                role = "Staff Engineer - Android at YML",
                location = "Fisher East",
                date = "2023-06-09",
                time = "11:25",
                subject = "From Chaos to Consistency: Managing Build and Release for 25+ Android Repos with Github Actions",
                description = "Managing the build and release process for over 25 Android repositories can be a daunting task. With each repository having its own pipeline or workflow, it can become difficult to ensure consistency and quality across all the repositories. In this talk, we will share our experience of how we tackled this challenge at YML by using a centralized Github Actions approach. We will discuss how we created reusable workflows that can be easily shared across all the repositories, reducing the need to maintain multiple pipelines. We will also cover the various checks that we execute for each repository, such as unit tests, integration tests, and code quality checks. These checks help us to catch issues early and ensure that we are always release-ready. By using a centralized Github Actions approach, we have been able to streamline our build and release process, reduce the time and effort required to maintain multiple pipelines, and ensure that our code quality remains consistent across all the repositories. Join us to learn more about this approach and how it can benefit your organization."
            ),
            "steven-schoen" to SessionInfo(
                id = "steven-schoen",
                speaker = "STEVEN SCHOEN",
                role = "Android UI Platform @ Reddit",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "13:35",
                subject = "Tactics for Moving the Needle on Broad Modernization Efforts-Case Study: Android Platform @ Reddit",
                description = "Successful platform teams drive major changes within their organizations but they cannot do the work alone. Explore some of the modernization efforts the Reddit platform team has driven across a large, diverse codebase, from monolith breakups to Compose adoption at scale. What worked, what challenges were faced, and learn how you can help your organization evolve successfully over time using similar tactics, no matter its scale. Key Points: * Cover modernization efforts applicable to companies of any size * Examine different approaches to large and small scale conversion efforts * Learn about some platform migration anti-patterns to avoid"
            ),
            "syed-hamid" to SessionInfo(
                id = "syed-hamid",
                speaker = "SYED HAMID",
                role = "CEO - Sofy",
                location = "Fisher East",
                date = "2023-06-08",
                time = "14:55",
                subject = "The GPT-ification of Mobile App Testing",
                description = "It`s no secret that OpenAI`s ChatGPT has taken the tech industry by storm. In fact, in the tech sector, it`s difficult to miss discussion about how the jobs of today will inevitably be impacted by GPT-3.5 and, recently, GPT-4. With the arrival of Microsoft 365 Copilot and the massive amount of GitHub data Microsoft has used to train it, it`s evident that OpenAI`s large language models will also transform the software development process. In this discussion, Sofy CEO and founder Syed Hamid will share a variety of use cases that provide insight into how this technology is changing today`s software testing landscape. Syed will also discuss how Sofy has harnessed the power of GPTs to further amplify Sofy`s revolutionary no-code mobile app testing approach."
            ),
            "tadeas-kriz" to SessionInfo(
                id = "tadeas-kriz",
                speaker = "TADEAS KRIZ",
                role = "Senior Mobile Developer at Touchlab",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "11:25",
                subject = "Meta-programming with Kotlin compiler plugins",
                description = "A sequel to my previous talk about KSP and Kotlin compiler plugins. This time focusing on the new K2 compiler, FIR and the possibilities of frontend compiler plugins. Learn what the new K2 compiler brings and how to update your plugins to support K2. Augment your code and get rid of boilerplate!"
            ),
            "tasha-ramesh" to SessionInfo(
                id = "tasha-ramesh",
                speaker = "TASHA RAMESH",
                role = "Android @ Tinder",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "10:20",
                subject = "Creative Coding with Compose: The Next Chapter",
                description = "Creative coding bridges the worlds of art and technology, offering expressive and innovative opportunities for data visualization, art installations, interaction design, and even games. Jetpack Compose makes these possibilities even more accessible on Android! Building upon a previous Droidcon New York talk, this revisits the core concepts of creative coding, focusing on generative and interactive art. We`ll also venture into image processing techniques and discuss their implementation in Compose. With practical examples, we`ll gain a deeper understanding of the creative coding landscape and acquire tools that can be applied to enhance UX or simply have fun exploring new artistic realms!"
            ),
            "tatiana-solonets" to SessionInfo(
                id = "tatiana-solonets",
                speaker = "TATIANA SOLONETS",
                role = "Staff Software Engineer at Life360",
                location = "Robertson 2",
                date = "2023-06-08",
                time = "14:55",
                subject = "Building Multiprocess Android Applications",
                description = "Today we put more and more functionality into the client. Each Android app has a memory budget for its execution that cannot be exceeded. That limit is enforced in a per-process basis. A separate process can take advantage of its own RAM budget, allowing the main process to have more space for its resources. Key takeaways and learning points: - Pros and Cons of having more than one process - How to create a multiprocess Android applications - Understanding of the Process lifecycle - Ways to implement IPC - How Life360 transformed internal event streaming system to deliver events in multiprocess environment"
            ),
            "tor-norbye" to SessionInfo(
                id = "tor-norbye",
                speaker = "TOR NORBYE",
                role = "Engineering Director for Android Studio",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "9:00",
                subject = "Keynote: Android Developer Productivity",
                description = "Android Studio is at the center of the Android developer’s universe. In this keynote, Tor Norbye will share his perspective on how to get the most out of the IDE, covering his favorite features, some hidden gems, and some recent additions. Last but not least, he`ll cover some upcoming features under active development."
            ),
            "ty-smith" to SessionInfo(
                id = "ty-smith",
                speaker = "TY SMITH",
                role = "Principal Engineer at Uber, Mobile Platform",
                location = "Robertson 1",
                date = "2023-06-09",
                time = "14:35",
                subject = "Balancing Speed and Reliability: The Double-Edged Sword of Third-Party Libraries",
                description = "Using third-party libraries in your apps can be a great way to save engineering time and move faster, but can also bring significant risk. If a library malfunctions and causes an outage, it may take days or weeks to get it solved for all your users. Apps have long update cycles and don’t have the luxury of hotfixes when something goes wrong. At Uber, as an app that people rely upon for making their income, getting to the doctor, or commuting to work, reliability in our app is the top priority. Learn how Uber decides when mobile libraries are safe to include and when they should be avoided. We’ll review how Uber analyzes external libraries to reduce risk, walk through some horror stories when things went wrong, and discover some techniques that can help keep reliability for your user when the worst does happen. You’ll walk away with a tactical framework for evaluating libraries in your own apps."
            ),
            "vitalii-markus" to SessionInfo(
                id = "vitalii-markus",
                speaker = "VITALII MARKUS",
                role = "Android Engineer @ Flo Health Inc.",
                location = "Fisher East",
                date = "2023-06-09",
                time = "9:00",
                subject = "Tricky Back-Compatible Localization",
                description = "In the main part, I want to speak about the new Android 13 feature Per -app language preferences which was introduced for all Android versions in AppCompat-1.6.0 this January. I will explain how it works under the hood and what is wrong with how it was backported to Android 12 via AppCompat Library. I will show how AppCompat Library replaces resources, recreates the whole activity stack, syncs locales, etc. Indeed, the experience is not the same for all Android versions, which we should keep in mind. I am eager to share my experience and suggest my solution for discussion and how I was able to unify it. To spice it up, I have my story of ups and downs, which is quite entertaining and, at the same time, useful for the audience; how I broke the app for Indonesian users because of locales."
            ),
            "vinay-gaba" to SessionInfo(
                id = "vinay-gaba",
                speaker = "VINAY GABA",
                role = "Android @ Airbnb",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "16:50",
                subject = "Panel Discussion: Adopting Jetpack Compose @ Scale",
                description = "Over the last couple years, thousands of apps have embraced Jetpack Compose for building their Android apps. While everyone is using the same library, the approach they`ve taken in adopting it is really different on each team. There`s a lot of nuance in how one approaches a migration of this size and the difficulty is amplified when you are doing this at scale. This panel discussion brings together engineers working on popular apps that are using Compose and their experience in coordinating it`s adoption."
            ),
            "zachary-sweigart" to SessionInfo(
                id = "zachary-sweigart",
                speaker = "ZACHARY SWEIGART",
                role = "Staff Android Engineer @ Faire",
                location = "Fisher West",
                date = "2023-06-08",
                time = "14:55",
                subject = "We want go fast: Performance at Faire",
                description = "Throughout Faire’s history, we’ve primarily focused on adding value for our users by virtue of rapidly delivering features to the end user. This has, historically, been a sensible prioritization. Faire’s two-sided marketplace is composed of generally high-intent users. Since we can assume a higher level of tolerance for latency for high-intent users, we’ve often dismissed the importance of performance. However, industry research has shown that one-second delay in response can result in a 7% loss in conversion. With that in mind, Faire set out to improve our app speed. In this talk we will cover how we approached improving our app speed (Define, Measure, Analyze, Improve, Contro), what tools and techniques we used, our progress towards our goals and our next planned steps."
            ),
            "zach-klippenstein" to SessionInfo(
                id = "zach-klippenstein",
                speaker = "ZACH KLIPPENSTEIN",
                role = "Composer at Google",
                location = "Robertson 1",
                date = "2023-06-08",
                time = "15:55",
                subject = "Reimagining text fields in Compose",
                description = "The Compose Text team is completely rethinking the text field APIs from scratch. Come learn why, how we`re approaching the process, and get a sneak peak at what the future might look like."
            ),
            "zhenlei-ji" to SessionInfo(
                id = "zhenlei-ji",
                speaker = "ZHENLEI JI",
                role = "Android Engineer, Script Addicted, Origami Enthusiast",
                location = "Fisher West",
                date = "2023-06-09",
                time = "14:35",
                subject = "A shortcut for your productivity in Android Studio",
                description = "Throughout my journey as a developer, I`ve spent countless hours in front of Android Studio. In order to be more productive, it was important for me to know the right shortcuts and how to use them in the most effective way. In this talk, we will learn how to develop a shortcut mindset and explore many shortcuts with real world examples in order to make your life as a developer much easier. Finally, I will share my favorite plugins to help you get the most out of Android Studio. After this talk, your daily work won`t be the same. These shortcuts will boost your efficiency to another level and make it less error prone, allowing you to focus on what really matters."
            ),
            "zach-klippenstein-speaker-2" to SessionInfo(
                id = "zach-klippenstein-speaker-2",
                speaker = "zach klippenstein",
                role = "Composer at Google",
                location = "Robertson 2",
                date = "2023-06-09",
                time = "10:00",
                subject = "Opening the shutter on snapshots",
                description = "Jetpack Compose shows the power of a custom compiler plugin. But not all the magic happens during compilation. A lot of Compose features are based on a runtime library that doesn`t require any compiler support: the snapshot system. It might seem like magic at first, but it`s just built on top of things you might already know: ThreadLocals, linked lists, and, yes, even regular old callbacks. Once you understand how Compose thinks about state, you might find ways to use its tools in your own code – even outside of Compose."
            ),
            "pierre-yves-ricau" to SessionInfo(
                id = "pierre-yves-ricau",
                speaker = "PIERRE-YVES RICAU",
                role = "Android Plumber",
                location = "Robertson 1",
                date = "2023-06-09",
                time = "13:40",
                subject = "The Subtle Art of Profiling Android Apps",
                description = "How do you investigate a performance issue that you can reproduce? Google has built several powerful yet intimidating investigation tools. The first time I opened Perfetto, I laughed and closed it immediately. Where should you start? What signals should you look at, in what order, and how do you choose the right tool for your situation? This talk will provide a systematic and concrete approach to analyzing Android performance problems, by using the right tools in the right order."
            ),
            "michael-krueger-speaker-2" to SessionInfo(
                id = "michael-krueger-speaker-2",
                speaker = "MICHAEL KRUEGER",
                role = "Sr. Director of Application Security at NowSecure",
                location = "Fisher West",
                date = "2023-06-08",
                time = "12:10",
                subject = "Be Aware & Prepare: Grow Downloads & User Trust with a MASA Validation",
                description = "Looking to set your app apart from the rest on the Google Play Store? For 88% of users, how much personal data they share depends on how much they trust a company. Show customers you safeguard their data and are transparent with your privacy practices by completing a new Independent Security Review to get the badge on your Google Play Store Data safety declaration. In this session, learn about the new App Defense Alliance (ADA) Mobile App Security Assessment (MASA), launched Fall 2022 and how it can drive your business: - Learn about the ADA MASA validation process - Get tips on secure coding practices to speed your validation - See how & why so many other top Android apps have been validated. Get the inside scoop from NowSecure experts who helped create the ADA MASA framework and who have conducted hundreds of MASA assessments."

            )
        )
    }
}