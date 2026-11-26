package com.cortinadev.snoopy.util

import androidx.compose.ui.graphics.Color
import com.cortinadev.snoopy.ui.theme.LightText
import com.cortinadev.snoopy.ui.theme.LightningBlue
import com.cortinadev.snoopy.ui.theme.NeonGreen

fun consoleColorFor(console: String): Color = when(console) {
    "Xbox 360" -> NeonGreen
    "Nintendo 64" -> Color.Gray
    "Sega Genesis" -> LightningBlue
    else -> LightText
}
