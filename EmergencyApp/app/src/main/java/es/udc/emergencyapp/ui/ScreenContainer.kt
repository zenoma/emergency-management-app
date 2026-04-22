package es.udc.emergencyapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Wrapper to ensure system bars (status + navigation) are respected on every screen.
 * Use as: ScreenContainer { YourComposable() }
 */
@Composable
fun ScreenContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxSize()
    ) {
        content()
    }
}
