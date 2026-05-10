package es.udc.emergencyapp.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import es.udc.emergencyapp.util.DateUtils
import org.json.JSONObject

@Composable
fun AssignmentResourceCard(assignment: JSONObject) {
    val status = assignment.optString("status", "-")
    val teamInfo = assignment.optJSONObject("teamInfo")
    val vehicleInfo = assignment.optJSONObject("vehicleInfo")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "#${assignment.optLong("id", 0)}", style = MaterialTheme.typography.subtitle2)
                Spacer(modifier = Modifier.weight(1f))
                StatusChip(status = status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            teamInfo?.let { team ->
                ResourceLine(
                    icon = Icons.Default.Business,
                    primary = team.optString("code", ""),
                    secondary = team.optJSONObject("organization")?.let { org ->
                        listOfNotNull(org.optString("code", null), org.optString("name", null)).joinToString(" - ").ifBlank { null }
                    }
                )
            }

            vehicleInfo?.let { vehicle ->
                ResourceLine(
                    icon = Icons.Default.DirectionsCar,
                    primary = vehicle.optString("vehiclePlate", ""),
                    secondary = vehicle.optString("type", null).takeIf { !it.isNullOrBlank() }
                )
            }

            val notes = assignment.optString("notes")
            if (notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = notes, style = MaterialTheme.typography.body2, color = Color.DarkGray)
            }

            val assignedAt = assignment.optString("assignedAt")
            val acceptedAt = assignment.optString("acceptedAt")
            val completedAt = assignment.optString("completedAt")
            if (assignedAt.isNotBlank()) Text(text = "Assigned: ${DateUtils.formatForDisplay(assignedAt)}", style = MaterialTheme.typography.caption, color = Color.Gray)
            if (acceptedAt.isNotBlank()) Text(text = "Accepted: ${DateUtils.formatForDisplay(acceptedAt)}", style = MaterialTheme.typography.caption, color = Color.Gray)
            if (completedAt.isNotBlank()) Text(text = "Completed: ${DateUtils.formatForDisplay(completedAt)}", style = MaterialTheme.typography.caption, color = Color.Gray)
        }
    }
}

@Composable
private fun ResourceLine(
    icon: ImageVector,
    primary: String,
    secondary: String? = null,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = primary.ifBlank { "-" }, style = MaterialTheme.typography.body1)
            if (!secondary.isNullOrBlank()) {
                Text(text = secondary, style = MaterialTheme.typography.caption, color = Color.Gray)
            }
        }
    }
}
