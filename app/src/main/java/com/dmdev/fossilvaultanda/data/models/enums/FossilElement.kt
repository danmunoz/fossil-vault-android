package com.dmdev.fossilvaultanda.data.models.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class FossilElement(val displayString: String) {
    @SerialName("tooth") TOOTH("Tooth"),
    @SerialName("jaw") JAW("Jaw"),
    @SerialName("skull") SKULL("Skull"),
    @SerialName("bone") BONE("Bone"),
    @SerialName("claw") CLAW("Claw"),
    @SerialName("horn") HORN("Horn"),
    @SerialName("rib") RIB("Rib"),
    @SerialName("vertebra") VERTEBRA("Vertebra"),
    @SerialName("shell") SHELL("Shell"),
    @SerialName("ammonite") AMMONITE("Ammonite"),
    @SerialName("matrix") MATRIX("Matrix"),
    @SerialName("coprolite") COPROLITE("Coprolite"),
    @SerialName("imprint") IMPRINT("Imprint"),
    @SerialName("track") TRACK("Track"),
    @SerialName("egg") EGG("Egg"),
    @SerialName("other") OTHER("Other");
}