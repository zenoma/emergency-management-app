package es.udc.emergencyapp.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Small reusable chip for compact labels (e.g. 'Cuadrantes X').
 * bgColor optional: if not provided, uses primary with low alpha.
 */
@Composable
fun CompactChip(label: String, modifier: Modifier = Modifier, bgColor: androidx.compose.ui.graphics.Color? = null) {
    val background = bgColor ?: MaterialTheme.colors.primary.copy(alpha = 0.08f)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = background,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(text = label, style = MaterialTheme.typography.body2)
        }
    }
}
