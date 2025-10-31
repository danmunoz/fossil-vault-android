package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.google.firebase.Timestamp
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * Ownership status for a specimen that has been disposed of
 * Matches iOS OwnershipStatus enum
 */
enum class OwnershipStatus(val serializedName: String, val displayString: String) {
    SOLD("sold", "Sold"),
    TRADED("traded", "Traded"),
    GIFTED("gifted", "Gifted"),
    LOST("lost", "Lost");

    companion object {
        fun fromSerializedName(name: String?): OwnershipStatus? {
            return when (name?.lowercase()) {
                "sold" -> SOLD
                "traded" -> TRADED
                "gifted" -> GIFTED
                "lost" -> LOST
                else -> null
            }
        }
    }
}

/**
 * Represents a price with amount and currency
 * Matches iOS Price struct
 */
@Serializable
data class Price(
    val amount: Double,
    val currency: Currency
) {
    fun toFirestoreMap(): Map<String, Any> {
        return mapOf(
            "amount" to amount,
            "currency" to currency.serializedName
        )
    }

    companion object {
        fun fromFirestoreMap(map: Map<String, Any?>?): Price? {
            if (map == null) return null

            val amount = (map["amount"] as? Number)?.toDouble() ?: return null
            val currencyStr = map["currency"] as? String
            val currency = Currency.fromSerializedName(currencyStr) ?: return null

            return Price(amount = amount, currency = currency)
        }
    }
}

/**
 * Represents the disposition of a specimen (sold, traded, gifted, or lost)
 * Matches iOS Disposition struct
 */
@Serializable
data class Disposition(
    val status: OwnershipStatus,
    val date: Instant? = null,
    val recipient: String? = null,
    val price: Price? = null,
    val notes: String? = null,
    val imageURLs: List<String> = emptyList()
) {
    /**
     * Converts the disposition to a map for Firestore storage
     */
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "status" to status.serializedName,
            "date" to date?.let { Timestamp(it.epochSeconds, it.nanosecondsOfSecond) },
            "recipient" to recipient,
            "price" to price?.toFirestoreMap(),
            "notes" to notes,
            "imageURLs" to imageURLs
        )
    }

    companion object {
        /**
         * Creates Disposition from Firestore map data
         */
        fun fromFirestoreMap(map: Map<String, Any?>?): Disposition? {
            if (map == null) return null

            val statusStr = map["status"] as? String
            val status = OwnershipStatus.fromSerializedName(statusStr) ?: return null

            val dateTimestamp = map["date"] as? Timestamp
            val date = dateTimestamp?.let {
                Instant.fromEpochSeconds(it.seconds, it.nanoseconds)
            }

            val recipient = map["recipient"] as? String

            @Suppress("UNCHECKED_CAST")
            val priceMap = map["price"] as? Map<String, Any?>
            val price = Price.fromFirestoreMap(priceMap)

            val notes = map["notes"] as? String

            @Suppress("UNCHECKED_CAST")
            val imageURLs = (map["imageURLs"] as? List<String>) ?: emptyList()

            return Disposition(
                status = status,
                date = date,
                recipient = recipient,
                price = price,
                notes = notes,
                imageURLs = imageURLs
            )
        }
    }
}
