# droidcon SF 2023

Android app demos for droidcon San Francisco, 9th June 2023.

There are four demos:

- [Source Editor](SourceEditor/) - LLM Completion API
- [JetchatAI](Jetchat/) - LLM Chat API and LLM Embeddings API
- [ONNX Object Detection](ObjectDetection-ONNX/) - local image processing model
- [ONNX Whisper](Whisper-ONNX/) - local version of OpenAI speech-to-text model

The first two are included in this repo, the second two are linked to their original source.

## Source Editor (OpenAI)

Summarization feature with HTML content.

- [Completion documentation](https://platform.openai.com/docs/api-reference/completions)
- [Complation API](https://platform.openai.com/docs/api-reference/completions/create)
- Blog: [OpenAI API endpoints](https://devblogs.microsoft.com/surface-duo/android-openai-chatgpt-3/)

## JetchatAI

Chat interface with LLM back-ends for OpenAI and PaLM. Originally blogged as [Jetchat with OpenAI on Android](https://devblogs.microsoft.com/surface-duo/android-openai-chatgpt-5/).

### OpenAI

- [Introduction](https://platform.openai.com/docs/introduction)
- [Chat documentation](https://platform.openai.com/docs/guides/chat)
- [Chat API](https://platform.openai.com/docs/api-reference/chat)
- [Chat Playground](https://platform.openai.com/playground?mode=chat)

### Google PaLM

- [Overview](https://developers.generativeai.google/guide/palm_api_overview)
- [Chat quickstart](https://developers.generativeai.google/tutorials/chat_android_quickstart)
- [REST API](https://developers.generativeai.google/api/rest/generativelanguage)
- [Chat MakerSuite](https://makersuite.google.com/app/prompts/new_multiturn)

### Blog

Original project blogged at [Jetchat with OpenAI on Android](https://devblogs.microsoft.com/surface-duo/android-openai-chatgpt-5/) on [devblogs.microsoft.com](https://devblogs.microsoft.com/surface-duo/)

### Forked from Jetpack Compose Samples

Original source for the chat app can be found at:

https://github.com/android/compose-samples/

| Project | |
|:-----|---------|
|  <img src="https://github.com/android/compose-samples/raw/main/readme/jetchat.png" alt="Jetchat" width="240"></img> <br><br>A sample chat app that focuses on UI state patterns and text input.<br><br>• Low complexity<br>• Material Design 3 theme and Material You dynamic color<br>• Resource loading<br>• Back button handling<br>• Integration with Architecture Components: Navigation, Fragments, LiveData, ViewModel<br>• Animation<br>• UI Testing<br><br>**[> Browse](https://github.com/android/compose-samples/raw/main/Jetchat/)** <br><br> | <img src="https://github.com/android/compose-samples/raw/main/readme/screenshots/Jetchat.png" width="320" alt="Jetchat sample demo">|
|  |  |
