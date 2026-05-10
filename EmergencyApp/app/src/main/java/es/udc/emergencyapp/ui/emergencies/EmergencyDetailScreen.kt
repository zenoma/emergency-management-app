package es.udc.emergencyapp.ui.emergencies

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.udc.emergencyapp.R
import es.udc.emergencyapp.data.dto.EmergencyDto
import es.udc.emergencyapp.indexColor
import es.udc.emergencyapp.net.HttpClient
import es.udc.emergencyapp.ui.common.CompactChip
import es.udc.emergencyapp.ui.common.CoordinateWithQuadrantChip
import es.udc.emergencyapp.ui.common.StatusChip
import es.udc.emergencyapp.util.DateUtils
import es.udc.emergencyapp.util.emergencyTypeIcon
import es.udc.emergencyapp.util.transformWgs84ToUtm29
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun EmergencyDetailScreen(emergency: EmergencyDto, onClose: (() -> Unit)? = null) {
    val ctx = LocalContext.current
    val pointAssignmentsState = remember { mutableStateOf(listOf<JSONObject>()) }
    val pointLoadingState = remember { mutableStateOf(false) }

    LaunchedEffect(emergency.id, emergency.location?.lon, emergency.location?.lat) {
        pointAssignmentsState.value = emptyList()
        val lon = emergency.location?.lon
        val lat = emergency.location?.lat
        if (lon == null || lat == null) return@LaunchedEffect

        pointLoadingState.value = true
        try {
            val (px, py) = if (kotlin.math.abs(lon) > 1000000 || kotlin.math.abs(lat) > 1000000) {
                Pair(lon, lat)
            } else {
                transformWgs84ToUtm29(lon, lat)
            }

            val quadrantBody = withContext(Dispatchers.IO) {
                HttpClient.getFromHosts("/quadrants/findByCoordinates?lat=$py&lon=$px", ctx).first
            }

            val quadrantId = quadrantBody?.let { JSONObject(it).optLong("id") }?.takeIf { it > 0 }
            if (quadrantId != null) {
                val assignmentsBody = withContext(Dispatchers.IO) {
                    HttpClient.getFromHosts("/assignments?emergencyId=${emergency.id}", ctx).first
                }
                if (!assignmentsBody.isNullOrBlank()) {
                    val arr = JSONArray(assignmentsBody)
                    val list = mutableListOf<JSONObject>()
                    for (i in 0 until arr.length()) {
                        try {
                            list.add(arr.getJSONObject(i))
                        } catch (_: Exception) {
                        }
                    }
                    pointAssignmentsState.value = list
                }
            }
        } catch (_: Exception) {
            pointAssignmentsState.value = emptyList()
        } finally {
            pointLoadingState.value = false
        }
    }

    LazyColumn(modifier = Modifier.padding(12.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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

                    if (!emergency.description.isNullOrBlank()) {
                        Text(
                            text = emergency.description
                                ?: stringResource(R.string.description_default),
                            style = MaterialTheme.typography.body1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val idx = emergency.emergencyIndex ?: "-"
                        CompactChip(label = idx, bgColor = indexColor(idx))
                        Spacer(modifier = Modifier.size(12.dp))
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(R.string.created_label)
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(text = DateUtils.formatForDisplay(emergency.createdAt))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val locLon = emergency.location?.lon
                    val locLat = emergency.location?.lat
                    if (locLon != null && locLat != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = stringResource(R.string.location_label)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            CoordinateWithQuadrantChip(
                                lon = locLon,
                                lat = locLat,
                                showCoordinates = true
                            )
                        }
                    } else {
                        val qCount = emergency.quadrantInfo?.size ?: 0
                        if (qCount > 0) {
                            CompactChip(
                                label = stringResource(
                                    R.string.quadrant_label_format,
                                    qCount
                                )
                            )
                        }
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
                        },
                    shape = RoundedCornerShape(10.dp),
                    elevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.size(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = listOfNotNull(
                                    q.nombre,
                                    if (q.id > 0) "#${q.id}" else null
                                ).joinToString(" "), style = MaterialTheme.typography.body1
                            )
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
                    text = stringResource(R.string.resources_in_quadrant),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (pointLoadingState.value) {
                item {
                    CircularProgressIndicator()
                }
            } else if (pointAssignmentsState.value.isEmpty()) {
                item {
                    Text(text = stringResource(R.string.no_assignments))
                }
            } else {
                items(pointAssignmentsState.value) { assignment ->
                    AssignmentRow(assignment)
                }
            }
        }
    }
}

@Composable
private fun AssignmentRow(assignment: JSONObject) {
    val status = assignment.optString("status", "-")
    val teamInfo = assignment.optJSONObject("teamInfo")
    val vehicleInfo = assignment.optJSONObject("vehicleInfo")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${assignment.optLong("id", 0)}",
                    style = MaterialTheme.typography.subtitle2
                )
                Spacer(modifier = Modifier.weight(1f))
                StatusChip(status = status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            teamInfo?.let { team ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Business, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = team.optString("code", ""), style = MaterialTheme.typography.body1)
                }
            }

            vehicleInfo?.let { vehicle ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = vehicle.optString("vehiclePlate", ""),
                        style = MaterialTheme.typography.body1
                    )
                }
            }

            val notes = assignment.optString("notes")
            if (notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = notes, style = MaterialTheme.typography.body2)
            }

            val assignedAt = assignment.optString("assignedAt")
            val acceptedAt = assignment.optString("acceptedAt")
            val completedAt = assignment.optString("completedAt")
            if (assignedAt.isNotBlank()) Text(
                text = stringResource(
                    R.string.assigned_label,
                    DateUtils.formatForDisplay(assignedAt)
                ), style = MaterialTheme.typography.caption
            )
            if (acceptedAt.isNotBlank()) Text(
                text = stringResource(
                    R.string.accepted_label,
                    DateUtils.formatForDisplay(acceptedAt)
                ), style = MaterialTheme.typography.caption
            )
            if (completedAt.isNotBlank()) Text(
                text = stringResource(
                    R.string.completed_label,
                    DateUtils.formatForDisplay(completedAt)
                ), style = MaterialTheme.typography.caption
            )
        }
    }
}

// Deprecated local icon matcher. Use util.emergencyTypeIcon instead.
@Deprecated("Use es.udc.emergencyapp.util.emergencyTypeIcon")
private fun iconForEmergencyType(type: String?): ImageVector = emergencyTypeIcon(type)
