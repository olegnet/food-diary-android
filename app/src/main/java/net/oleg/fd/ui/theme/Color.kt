package net.oleg.fd.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


@Composable
fun invertedButtonColors(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = colorScheme.inverseOnSurface,
        contentColor = colorScheme.primary
    )

@Composable
fun deleteButtonColors(): ButtonColors =
    ButtonDefaults.buttonColors(
        containerColor = colorScheme.errorContainer,
        contentColor = colorScheme.error
    )

@Composable
fun requestBarcodeSpanStyle(): SpanStyle =
    SpanStyle(
        color = colorScheme.secondary,
        background = colorScheme.secondaryContainer
    )
