package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.NumberFormat
import java.util.Locale

@Serializable
enum class Currency(
    val currencyCode: String,
    val symbol: String,
    val displayName: String
) {
    @SerialName("USD") USD("USD", "$", "US Dollar"),
    @SerialName("EUR") EUR("EUR", "€", "Euro"),
    @SerialName("GBP") GBP("GBP", "£", "British Pound"),
    @SerialName("JPY") JPY("JPY", "¥", "Japanese Yen"),
    @SerialName("CAD") CAD("CAD", "$", "Canadian Dollar"),
    @SerialName("AUD") AUD("AUD", "$", "Australian Dollar"),
    @SerialName("CHF") CHF("CHF", "Fr", "Swiss Franc"),
    @SerialName("CNY") CNY("CNY", "¥", "Chinese Yuan"),
    @SerialName("SEK") SEK("SEK", "kr", "Swedish Krona"),
    @SerialName("NOK") NOK("NOK", "kr", "Norwegian Krone"),
    @SerialName("DKK") DKK("DKK", "kr", "Danish Krone"),
    @SerialName("PLN") PLN("PLN", "zł", "Polish Zloty"),
    @SerialName("CZK") CZK("CZK", "Kč", "Czech Koruna"),
    @SerialName("HUF") HUF("HUF", "Ft", "Hungarian Forint"),
    @SerialName("RUB") RUB("RUB", "₽", "Russian Ruble"),
    @SerialName("BRL") BRL("BRL", "R$", "Brazilian Real"),
    @SerialName("INR") INR("INR", "₹", "Indian Rupee"),
    @SerialName("KRW") KRW("KRW", "₩", "South Korean Won"),
    @SerialName("MXN") MXN("MXN", "$", "Mexican Peso"),
    @SerialName("SGD") SGD("SGD", "$", "Singapore Dollar"),
    @SerialName("HKD") HKD("HKD", "$", "Hong Kong Dollar"),
    @SerialName("NZD") NZD("NZD", "$", "New Zealand Dollar"),
    @SerialName("ZAR") ZAR("ZAR", "R", "South African Rand"),
    @SerialName("TRY") TRY("TRY", "₺", "Turkish Lira"),
    @SerialName("ILS") ILS("ILS", "₪", "Israeli Shekel"),
    @SerialName("AED") AED("AED", "د.إ", "UAE Dirham"),
    @SerialName("THB") THB("THB", "฿", "Thai Baht"),
    @SerialName("MYR") MYR("MYR", "RM", "Malaysian Ringgit");
    
    /**
     * Gets the serialized name for Firebase storage (currency code)
     */
    val serializedName: String
        get() = currencyCode
    
    companion object {
        fun getDeviceDefault(): Currency {
            val locale = Locale.getDefault()
            val currencyCode = try {
                java.util.Currency.getInstance(locale).currencyCode
            } catch (e: Exception) {
                "USD"
            }
            
            return values().find { it.currencyCode == currencyCode } ?: USD
        }
        
        /**
         * Parse from serialized name (case-insensitive for backwards compatibility)
         */
        fun fromSerializedName(name: String?): Currency {
            if (name == null) return getDeviceDefault()
            return values().find { 
                it.currencyCode.equals(name, ignoreCase = true) ||
                it.name.equals(name, ignoreCase = true)
            } ?: getDeviceDefault()
        }
        
        fun formatAmount(amount: Double, currency: Currency): String {
            val formatter = NumberFormat.getCurrencyInstance()
            formatter.currency = java.util.Currency.getInstance(currency.currencyCode)
            return formatter.format(amount)
        }
    }
}

data class MultiCurrencyValue(
    val totals: Map<Currency, Double>,
    val primaryCurrency: Currency,
    val specimenCount: Int
) {
    val primaryTotal: Double get() = totals[primaryCurrency] ?: 0.0
    val hasMultipleCurrencies: Boolean get() = totals.size > 1
    val currencyCount: Int get() = totals.size
    val isEmpty: Boolean get() = totals.isEmpty() || totals.values.all { it == 0.0 }
    
    val sortedCurrencies: List<Pair<Currency, Double>> 
        get() = totals.toList().sortedByDescending { it.second }
}