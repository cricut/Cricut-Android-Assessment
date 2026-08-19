package com.cricut.androidassessment.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DroidGreen80,
    secondary = DroidGreenGrey80,
    tertiary = DroidTeal80,
)

private val LightColorScheme = lightColorScheme(
    primary = DroidGreen40,
    secondary = DroidGreenGrey40,
    tertiary = DroidTeal40,
)

@Composable
fun AndroidAssessmentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Off by default so the quiz keeps its Android-green identity; flip on to let
    // Material You recolor the app from the user's wallpaper (Android 12+).
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme

        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
