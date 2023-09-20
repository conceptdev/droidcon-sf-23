package com.example.compose.jetchat.data

import android.content.ContentValues
import android.util.Log
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.example.compose.jetchat.Constants
import com.example.compose.jetchat.dot
import java.util.SortedMap

/**
 * Store and retrieve messages from the conversation
 * calculating an embedding vector when they're saved
 * for later retrieval
 */
class EmbeddingHistory {
    companion object {
        /** key'd map of vector to message chunk */
        private var historyCache: MutableMap<DoubleArray, String> = mutableMapOf()

        /**
         * Calculate an embedding vector for this message pair (user query+assistant reply)
         * and save it to database/memory cache
         */
        suspend fun storeInHistory (openAI: OpenAI, dbHelper: HistoryDbHelper, user: CustomChatMessage, bot: CustomChatMessage) {

            val contentToEmbed = user.userContent + " " + bot.userContent

            val embeddingRequest = EmbeddingRequest(
                model = ModelId(Constants.OPENAI_EMBED_MODEL),
                input = listOf(contentToEmbed)
            )
            val embedding = openAI.embeddings(embeddingRequest)
            val vector = embedding.embeddings[0].embedding.toDoubleArray()

            // serialize and save to database for next time
            var vectorString = ""
            var needComma = false
            for (dbl in vector) {
                if (needComma)
                    vectorString += ","
                else
                    needComma = true

                vectorString += dbl
            }

            // Create a new map of values, where column names are the keys
            val values = ContentValues(2).apply {
                put(HistoryContract.EmbeddingEntry.COLUMN_NAME_MESSAGE, contentToEmbed)
                put(HistoryContract.EmbeddingEntry.COLUMN_NAME_VECTOR, vectorString)
            }

            // Gets the data repository in write mode
            val db = dbHelper.writableDatabase

            // Insert the new row, returning the primary key value of the row (would be -1 if error)
            val newRowId =
                db?.insert(HistoryContract.EmbeddingEntry.TABLE_NAME, null, values)
            // add to in-memory version
            historyCache[vector] = contentToEmbed
            Log.v("LLM-EH", "insert into database ($newRowId) $contentToEmbed vector: $vector")
        }

        suspend fun groundInHistory (openAI: OpenAI, dbHelper: HistoryDbHelper, message: String): String {
            var messagePreamble = ""
            var messageVector: DoubleArray? = null
            val embeddingRequest = EmbeddingRequest(
                model = ModelId(Constants.OPENAI_EMBED_MODEL),
                input = listOf(message)
            )
            try {
                val embedding = openAI.embeddings(embeddingRequest)
                val vector = embedding.embeddings[0].embedding.toDoubleArray()
                Log.i("LLM-EH", "historyVector: $vector")
                messageVector = vector
            } catch (e: Exception) {
                Log.i("LLM-EH", "e: $e")
            }


            var sortedVectors: SortedMap<Double, String> = sortedMapOf()

            // find the best match history items
            for (pastMessagePair in historyCache) {
                val v = messageVector!! dot pastMessagePair.key
                sortedVectors[v] = pastMessagePair.value
                // uncomment to examine vector comparison scores
                Log.v("LLM-EH", "dot $v comparing input to ${pastMessagePair.value}")
            }

            if (sortedVectors.size <= 0) return "" // nothing found

            if (sortedVectors.lastKey() > 0.8) { // arbitrary match threshold
                Log.i("LLM-EH", "Top match is ${sortedVectors.lastKey()}")
                messagePreamble =
                    "Following are some older interactions from this chat:\n\n"
                for (pastMessagePair in sortedVectors.tailMap(0.8)) {
                    Log.i("LLM-EH", "Add to preamble: ${pastMessagePair.key} -> ${pastMessagePair.value}")

                    messagePreamble += pastMessagePair.value + "\n\n"

                }
                messagePreamble += "\n\nUse the above information to answer the following question:\n\n"
                Log.v("LLM-EH", "$messagePreamble")
            } else {
                Log.i(
                    "LLM-EH",
                    "Top match was ${sortedVectors.lastKey()} which was below 0.8 and failed to meet criteria for grounding data"
                )
            }
            return messagePreamble
        }
    }
}