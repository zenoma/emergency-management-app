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
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import es.udc.emergencyapp.data.dto.EmergencyDto
import es.udc.emergencyapp.ui.common.CompactChip
import es.udc.emergencyapp.ui.common.CoordinateChip


@Composable
fun EmergencyDetailScreen(emergency: EmergencyDto, onClose: (() -> Unit)? = null) {
    // Track expanded quadrant ids (remember must be called from a @Composable scope)
    val expanded = remember { mutableStateListOf<Long>() }

    LazyColumn(modifier = Modifier.padding(12.dp)) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (onClose != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                        ) {
                            androidx.compose.material.TextButton(onClick = { onClose() }) {
                                Text(
                                    "Cerrar"
                                )
                            }
                        }
                    }
                    Text(
                        text = "#${emergency.id} - ${emergency.emergencyTypeName ?: ""}",
                        style = MaterialTheme.typography.h6
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = emergency.description ?: "(no description)")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Index: ${emergency.emergencyIndex ?: ""}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Created: ${emergency.createdAt ?: ""}")
                    Spacer(modifier = Modifier.height(8.dp))
                    if (emergency.location != null) {
                        CoordinateChip(
                            lonText = "${emergency.location.lon}",
                            latText = "${emergency.location.lat}",
                            quadrantName = null,
                            showCoordinates = true
                        )
                    } else {
                        val qCount = emergency.quadrantInfo?.size ?: 0
                        if (qCount > 0) CompactChip(label = "Cuadrantes $qCount")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (!emergency.quadrantInfo.isNullOrEmpty()) {
            item {
                Text(
                    text = "Cuadrantes afectados",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
            }
            items(emergency.quadrantInfo) { q ->
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (expanded.contains(q.id)) expanded.remove(q.id) else expanded.add(
                                    q.id
                                )
                            }, shape = RoundedCornerShape(8.dp), elevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Place, contentDescription = null)
                            Spacer(modifier = Modifier.size(8.dp))
                            Column {
                                Text(text = q.nombre ?: "-", style = MaterialTheme.typography.body1)
                                Text(
                                    text = "Escala: ${q.escala ?: "-"}",
                                    style = MaterialTheme.typography.caption
                                )
                            }
                        }
                    }

                    if (expanded.contains(q.id)) {
                        QuadrantResourcesScreen(emergencyId = emergency.id, quadrantId = q.id)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        if (emergency.location != null) {
            item {
                Text(
                    text = "Equipos disponibles (mock)",
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
                        Text(text = "Equipo #$t", style = MaterialTheme.typography.body1)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Text(
                    text = "Vehículos disponibles (mock)",
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
                        Text(text = "Vehículo #$v", style = MaterialTheme.typography.body1)
                    }
                }
            }
        }
    }
}
