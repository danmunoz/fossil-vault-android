package com.dmdev.fossilvaultanda.data.models

import kotlinx.serialization.Serializable

@Serializable
data class FaqItem(
    val question: String,
    val answer: String
)

@Serializable
data class FaqData(
    val en: List<FaqItem> = emptyList(),
    val es: List<FaqItem> = emptyList(),
    val `pt-BR`: List<FaqItem> = emptyList()
) {
    fun getFaqForLocale(language: String): List<FaqItem> {
        return when (language.lowercase()) {
            "es", "es-es", "es-mx" -> es
            "pt", "pt-br", "pt-pt" -> `pt-BR`
            else -> en // Default to English
        }
    }
}