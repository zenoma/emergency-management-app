package es.udc.emergencyapp.ui.emergencies

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import es.udc.emergencyapp.R
import es.udc.emergencyapp.data.dto.EmergencyDto
import es.udc.emergencyapp.data.dto.QuadrantInfoDto
import es.udc.emergencyapp.indexColor
import es.udc.emergencyapp.net.HttpClient
import es.udc.emergencyapp.ui.common.CompactChip
import es.udc.emergencyapp.ui.common.CoordinateWithQuadrantChip
import es.udc.emergencyapp.ui.notices.NoticeDetailActivity
import es.udc.emergencyapp.util.transformProjectedToGeographic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EmergenciesScreen() {
    val ctx = LocalContext.current
    val emergenciesState = remember { mutableStateOf<List<EmergencyDto>?>(null) }
    val loading = remember { mutableStateOf(true) }
    val errorMsg = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loading.value = true
        try {
            var resp: String? = null
            withContext(Dispatchers.IO) {
                val pair = HttpClient.getFromHosts("/emergencies", ctx)
                resp = pair.first
            }

            if (resp != null) {
                try {
                    emergenciesState.value = parseEmergenciesJson(resp)
                } catch (e: Exception) {
                    android.util.Log.w("Emergencies", "Failed to parse emergencies JSON", e)
                    errorMsg.value = ctx.getString(R.string.parse_error)
                    emergenciesState.value = emptyList()
                }
            } else {
                emergenciesState.value = emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.w("Emergencies", "Failed to load emergencies", e)
            errorMsg.value = e.localizedMessage ?: e.toString()
            emergenciesState.value = emptyList()
        } finally {
            loading.value = false
        }
    }

    Column(modifier = Modifier.padding(12.dp)) {
        val searchQuery = remember { mutableStateOf("") }
        val showAll = remember { mutableStateOf(false) }
        val defaultLimit = 12
        val sortByCreatedDesc = remember { mutableStateOf(true) }

        if (loading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            return@Column
        }

        val rawList = emergenciesState.value ?: emptyList()
        var filtered = rawList.filter {
            searchQuery.value.isBlank() || (it.description ?: "").contains(
                searchQuery.value,
                true
            ) || (it.emergencyTypeName ?: "").contains(searchQuery.value, true)
        }

        filtered =
            if (sortByCreatedDesc.value) filtered.sortedByDescending { it.createdAt } else filtered.sortedBy { it.createdAt }
        Text(
            text = stringResource(R.string.emergencies_title),
            style = MaterialTheme.typography.h5,
        )
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.emergencies_total, filtered.size),
                modifier = Modifier.padding(end = 8.dp)
            )
            Button(onClick = { sortByCreatedDesc.value = !sortByCreatedDesc.value }) {
                Text(
                    text = if (sortByCreatedDesc.value) stringResource(R.string.sort_created_desc) else stringResource(
                        R.string.sort_created_asc
                    )
                )
            }
            Spacer(modifier = Modifier.padding(6.dp))
            TextButton(onClick = { showAll.value = !showAll.value }) {
                Text(if (showAll.value) stringResource(R.string.show_less) else stringResource(R.string.show_all))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text(stringResource(R.string.search_description_or_type)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Text(
                text = if (errorMsg.value != null) errorMsg.value
                    ?: "" else stringResource(R.string.no_emergencies_found),
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }

        val list = if (showAll.value) filtered else filtered.take(defaultLimit)

        LazyColumn {
            itemsIndexed(list) { idx, e ->
                val bg = if (idx % 2 == 0) Color.White else Color(0xFFF8F8F8)
                val openDetails = {
                    try {
                        val intent = Intent(ctx, EmergencyDetailActivity::class.java)
                        intent.putExtra("emergency", Gson().toJson(e))
                        // prefer Activity.startActivity when possible, otherwise add FLAG_ACTIVITY_NEW_TASK
                        val activity = ctx as? android.app.Activity
                        if (activity != null) {
                            activity.startActivity(intent)
                        } else {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            ctx.startActivity(intent)
                        }
                    } catch (ex: android.content.ActivityNotFoundException) {
                        // Fallback: if the new EmergencyDetailActivity isn't available (manifest/build issue),
                        // fallback to the legacy NoticeDetailActivity (which is present in the manifest)
                        android.util.Log.w(
                            "Emergencies",
                            "EmergencyDetailActivity not found, falling back to NoticeDetailActivity",
                            ex
                        )
                        try {
                            val fallback = Intent(ctx, NoticeDetailActivity::class.java)
                            fallback.putExtra("notice", Gson().toJson(e))
                            val activity = ctx as? android.app.Activity
                            if (activity != null) {
                                activity.startActivity(fallback)
                            } else {
                                fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                ctx.startActivity(fallback)
                            }
                        } catch (ex2: Exception) {
                            android.util.Log.w("Emergencies", "Fallback also failed", ex2)
                            Toast.makeText(
                                ctx,
                                ctx.getString(
                                    R.string.failed_to_send_notice,
                                    ex2.localizedMessage ?: ""
                                ),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (ex: Exception) {
                        android.util.Log.w("Emergencies", "Failed to open detail", ex)
                        Toast.makeText(
                            ctx,
                            ctx.getString(
                                R.string.failed_to_send_notice,
                                ex.localizedMessage ?: ""
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { openDetails() },
                    shape = RoundedCornerShape(8.dp),
                    elevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .background(bg)
                            .padding(10.dp)
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {

                        val icon = es.udc.emergencyapp.util.emergencyTypeIcon(e.emergencyTypeName)
                        val idxColor = indexColor(e.emergencyIndex)
                        androidx.compose.material.Surface(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = idxColor
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                            ) {
                                androidx.compose.material.Icon(
                                    imageVector = icon,
                                    contentDescription = e.emergencyTypeName,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = e.description ?: "(no description)",
                                    style = MaterialTheme.typography.subtitle1,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.padding(6.dp))
                                Text(
                                    text = "#${e.id}",
                                    style = MaterialTheme.typography.caption,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = (e.emergencyTypeName
                                    ?: "") + (if (!e.quadrantInfo.isNullOrEmpty()) " • ${e.quadrantInfo.first().nombre ?: ""}" else ""),
                                style = MaterialTheme.typography.body2,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = es.udc.emergencyapp.util.DateUtils.formatForDisplay(e.createdAt),
                                    style = MaterialTheme.typography.caption
                                )
                                Spacer(modifier = Modifier.padding(8.dp))
                                val loc = e.location
                                if (loc != null) {
                                    CoordinateWithQuadrantChip(
                                        lon = loc.lon,
                                        lat = loc.lat,
                                        showCoordinates = false
                                    )
                                } else {
                                    // If no location provided, show number of affected quadrants if available
                                    val qCount = e.quadrantInfo?.size ?: 0
                                    if (qCount > 0) {
                                        CompactChip(label = "Cuadrantes $qCount")
                                    } else {
                                        Text(
                                            text = "-",
                                            style = MaterialTheme.typography.caption,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            CompactChip(label = e.emergencyIndex ?: "", bgColor = idxColor)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun parseEmergenciesJson(resp: String): List<EmergencyDto> {
    val ja = org.json.JSONArray(resp)
    val parsed = mutableListOf<EmergencyDto>()
    for (i in 0 until ja.length()) {
        try {
            val o = ja.getJSONObject(i)
            if ((o.has("resolvedAt") && !o.isNull("resolvedAt")) || o.optString("status", "")
                    .equals("resolved", ignoreCase = true)
            ) continue

            val id = o.optLong("id")
            val desc = if (o.has("description")) o.optString("description") else null
            val typeName =
                if (o.has("emergencyTypeName")) o.optString("emergencyTypeName") else null
            val index = if (o.has("emergencyIndex")) o.optString("emergencyIndex") else null
            val createdAt = if (o.has("createdAt")) o.optString("createdAt") else null
            val resolvedAt =
                if (o.has("resolvedAt")) if (o.isNull("resolvedAt")) null else o.optString("resolvedAt") else null

            val location = if (o.has("location") && !o.isNull("location")) {
                val c = o.getJSONObject("location")
                val lonRaw =
                    if (c.has("lon")) c.optDouble("lon") else if (c.has("x")) c.optDouble("x") else Double.NaN
                val latRaw =
                    if (c.has("lat")) c.optDouble("lat") else if (c.has("y")) c.optDouble("y") else Double.NaN
                if (!lonRaw.isNaN() && !latRaw.isNaN()) {
                    val (finalLon, finalLat) = if (kotlin.math.abs(lonRaw) > 1000000 || kotlin.math.abs(
                            latRaw
                        ) > 1000000
                    ) {
                        transformProjectedToGeographic(lonRaw, latRaw)
                    } else Pair(lonRaw, latRaw)
                    es.udc.emergencyapp.data.dto.CoordinatesDto(finalLon, finalLat)
                } else null
            } else null

            val quadrants = mutableListOf<QuadrantInfoDto>()
            if (o.has("quadrantInfo") && !o.isNull("quadrantInfo")) {
                val qarr = o.getJSONArray("quadrantInfo")
                for (j in 0 until qarr.length()) {
                    try {
                        val q = qarr.getJSONObject(j)
                        quadrants.add(
                            QuadrantInfoDto(
                                q.optLong("id"),
                                q.optString("escala"),
                                q.optString("nombre")
                            )
                        )
                    } catch (_: Exception) {
                    }
                }
            }

            parsed.add(
                EmergencyDto(
                    id = id,
                    description = desc,
                    emergencyTypeName = typeName,
                    emergencyIndex = index,
                    createdAt = createdAt,
                    resolvedAt = resolvedAt,
                    location = location,
                    quadrantInfo = quadrants
                )
            )
        } catch (_: Exception) {
        }
    }
    return parsed
}
