package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class SizeUnit(val symbol: String, val displayName: String) {
    @SerialName("mm") MM("mm", "Millimeters"),
    @SerialName("cm") CM("cm", "Centimeters"),
    @SerialName("inch") INCH("in", "Inches");
    
    companion object {
        val default = MM
    }
}