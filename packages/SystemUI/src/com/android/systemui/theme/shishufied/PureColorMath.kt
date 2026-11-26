package com.android.systemui.theme.shishufied

import com.google.ux.material.libmonet.hct.Hct

fun argb(hexColor: String): Int {
    val normalized = if (hexColor.startsWith("#")) hexColor else "#$hexColor"
    return android.graphics.Color.parseColor(normalized)
}

fun hex(argbColor: Int): String =
    String.format("#%06X", 0xFFFFFF and argbColor).lowercase()

fun tone(hexColor: String): Double =
    Hct.fromInt(argb(hexColor)).tone

fun hctWith(hexColor: String, hueDelta: Double, chromaFactor: Double, tone: Double): String {
    val hct = Hct.fromInt(argb(hexColor))
    val h = ((hct.hue + hueDelta) % 360 + 360) % 360
    return hex(Hct.from(h, hct.chroma * chromaFactor, tone).toInt())
}

/** Ajusta el tone manteniendo H y C; el offset viene de recursos. */
fun adaptTone(hexColor: String, targetTone: Double): String {
    val hct = Hct.fromInt(argb(hexColor))
    return hex(Hct.from(hct.hue, hct.chroma, targetTone).toInt())
}

/** Terciario derivado: H+delta, C×factor, T50. */
fun derivePureColorTertiary(
    accentColor: String,
    hueDelta: Double,
    chromaFactor: Double,
): String = hctWith(accentColor, hueDelta, chromaFactor, 50.0)

/** shade = 1000 - 10 × tone. */
fun naturalShade(hexColor: String): Int =
    (1000 - 10 * tone(hexColor)).toInt()
