package es.udc.emergencyapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val colors = if (dark) {
        darkColors(
            primary = colorResource(id = R.color.primary_main),
            primaryVariant = colorResource(id = R.color.primary_variant),
            secondary = colorResource(id = R.color.secondary_main),
            background = colorResource(id = R.color.background_dark),
            surface = colorResource(id = R.color.surface_dark_paper),
            onPrimary = colorResource(id = R.color.on_primary),
            onSecondary = colorResource(id = R.color.on_secondary),
            onBackground = colorResource(id = R.color.on_background_dark)
        )
    } else {
        lightColors(
            primary = colorResource(id = R.color.primary_main),
            primaryVariant = colorResource(id = R.color.primary_variant),
            secondary = colorResource(id = R.color.secondary_main),
            background = colorResource(id = R.color.background_light),
            surface = colorResource(id = R.color.surface_light),
            onPrimary = colorResource(id = R.color.on_primary),
            onSecondary = colorResource(id = R.color.on_secondary),
            onBackground = colorResource(id = R.color.on_background_light)
        )
    }

    MaterialTheme(colors = colors) {
        content()
    }
}

fun statusColor(status: String?): Color {
    return when (status?.uppercase()) {
        "ACCEPTED" -> Color(0xFF2E7D32) // #2e7d32
        "REJECTED" -> Color(0xFFF44336) // #f44336
        "COMPLETED" -> Color(0xFF607D8B) // #607d8b
        "PENDING" -> Color(0xFFFF9800) // #ff9800
        "BUSY" -> Color(0xFFD32F2F) // #d32f2f
        "AVAILABLE" -> Color(0xFF4CAF50) // #4caf50
        else -> Color(0xFF9E9E9E) // #9e9e9e
    }
}

/**
 * Centralized mapping for emergency index colors.
 * Keep colors here (single point of truth) instead of scattering hex literals in UI files.
 */
fun indexColor(index: String?): Color {
    return when (index?.uppercase()) {
        "RESUELTO" -> Color(0xFF9E9E9E)
        "CERO" -> Color(0xFF4CAF50)
        "UNO" -> Color(0xFFFFC107)
        "DOS" -> Color(0xFFFF9800)
        "TRES" -> Color(0xFFF44336)
        else -> Color(0xFF2196F3)
    }
}
