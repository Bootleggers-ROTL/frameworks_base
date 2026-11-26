package com.android.systemui.theme.shishufied

import org.json.JSONObject

class PureColorSettingsReader(private val readJson: () -> String?) {

    private fun pureColorKey(name: String): String? = try {
        JSONObject(readJson() ?: "{}").optString(
            "android.theme.customization.$name", null)
    } catch (e: Exception) {
        null
    }

    fun accentSource(): PureColorAccentSource = when (pureColorKey("pure_color_accent_source")) {
        "PURE_COLOR_BUNDLE" -> PureColorAccentSource.PURE_COLOR_BUNDLE
        "WALLPAPER" -> PureColorAccentSource.WALLPAPER
        else -> PureColorAccentSource.USER_COLOR
    }

    fun userColor(): String? = pureColorKey("shishufied_user_color")
    fun userPurity(): String? = pureColorKey("pure_color_pure")
    fun userTertiaryColor(): String? = pureColorKey("pure_color_tertiary_color")
    fun userTertiaryPurity(): String? = pureColorKey("pure_color_tertiary_pure")

    fun preset(): PureColorPreset? {
        val accent = pureColorKey("pure_color_preset_accent") ?: return null
        return PureColorPreset(
            pureColorAccent = accent,
            pureColorPurity = pureColorKey("pure_color_preset_pure"),
            pureColorTertiary = pureColorKey("pure_color_preset_tertiary"),
            pureColorTertiaryPurity = pureColorKey("pure_color_preset_tertiary_pure"),
        )
    }
}

data class PureColorPreset(
    val pureColorAccent: String,
    val pureColorPurity: String?,
    val pureColorTertiary: String?,
    val pureColorTertiaryPurity: String?,
)
