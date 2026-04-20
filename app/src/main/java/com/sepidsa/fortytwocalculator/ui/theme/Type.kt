package com.sepidsa.fortytwocalculator.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sepidsa.fortytwocalculator.R

val Inter = FontFamily(
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
)

val DmMono = FontFamily(
    Font(R.font.dm_mono_light, FontWeight.Light),
    Font(R.font.dm_mono_regular, FontWeight.Normal),
    Font(R.font.dm_mono_medium, FontWeight.Medium),
)

val AppTypography = Typography(
    headlineSmall = TextStyle(
        fontFamily = DmMono,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = DmMono,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
)
