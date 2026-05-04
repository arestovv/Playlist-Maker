package com.arestov.playlistmaker.ui.compose.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.arestov.playlistmaker.R

val YsDisplay = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.Normal),
    Font(R.font.ys_display_medium, FontWeight.Medium),
    Font(R.font.ys_display_bold, FontWeight.Bold),
)

val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = YsDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = YsDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = YsDisplay,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    ),
    labelLarge = TextStyle(
        fontFamily = YsDisplay,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
)
