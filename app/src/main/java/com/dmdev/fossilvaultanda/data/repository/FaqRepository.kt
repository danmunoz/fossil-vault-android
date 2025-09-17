package com.dmdev.fossilvaultanda.data.repository

import android.content.Context
import com.dmdev.fossilvaultanda.data.models.FaqData
import com.dmdev.fossilvaultanda.data.models.FaqItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaqRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var cachedFaqData: FaqData? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun getFaqItems(): List<FaqItem> = withContext(Dispatchers.IO) {
        try {
            val faqData = loadFaqData()
            val currentLanguage = Locale.getDefault().language
            val items = faqData.getFaqForLocale(currentLanguage)

            // If no items loaded from JSON, return hardcoded fallback
            if (items.isEmpty()) {
                return@withContext listOf(
                    FaqItem(
                        question = "Can I use the app without creating an account?",
                        answer = "Yes! You can use FossilVault without signing up. Your data is stored locally on your device."
                    ),
                    FaqItem(
                        question = "What happens if I delete the app without backing up?",
                        answer = "If you're using a local account and delete the app, all your fossil data will be permanently lost."
                    ),
                    FaqItem(
                        question = "How do I back up my fossil collection?",
                        answer = "You can back up your collection by exporting as CSV or ZIP files, or by creating an account for cloud sync."
                    )
                )
            }

            return@withContext items
        } catch (e: Exception) {
            // Return fallback data if there's any error
            return@withContext listOf(
                FaqItem(
                    question = "Error Loading FAQ",
                    answer = "There was an error loading the FAQ data. Please try again later."
                )
            )
        }
    }

    private fun loadFaqData(): FaqData {
        if (cachedFaqData == null) {
            try {
                val inputStream = context.assets.open("fossilvault_qa_localized.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                cachedFaqData = json.decodeFromString<FaqData>(jsonString)
            } catch (e: Exception) {
                // If loading fails, return empty data which will trigger fallback
                cachedFaqData = FaqData()
            }
        }
        return cachedFaqData ?: FaqData()
    }
}