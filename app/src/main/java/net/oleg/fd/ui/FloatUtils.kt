package net.oleg.fd.ui

import net.oleg.fd.viewmodel.FloatFieldState

fun Float?.printToForm(): String =
    FloatFieldState(value = this, minimumFractionDigits = 2).value
