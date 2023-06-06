/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 *
 */

package com.microsoft.device.display.wm_samples.sourceeditor

internal object Constants {
    // The OpenAI API key.
    internal const val OPENAI_KEY = "{OPENAPI_API_KEY_GOES_HERE}"

    // The OpenAI API model name.
    internal const val OPENAI_MODEL_COMPLETIONS = "text-davinci-003"
    internal const val OPENAI_MODEL_EDITS = "text-davinci-edit-001"

    // The OpenAI API endpoint.
    internal const val API_ENDPOINT_COMPLETIONS = "https://api.openai.com/v1/completions"
    internal const val API_ENDPOINT_EDITS = "https://api.openai.com/v1/edits"

    internal const val SHORTEN = "Strip away extra words in the below html content and provide a clear message as html content.\n\n"
    internal const val GRAMMARCHECK = "Check grammar and spelling in the below html content and provide the result as html content.\n\n"
}