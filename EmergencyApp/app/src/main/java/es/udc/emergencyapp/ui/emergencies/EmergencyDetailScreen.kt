package es.udc.emergencyapp.ui.emergencies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.udc.emergencyapp.R
import es.udc.emergencyapp.data.dto.EmergencyDto
import es.udc.emergencyapp.indexColor
import es.udc.emergencyapp.ui.common.CompactChip
import es.udc.emergencyapp.ui.common.CoordinateChip
import es.udc.emergencyapp.util.emergencyTypeIcon


@Composable
fun EmergencyDetailScreen(emergency: EmergencyDto, onClose: (() -> Unit)? = null) {
    val ctx = LocalContext.current

    LazyColumn(modifier = Modifier.padding(12.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Top row: icon for type + title
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val typeIcon = iconForEmergencyType(emergency.emergencyTypeName)
                        Icon(
                            imageVector = typeIcon,
                            contentDescription = emergency.emergencyTypeName
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        Column {
                            Text(
                                text = "#${emergency.id}",
                                style = MaterialTheme.typography.subtitle2
                            )
                            Text(
                                text = emergency.emergencyTypeName ?: "-",
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Description
                    if (!emergency.description.isNullOrBlank()) {
                        Text(
                            text = emergency.description
                                ?: stringResource(R.string.description_default),
                            style = MaterialTheme.typography.body1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Index and CreatedAt row
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val idx = emergency.emergencyIndex ?: "-"
                        CompactChip(label = idx, bgColor = indexColor(idx))
                        Spacer(modifier = Modifier.size(12.dp))
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.created_label)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(text = es.udc.emergencyapp.util.DateUtils.formatForDisplay(emergency.createdAt))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Coordinates or quadrant count
                    if (emergency.location != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = stringResource(R.string.location_label)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            CoordinateChip(
                                lonText = "${emergency.location.lon}",
                                latText = "${emergency.location.lat}",
                                quadrantName = null,
                                showCoordinates = true
                            )
                        }
                    } else {
                        val qCount = emergency.quadrantInfo?.size ?: 0
                        if (qCount > 0) CompactChip(
                            label = stringResource(
                                R.string.quadrant_label_format,
                                qCount
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (!emergency.quadrantInfo.isNullOrEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.quadrants_label),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
            }
            items(emergency.quadrantInfo) { q ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable {
                            try {
                                val intent = android.content.Intent(
                                    ctx,
                                    QuadrantResourcesActivity::class.java
                                )
                                intent.putExtra("emergencyId", emergency.id)
                                intent.putExtra("quadrantId", q.id)
                                val activity = ctx as? android.app.Activity
                                if (activity != null) activity.startActivity(intent) else {
                                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                                    ctx.startActivity(intent)
                                }
                            } catch (_: Exception) {
                            }
                        }, shape = RoundedCornerShape(10.dp), elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.size(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = q.nombre ?: "-", style = MaterialTheme.typography.body1)
                            Text(
                                text = stringResource(R.string.scale_label, q.escala ?: "-"),
                                style = MaterialTheme.typography.caption
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        if (emergency.location != null) {
            item {
                Text(
                    text = stringResource(R.string.available_teams_mock),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(listOf(1, 2, 3)) { t ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.team_item_label, t),
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Text(
                    text = stringResource(R.string.available_vehicles_mock),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            items(listOf(1, 2)) { v ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.vehicle_item_label, v),
                            style = MaterialTheme.typography.body1
                        )
                    }
                }
            }
        }
    }
}

// Deprecated local icon matcher. Use util.emergencyTypeIcon instead.
@Deprecated("Use es.udc.emergencyapp.util.emergencyTypeIcon")
private fun iconForEmergencyType(type: String?): ImageVector =
    emergencyTypeIcon(type)

// use DateUtils.formatForDisplay instead of local formatter
