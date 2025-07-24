package com.dmdev.fossilvaultanda.data.models

import com.dmdev.fossilvaultanda.data.models.enums.Currency
import com.dmdev.fossilvaultanda.data.models.enums.SizeUnit
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val unit: SizeUnit = SizeUnit.MM,
    val divideCarboniferous: Boolean = false,
    val defaultCurrency: Currency = Currency.getDeviceDefault()
)