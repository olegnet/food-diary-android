package net.oleg.fd.ui

import android.icu.number.NumberFormatter
import android.icu.number.Precision
import android.icu.text.DecimalFormat
import android.icu.util.ULocale
import android.os.Build
import kotlin.math.roundToInt

fun Float?.printToForm(): String {
    val value = this ?: return ""
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            NumberFormatter.withLocale(ULocale.getDefault())
                .precision(Precision.minMaxFraction(2, 2))
                .format(value)
                .toString()
        } else {
            DecimalFormat().apply {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
            }.format(value)
        }
    } catch (ex: NumberFormatException) {
        ""
    }
}

fun Float.printToEditForm(): String =
    if (this > this.roundToInt()) {
        this.toString()
    } else {
        this.toInt().toString()
    }
