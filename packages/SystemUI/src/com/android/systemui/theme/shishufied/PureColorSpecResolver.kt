package com.android.systemui.theme.shishufied

import android.content.res.Resources
import com.android.internal.R
import com.android.systemui.monet.PureColorPaletteSpec

enum class PureColorPurity(val value: String) {
    LIGHT("LIGHT"), DARK("DARK"), BOTH("BOTH"), INHERIT("INHERIT");

    fun isPureIn(pureColorIsNightMode: Boolean): Boolean = when (this) {
        LIGHT -> !pureColorIsNightMode
        DARK -> pureColorIsNightMode
        BOTH, INHERIT -> true
    }

    companion object {
        fun parse(value: String?): PureColorPurity =
            entries.find { it.value == value } ?: INHERIT
    }
}

class PureColorSpecResolver(private val pureColorSettingsReader: PureColorSettingsReader) {

    fun resolve(
        pureColorResources: Resources,
        wallpaperSeedColor: Int,
    ): PureColorPaletteSpec {
        val preset = pureColorSettingsReader.preset()
        val pureColorAccentSource = pureColorSettingsReader.accentSource()

        val pureColorSeedLight = when (pureColorAccentSource) {
            PureColorAccentSource.USER_COLOR ->
                pureColorSettingsReader.userColor()
                    ?: hex(pureColorResources.getColor(
                            com.android.internal.R.color.pure_color_accent, null))
            PureColorAccentSource.PURE_COLOR_BUNDLE ->
                preset?.pureColorAccent
                    ?: hex(pureColorResources.getColor(
                            com.android.internal.R.color.pure_color_accent, null))
            PureColorAccentSource.WALLPAPER -> hex(wallpaperSeedColor)
        }
        val pureColorPurity = PureColorPurity.parse(
            pureColorSettingsReader.userPurity() ?: preset?.pureColorPurity)

        val pureColorTertiaryDerived = pureColorSettingsReader.userTertiaryColor()
            ?: preset?.pureColorTertiary
            ?: derivePureColorTertiary(
                pureColorSeedLight,
                pureColorResources.getInteger(
                    com.android.internal.R.integer.pure_color_tertiary_hue_delta).toDouble(),
                0.65)

        val pureColorAccentAnchorTone = tone(pureColorSeedLight)
        val pureColorTertiaryAnchorTone = tone(pureColorTertiaryDerived)

        return PureColorPaletteSpec(
            argb(pureColorSeedLight),
            argb(pureColorSeedLight),
            argb(pureColorTertiaryDerived),
            wallpaperSeedColor,
            pureColorAccentAnchorTone,
            pureColorTertiaryAnchorTone,
            pureColorResources.getInteger(
                com.android.internal.R.integer.pure_color_tertiary_hue_delta).toDouble(),
            0.65,
            0.0, // boalsNeutralChroma: gris real en Fase 1
            0, // boalsNeutralAnchorArgb: sin pin de background
            0.0, // boalsNeutralAnchorTone
        )
    }
}
