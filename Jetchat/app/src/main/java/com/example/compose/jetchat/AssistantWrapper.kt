package com.example.compose.jetchat

import android.content.Context
import android.util.Log
import androidx.compose.ui.input.key.Key.Companion.Sleep
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.Assistant
import com.aallam.openai.api.assistant.AssistantId
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.FunctionMode
import com.aallam.openai.api.chat.FunctionTool
import com.aallam.openai.api.chat.ToolId
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.message.FileCitationAnnotation
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.run.FunctionToolCallStep
import com.aallam.openai.api.run.RunRequest
import com.aallam.openai.api.run.ToolCallStep
import com.aallam.openai.api.run.ToolCallStepDetails
import com.aallam.openai.api.run.ToolOutputBuilder
import com.aallam.openai.api.run.toolOutput
import com.aallam.openai.client.OpenAI
import com.example.compose.jetchat.data.CustomChatMessage
import com.example.compose.jetchat.data.DroidconDbHelper
import com.example.compose.jetchat.data.EmbeddingHistory
import com.example.compose.jetchat.data.HistoryDbHelper
import com.example.compose.jetchat.data.SlidingWindow
import com.example.compose.jetchat.functions.AddFavoriteFunction
import com.example.compose.jetchat.functions.AskWikipediaFunction
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.log

/** Uses OpenAI Kotlin lib to call chat model */
@OptIn(BetaOpenAI::class)
class AssistantWrapper(val context: Context?) {
    private val openAIToken: String = Constants.OPENAI_TOKEN
    private var openAI: OpenAI = OpenAI(openAIToken)

    private var assistant: Assistant? =null
    private var thread: com.aallam.openai.api.thread.Thread? = null

    suspend fun chat(message: String): String {

        if (assistant == null) {
            // open assistant and create a new thread
            // every app-start
            assistant = openAI.assistant(id = AssistantId(Constants.OPENAI_ASSISTANT_ID))
            thread = openAI.thread()
        }

        val userMessage = openAI.message (
            threadId = thread!!.id,
            request = MessageRequest(
                role = Role.User,
                content = message
            )
        )

        val run = openAI.createRun(
            threadId = thread!!.id,
            request = RunRequest(assistantId = assistant!!.id)
        )

        do
        {
            delay(1_000)
            val runTest = openAI.getRun(thread!!.id, run.id)
            Log.v("LLM-A", "Status: " + runTest.status)

            if (runTest.status == Status.RequiresAction){
                val steps = openAI.runSteps(thread!!.id, run.id)
                val stepDetails = steps[0].stepDetails
                if (stepDetails is ToolCallStepDetails) {
                    val toolCallStep = stepDetails.toolCalls!![0]
                    if (toolCallStep is ToolCallStep.FunctionTool) {
                        var function = toolCallStep.function
                        Log.i("LLM-A", "${toolCallStep.id} ${function.name} ${function.arguments}")
                        var functionResponse = ""
                        when (function.name) {
                            AskWikipediaFunction.name() -> {
                                var functionArgs = argumentsAsJson(function.arguments) ?: error("arguments field is missing")
                                val query = functionArgs.getValue("query").jsonPrimitive.content
                                // CALL THE FUNCTION!!
                                functionResponse = AskWikipediaFunction.function(query)
                            }
                            else -> {
                                Log.i("LLM-A", "Function ${function!!.name} does not exist")
                                functionResponse = "Error: could not retrieve any information."
                            }
                        }
                        // Send back to chat (submit_tool_outputs_
                        val to = toolOutput {
                            toolCallId = ToolId(toolCallStep.id.id)
                            output = functionResponse
                        }
                        openAI.submitToolOutput(thread!!.id, run.id, listOf(to))
                        delay(1_000) // wait before polling again
                   }
                }
            }
        } while (runTest.status != Status.Completed)

        val messages = openAI.messages(thread!!.id)
        val message =messages.first()
        val messageContent = message.content[0]
        if (messageContent is MessageContent.Text) {
            var cites = ""
            if (messageContent.text.annotations.isNotEmpty()) {
                for (annot in messageContent.text.annotations) {
                    if (annot is FileCitationAnnotation) {
                        cites += "\n" + annot.text + " fileId: " + annot.fileCitation.fileId + ""
                    }
                }
            }
            return messageContent.text.value + cites
        }
        if (messageContent is MessageContent.Image) {
            return "TODO: image file id:" + messageContent.fileId
        }
        return "<Assistant Error>"
    }

    /** OpenAI generate image from prompt text.
     * Returns a URL to the image, must be downloaded or
     * rendered from the URL (no bytes returned from API)
     * @return image URL or empty string */
    suspend fun imageURL(prompt: String): String {
        val imageRequest = ImageCreation(prompt = prompt, model = ModelId(Constants.OPENAI_IMAGE_MODEL))

        // OpenAI network request
        val images: List<ImageURL> = openAI.imageURL(imageRequest)

        return if (images.isEmpty()) "" else images[0].url
    }

//    inline fun <reified T> decodeJson(jsonString: String): T = Json.decodeFromString(jsonString)

    public fun argumentsAsJson(arguments:String, json: Json = Json): JsonObject = json.decodeFromString(arguments)
}
