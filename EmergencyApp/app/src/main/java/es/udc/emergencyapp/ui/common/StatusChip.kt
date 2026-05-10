package es.udc.emergencyapp.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import es.udc.emergencyapp.R
import es.udc.emergencyapp.statusColor

@Composable
fun StatusChip(status: String?, modifier: Modifier = Modifier) {
    val label = when (status?.uppercase()) {
        "ACCEPTED" -> stringResource(R.string.status_accepted_label)
        "REJECTED" -> stringResource(R.string.status_rejected_label)
        "COMPLETED" -> stringResource(R.string.status_completed_label)
        "PENDING" -> stringResource(R.string.status_pending_label)
        "BUSY" -> stringResource(R.string.status_busy_label)
        "AVAILABLE" -> stringResource(R.string.status_available_label)
        null, "" -> stringResource(R.string.status_new_label)
        else -> status
    }
    val bg = statusColor(status)
    val onColor =
        if ((bg.red * 0.2126 + bg.green * 0.7152 + bg.blue * 0.0722) < 0.6f) Color.White else Color.Black

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = bg,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = label, color = onColor)
            Spacer(modifier = Modifier.size(2.dp))
        }
    }
}
