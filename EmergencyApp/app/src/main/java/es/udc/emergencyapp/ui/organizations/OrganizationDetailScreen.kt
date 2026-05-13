package es.udc.emergencyapp.ui.organizations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.udc.emergencyapp.R
import es.udc.emergencyapp.data.dto.OrganizationDto
import es.udc.emergencyapp.net.HttpClient
import es.udc.emergencyapp.ui.common.CoordinateWithQuadrantChip
import es.udc.emergencyapp.ui.common.StatusChip
import es.udc.emergencyapp.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

@Composable
fun OrganizationDetailScreen(organizationId: Long, onBack: () -> Unit) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var organization by remember { mutableStateOf<OrganizationDto?>(null) }
    var teams by remember { mutableStateOf(listOf<JSONObject>()) }
    var vehicles by remember { mutableStateOf(listOf<JSONObject>()) }

    LaunchedEffect(organizationId) {
        loading = true
        error = null
        try {
            withContext(Dispatchers.IO) {
                val orgPair = HttpClient.getFromHosts("/organizations/$organizationId", context)
                val teamsPair =
                    HttpClient.getFromHosts("/teams?organizationId=$organizationId", context)
                val vehiclesPair =
                    HttpClient.getFromHosts("/vehicles?organizationId=$organizationId", context)

                organization = orgPair.first?.let { parseOrganization(it) }
                teams = parseJsonArray(teamsPair.first)
                vehicles = parseJsonArray(vehiclesPair.first)
            }
        } catch (e: Exception) {
            error = e.message ?: "Unknown error"
        } finally {
            loading = false
        }
    }

    when {
        loading -> CircularProgressIndicator()
        !error.isNullOrBlank() -> Text(text = error ?: "Error", color = Color.Red)
        organization == null -> Text(text = stringResource(R.string.organizations_empty))
        else -> {
            val org = organization!!
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onBack) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                            Text(text = stringResource(R.string.back_label))
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = 6.dp,
                        backgroundColor = Color(0xFFF7FAFC)
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 112.dp)) {
                                Text(
                                    text = org.name ?: "-",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0B3A66)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = org.code ?: "-", fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = org.headquartersAddress ?: "-")
                                Spacer(modifier = Modifier.height(8.dp))
                                org.coordinates?.let { coords ->
                                    CoordinateWithQuadrantChip(lon = coords.lon, lat = coords.lat)
                                }
                                if (!org.createdAt.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(
                                            R.string.create_date_label,
                                            DateUtils.formatForDisplay(org.createdAt)
                                        ),
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            if (!org.organizationTypeName.isNullOrBlank()) {
                                Surface(
                                    modifier = Modifier.align(Alignment.TopEnd),
                                    color = Color(0xFFE8F1FA),
                                    shape = RoundedCornerShape(999.dp)
                                ) {
                                    Text(
                                        text = org.organizationTypeName,
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        ),
                                        color = Color(0xFF0B3A66),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.organizations_teams_title),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B3A66)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(teams) { team ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Business, contentDescription = null)
                                Spacer(modifier = Modifier.height(0.dp))
                                Text(
                                    text = team.optString("code", ""),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                StatusChip(status = team.optString("status", null))
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.organizations_vehicles_title),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0B3A66)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(vehicles) { vehicle ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null)
                                Spacer(modifier = Modifier.height(0.dp))
                                Text(
                                    text = vehicle.optString("vehiclePlate", ""),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                StatusChip(status = vehicle.optString("status", null))
                            }
                            val type = vehicle.optString("type", null)
                            if (!type.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = type, color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseOrganization(body: String): OrganizationDto = JSONObject(body).toOrganizationDto()

private fun parseJsonArray(body: String?): List<JSONObject> {
    if (body.isNullOrBlank()) return emptyList()
    val arr = org.json.JSONArray(body)
    return buildList {
        for (i in 0 until arr.length()) {
            add(arr.getJSONObject(i))
        }
    }
}
