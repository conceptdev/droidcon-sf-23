# droidcon SF 2023

Android app demos for droidcon San Francisco, 9th June 2023.

There are four demos:

- [Source Editor](SourceEditor/) - LLM Completion API
- [JetchatAI](Jetchat/) - LLM Chat API and LLM Embeddings API, plus Function Calling
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

Add an OpenAI key in **Constants.kt** to use the chat and embeddings endpoints.

- [Introduction](https://platform.openai.com/docs/introduction)
- [Chat documentation](https://platform.openai.com/docs/guides/chat)
- [Chat API](https://platform.openai.com/docs/api-reference/chat)
- [Chat Playground](https://platform.openai.com/playground?mode=chat)

#### OpenAI Chat Functions

Implements the OpenAI [Chat Function Calling](https://platform.openai.com/docs/guides/gpt/function-calling)
feature using the [Weather.gov](https://www.weather.gov/documentation/services-web-api)
web service endpoint. Add a unique identifier for the `WEATHER_USER_AGENT` in **Constants.kt**
according to the requirements on the API information page.

### Google PaLM

Add a PaLM key in **Constants.kt** to use the chat and embeddings endpoints.

- [Overview](https://developers.generativeai.google/guide/palm_api_overview)
- [Chat quickstart](https://developers.generativeai.google/tutorials/chat_android_quickstart)
- [REST API](https://developers.generativeai.google/api/rest/generativelanguage)
- [Chat MakerSuite](https://makersuite.google.com/app/prompts/new_multiturn)

### Blog

Original project blogged at [Jetchat with OpenAI on Android](https://devblogs.microsoft.com/surface-duo/android-openai-chatgpt-5/) on [devblogs.microsoft.com](https://devblogs.microsoft.com/surface-duo/)