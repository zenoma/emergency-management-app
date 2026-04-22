package es.udc.emergencyapp.ui.emergencies

import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import es.udc.emergencyapp.data.dto.EmergencyDto
import es.udc.emergencyapp.data.dto.QuadrantInfoDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EmergenciesScreen() {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val emergenciesState = remember { mutableStateOf<List<EmergencyDto>?>(null) }
    val loading = remember { mutableStateOf(true) }
    val errorMsg = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        loading.value = true
        try {
            val hostsToTry = listOf(
                "http://localhost:8080",
                "http://10.0.2.2:8080",
                "http://10.0.2.2:8000",
                "http://10.0.3.2:8000",
                "http://127.0.0.1:8000",
                "http://192.168.1.100:8000"
            )
            val prefs = ctx.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
            val jwt = prefs.getString("jwt_token", null)

            var resp: String? = null
            var successfulHost: String? = null
            withContext(Dispatchers.IO) {
                val pair = es.udc.emergencyapp.net.HttpClient.getFromHosts("/emergencies", jwt, hostsToTry)
                resp = pair.first
                successfulHost = pair.second
            }

            if (resp != null) {
                try {
                    val ja = org.json.JSONArray(resp)
                    val parsed = mutableListOf<EmergencyDto>()
                    for (i in 0 until ja.length()) {
                        try {
                            val o = ja.getJSONObject(i)
                            // skip resolved
                            if ((o.has("resolvedAt") && !o.isNull("resolvedAt")) || o.optString("status", "").equals("resolved", ignoreCase = true)) continue

                            val id = o.optLong("id")
                            val desc = if (o.has("description")) o.optString("description") else null
                            val typeName = if (o.has("emergencyTypeName")) o.optString("emergencyTypeName") else null
                            val index = if (o.has("emergencyIndex")) o.optString("emergencyIndex") else null
                            val createdAt = if (o.has("createdAt")) o.optString("createdAt") else null
                            val resolvedAt = if (o.has("resolvedAt")) if (o.isNull("resolvedAt")) null else o.optString("resolvedAt") else null

                            val location = if (o.has("location") && !o.isNull("location")) {
                                val c = o.getJSONObject("location")
                                val lon = if (c.has("lon")) c.optDouble("lon") else if (c.has("x")) c.optDouble("x") else Double.NaN
                                val lat = if (c.has("lat")) c.optDouble("lat") else if (c.has("y")) c.optDouble("y") else Double.NaN
                                if (!lon.isNaN() && !lat.isNaN()) es.udc.emergencyapp.data.dto.CoordinatesDto(lon, lat) else null
                            } else null

                            val quadrants = mutableListOf<QuadrantInfoDto>()
                            if (o.has("quadrantInfo") && !o.isNull("quadrantInfo")) {
                                val qarr = o.getJSONArray("quadrantInfo")
                                for (j in 0 until qarr.length()) {
                                    try {
                                        val q = qarr.getJSONObject(j)
                                        quadrants.add(QuadrantInfoDto(q.optLong("id"), q.optString("escala"), q.optString("nombre")))
                                    } catch (_: Exception) {}
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
                        } catch (_: Exception) {}
                    }
                    emergenciesState.value = parsed
                } catch (e: Exception) {
                    android.util.Log.w("Emergencies", "Failed to parse emergencies JSON", e)
                    errorMsg.value = "Parse error"
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

    Column(modifier = Modifier.padding(8.dp)) {
        val searchQuery = remember { mutableStateOf("") }
        val showAll = remember { mutableStateOf(false) }
        val defaultLimit = 8

        if (loading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            return@Column
        }

        val rawList = emergenciesState.value ?: emptyList()
        val filtered = rawList.filter {
            searchQuery.value.isBlank() || (it.description ?: "").contains(searchQuery.value, true) || (it.emergencyTypeName ?: "").contains(searchQuery.value, true)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = searchQuery.value, onValueChange = { searchQuery.value = it }, label = { Text("Search") }, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.padding(6.dp))
            TextButton(onClick = { showAll.value = !showAll.value }) {
                Text(if (showAll.value) "Show less" else "Show all")
            }
        }

        if (filtered.isEmpty()) {
            Text(text = if (errorMsg.value != null) "Error: ${errorMsg.value}" else "No emergencies found", modifier = Modifier.padding(16.dp))
        } else {
            val list = if (showAll.value) filtered else filtered.take(defaultLimit)
            LazyColumn {
                items(list) { e ->
                    Card(modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            try {
                                // placeholder: show raw json in a new activity
                                val intent = Intent(ctx, es.udc.emergencyapp.ui.notices.NoticeDetailActivity::class.java)
                                intent.putExtra("notice", Gson().toJson(e))
                                ctx.startActivity(intent)
                            } catch (_: Exception) {}
                        }, shape = RoundedCornerShape(12.dp), elevation = 6.dp) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = e.description ?: "(no description)", style = MaterialTheme.typography.h6)
                            Text(text = "Type: ${e.emergencyTypeName ?: ""} • Index: ${e.emergencyIndex ?: ""}")
                            Text(text = "Created: ${e.createdAt ?: ""}")
                            if (!e.quadrantInfo.isNullOrEmpty()) {
                                Text(text = "Quadrants: ${e.quadrantInfo.joinToString { it.nombre ?: it.id.toString() }}")
                            }
                        }
                    }
                }
            }
        }
    }
}
