package com.dmdev.fossilvaultanda.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.dmdev.fossilvaultanda.data.models.enums.Period

@Composable
fun getPeriodColor(period: Period): Color {
    val isDark = isSystemInDarkTheme()
    
    return if (isDark) {
        when (period) {
            Period.PRECAMBRIAN -> PeriodPrecambrianDark
            Period.CAMBRIAN -> PeriodCambrianDark
            Period.ORDOVICIAN -> PeriodOrdovicianDark
            Period.SILURIAN -> PeriodSilurianDark
            Period.DEVONIAN -> PeriodDevonianDark
            Period.CARBONIFEROUS -> PeriodCarboniferousDark
            Period.MISSISSIPPIAN -> PeriodCarboniferousDark
            Period.PENNSYLVANIAN -> PeriodCarboniferousDark
            Period.PERMIAN -> PeriodPermianDark
            Period.TRIASSIC -> PeriodTriassicDark
            Period.JURASSIC -> PeriodJurassicDark
            Period.CRETACEOUS -> PeriodCretaceousDark
            Period.PALEOCENE -> PeriodPaleogeneDark
            Period.NEOGENE -> PeriodNeogeneDark
            Period.QUATERNARY -> PeriodQuaternaryDark
            Period.UNKNOWN -> PeriodUnknownDark
        }
    } else {
        when (period) {
            Period.PRECAMBRIAN -> PeriodPrecambrianLight
            Period.CAMBRIAN -> PeriodCambrianLight
            Period.ORDOVICIAN -> PeriodOrdovicianLight
            Period.SILURIAN -> PeriodSilurianLight
            Period.DEVONIAN -> PeriodDevonianLight
            Period.CARBONIFEROUS -> PeriodCarboniferousLight
            Period.MISSISSIPPIAN -> PeriodCarboniferousLight
            Period.PENNSYLVANIAN -> PeriodCarboniferousLight
            Period.PERMIAN -> PeriodPermianLight
            Period.TRIASSIC -> PeriodTriassicLight
            Period.JURASSIC -> PeriodJurassicLight
            Period.CRETACEOUS -> PeriodCretaceousLight
            Period.PALEOCENE -> PeriodPaleogeneLight
            Period.NEOGENE -> PeriodNeogeneLight
            Period.QUATERNARY -> PeriodQuaternaryLight
            Period.UNKNOWN -> PeriodUnknownLight
        }
    }
}