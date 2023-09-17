package com.example.compose.jetchat

/**
 * dot product for comparing vector similarity
 */
infix fun DoubleArray.dot(other: DoubleArray): Double {
    var out = 0.0
    for (i in indices) out += this[i] * other[i]
    return out
}