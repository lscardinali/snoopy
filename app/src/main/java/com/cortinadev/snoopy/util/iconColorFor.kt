package com.cortinadev.snoopy.util

import androidx.compose.ui.graphics.Color
import com.cortinadev.snoopy.R
import com.cortinadev.snoopy.ui.theme.LightText
import com.cortinadev.snoopy.ui.theme.LightningBlue
import com.cortinadev.snoopy.ui.theme.NeonGreen
import com.cortinadev.snoopy.ui.theme.NeonRed
import com.cortinadev.snoopy.ui.theme.WarmOrange

fun iconColorFor(icon: Int): Color = when(icon) {
    R.drawable.ic_arrow_down -> LightningBlue
    R.drawable.ic_check -> NeonGreen
    R.drawable.ic_error -> NeonRed
    R.drawable.ic_stop -> WarmOrange
    R.drawable.ic_arrow_up -> WarmOrange
    R.drawable.ic_trash -> NeonRed
    R.drawable.ic_folder -> LightningBlue
    else -> LightText
}
