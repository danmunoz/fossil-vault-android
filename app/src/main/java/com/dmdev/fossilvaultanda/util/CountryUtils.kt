package com.dmdev.fossilvaultanda.util

import java.util.Locale

/**
 * Utility object for country-related operations including flag emojis and localized names.
 *
 * Provides functions to:
 * - Get flag emoji for ISO country codes
 * - Get localized country names
 * - Get list of all ISO 3166-1 alpha-2 country codes
 */
object CountryUtils {

    /**
     * Frequently used countries for fossil collection (ISO 3166-1 alpha-2 codes)
     * Matches iOS implementation: Germany, US, UK, Morocco, Niger, China, France, Madagascar
     */
    val PINNED_COUNTRIES = listOf("DE", "US", "GB", "MA", "NE", "CN", "FR", "MG")

    /**
     * Returns the flag emoji for a given ISO 3166-1 alpha-2 country code.
     *
     * Converts country code to regional indicator symbols (Unicode range U+1F1E6 to U+1F1FF).
     * For example: "US" -> "🇺🇸", "DE" -> "🇩🇪"
     *
     * @param countryCode Two-letter ISO country code (e.g., "US", "DE", "MA")
     * @return Flag emoji string
     */
    fun getFlagEmoji(countryCode: String): String {
        if (countryCode.length != 2) return ""

        val base = 0x1F1E6 // Regional indicator symbol A
        val flag = StringBuilder()

        countryCode.uppercase().forEach { char ->
            if (char in 'A'..'Z') {
                val offset = char - 'A'
                flag.append(Character.toChars(base + offset))
            }
        }

        return flag.toString()
    }

    /**
     * Returns the localized country name for a given ISO country code.
     * Uses the current device locale for localization.
     *
     * @param countryCode Two-letter ISO country code
     * @return Localized country name, or the country code if name not found
     */
    fun getLocalizedCountryName(countryCode: String): String {
        return Locale.Builder().setRegion(countryCode).build().displayCountry.ifBlank { countryCode }
    }

    /**
     * Returns all ISO 3166-1 alpha-2 country codes sorted alphabetically by localized name.
     * Excludes pinned countries from the list.
     *
     * @return List of country codes sorted by localized name
     */
    fun getAllCountryCodes(): List<String> {
        return Locale.getISOCountries()
            .filter { it.length == 2 } // Only ISO 3166-1 alpha-2 codes
            .filter { it !in PINNED_COUNTRIES } // Exclude pinned countries
            .sortedBy { getLocalizedCountryName(it) }
    }

    /**
     * Returns pinned countries sorted alphabetically by localized name.
     *
     * @return List of pinned country codes sorted by localized name
     */
    fun getPinnedCountryCodes(): List<String> {
        return PINNED_COUNTRIES.sortedBy { getLocalizedCountryName(it) }
    }

    /**
     * Filters country codes based on search query.
     * Searches within the localized country name.
     *
     * @param countryCodes List of country codes to filter
     * @param query Search query
     * @return Filtered list of country codes
     */
    fun filterCountries(countryCodes: List<String>, query: String): List<String> {
        if (query.isBlank()) return countryCodes

        return countryCodes.filter { countryCode ->
            getLocalizedCountryName(countryCode).contains(query, ignoreCase = true)
        }
    }
}
