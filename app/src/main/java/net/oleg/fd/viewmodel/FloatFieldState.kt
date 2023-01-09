/*
 * Copyright 2022-2023 Oleg Okhotnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.oleg.fd.viewmodel

import android.icu.text.DecimalFormat
import android.icu.text.NumberFormat
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.ParseException

/*
 * Rules for DecimalFormat
 *  minimumFractionDigits must be 2 for presentation and 0 for editing
 *  maximumFractionDigits is always 2
 */
@Parcelize
data class FloatFieldState(
    val value: String = "",
    val isError: Boolean = isFloatFieldError(value),
    val valueIsZero: Boolean = isFloatFieldValueEqZero(value)   // FIXME ugly hack, refactor it later
) : Parcelable {

    constructor(value: Float?, minimumFractionDigits: Int = 0) : this(printToForm(value, minimumFractionDigits))

    // FIXME all calls to this function will throw ParseException if the system locale has just changed
    fun toFloat(): Float =
        parseFloat(value)

    companion object {
        private val SPACE = Regex("[\\p{Space}]")

        private fun isFloatFieldError(src: String): Boolean {
            val value = src.trim()

            // Don't scare user with empty field with error
            if (value.isEmpty()) {
                return false
            }

            // Spaces between digits etc.
            if (value.contains(SPACE)) {
                return true
            }

            return try {
                // FIXME NumberFormat.parse() only uses the beginning of the string and silently discards the end
                //  even in strict mode
                parseFloat(value) <= 0
            } catch (ex: ParseException) {
                true
            }
        }

        private fun isFloatFieldValueEqZero(src: String): Boolean {
            return try {
                parseFloat(src.trim()) == 0f
            } catch (ex: ParseException) {
                false
            }
        }

        private fun parseFloat(value: String): Float {
            return NumberFormat.getInstance().apply { isParseStrict = true }
                .parse(value)
                .toFloat()
        }

        private fun printToForm(src: Float?, _minimumFractionDigits: Int): String {
            val value = src ?: return ""
            return DecimalFormat().apply {
                minimumFractionDigits = _minimumFractionDigits
                maximumFractionDigits = 2
            }.format(value)
        }
    }
}