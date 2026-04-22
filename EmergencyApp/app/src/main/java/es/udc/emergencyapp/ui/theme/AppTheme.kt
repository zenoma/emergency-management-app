package es.udc.emergencyapp

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource

/**
 * Small shared AppTheme composable that maps the XML colors into Compose MaterialTheme.
 */
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
