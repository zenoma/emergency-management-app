package es.udc.emergencyapp.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CoordinateChip(lonText: String, latText: String, quadrantName: String?, showCoordinates: Boolean = true) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colors.primary.copy(alpha = 0.08f),
        modifier = Modifier.padding(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Icon(
                painter = painterResource(android.R.drawable.ic_menu_mylocation),
                contentDescription = null,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(6.dp))
            Column {
                if (showCoordinates) {
                    Text(text = "$lonText, $latText", style = MaterialTheme.typography.body2)
                    Text(text = quadrantName ?: "-", style = MaterialTheme.typography.caption)
                } else {
                    // only show quadrant name
                    Text(text = quadrantName ?: "-", style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}
