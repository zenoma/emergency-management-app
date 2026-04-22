package es.udc.emergencyapp.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

/**
 * Helper to set Compose content wrapped with ScreenContainer so system bars are respected.
 */
fun ComponentActivity.setContentWithSystemBars(content: @Composable () -> Unit) {
    setContent {
        ScreenContainer {
            content()
        }
    }
}

/**
 * For ComposeView (e.g. when embedding Compose inside fragments), provide the same helper.
 */
fun ComposeView.setContentWithSystemBars(content: @Composable () -> Unit) {
    // setContent is an extension provided by compose; wrap content inside ScreenContainer
    this.setContent {
        ScreenContainer {
            content()
        }
    }
}
