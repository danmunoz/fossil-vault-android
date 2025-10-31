package com.dmdev.fossilvaultanda.data.models

import kotlinx.serialization.Serializable

/**
 * Represents the storage location of a fossil specimen
 * Matches iOS StorageMethod struct
 */
@Serializable
data class StorageMethod(
    val drawer: String? = null,
    val cabinet: String? = null,
    val room: String? = null
) {
    /**
     * Returns true if all storage fields are empty
     */
    val isEmpty: Boolean
        get() = (drawer.isNullOrEmpty() && cabinet.isNullOrEmpty() && room.isNullOrEmpty())

    /**
     * Converts the storage method to a map for Firestore storage
     */
    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "drawer" to drawer,
            "cabinet" to cabinet,
            "room" to room
        )
    }

    companion object {
        /**
         * Creates StorageMethod from Firestore map data
         */
        fun fromFirestoreMap(map: Map<String, Any?>?): StorageMethod? {
            if (map == null) return null

            val drawer = map["drawer"] as? String
            val cabinet = map["cabinet"] as? String
            val room = map["room"] as? String

            // Return null if all fields are empty
            if (drawer.isNullOrEmpty() && cabinet.isNullOrEmpty() && room.isNullOrEmpty()) {
                return null
            }

            return StorageMethod(
                drawer = drawer,
                cabinet = cabinet,
                room = room
            )
        }
    }
}
