package org.hyperhawks.itantra.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily.Companion.Default
import androidx.compose.ui.text.font.FontFamily.Companion.Monospace
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.sp

val Typography: Typography = Typography(
  displayLarge = TextStyle(
    fontFamily = Monospace,
    fontWeight = Bold,
    fontSize = 28.sp,
    lineHeight = 34.sp,
    color = TextPrimary
  ),
  titleLarge = TextStyle(
    fontFamily = Monospace,
    fontWeight = Bold,
    fontSize = 20.sp,
    lineHeight = 26.sp,
    color = TextPrimary
  ),
  titleMedium = TextStyle(
    fontFamily = Default,
    fontWeight = SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    color = TextPrimary
  ),
  bodyLarge = TextStyle(
    fontFamily = Default,
    fontWeight = Normal,
    fontSize = 15.sp,
    lineHeight = 22.sp,
    color = TextPrimary
  ),
  bodyMedium = TextStyle(
    fontFamily = Default,
    fontWeight = Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    color = TextSecondary
  ),
  labelSmall = TextStyle(
    fontFamily = Monospace,
    fontWeight = Medium,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    color = TextTertiary
  )
)
