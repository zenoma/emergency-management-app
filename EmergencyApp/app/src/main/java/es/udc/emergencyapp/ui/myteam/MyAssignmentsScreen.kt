package es.udc.emergencyapp.ui.myteam

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.udc.emergencyapp.MainActivity
import es.udc.emergencyapp.R
import es.udc.emergencyapp.net.HttpClient
import es.udc.emergencyapp.ui.DrawerBadgeState
import es.udc.emergencyapp.ui.common.StatusChip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection

@Composable
fun MyAssignmentsScreen(teamId: Long) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var assignments by remember { mutableStateOf(listOf<JSONObject>()) }

    LaunchedEffect(teamId) {
        loading = true
        try {
            withContext(Dispatchers.IO) {
                val pair =
                    HttpClient.getFromHosts("/assignments?resourceId=$teamId", context, 3000, 3000)
                val body = pair.first
                if (body != null) {
                    val arr = JSONArray(body)
                    val list = mutableListOf<JSONObject>()
                    for (i in 0 until arr.length()) {
                        val item = arr.getJSONObject(i)
                        val status = item.optString("status").uppercase()
                        if (status != "COMPLETED" && status != "RELEASED") {
                            list.add(item)
                        }
                    }
                    assignments = list
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                context.getString(R.string.assignment_accept_failed_occupied),
                Toast.LENGTH_LONG
            ).show()
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.my_assignments_title),
            style = MaterialTheme.typography.h5
        )

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Column
        }

        if (assignments.isEmpty()) {
            Text(text = stringResource(R.string.no_active_assignments))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(assignments) { a ->
                    AssignmentCard(
                        assignment = a,
                        onAccept = { assignmentId ->
                            scope.launch {
                                val result = acceptAssignment(context, assignmentId)
                                val updated = result.first
                                if (updated != null) {
                                    assignments = assignments.map { current ->
                                        if (current.optLong(
                                                "id",
                                                -1L
                                            ) == assignmentId
                                        ) updated else current
                                    }.filter {
                                        val status = it.optString("status").uppercase()
                                        status != "COMPLETED" && status != "RELEASED"
                                    }
                                    DrawerBadgeState.refreshTrigger++
                                    (context as? MainActivity)?.refreshDrawerAssignments()
                                } else {
                                    val message =
                                        context.getString(R.string.no_se_ha_podido_aceptar_la_asignaci_n_porque_el_equipo_est_en_estado_ocupado)
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AssignmentCard(
    assignment: JSONObject,
    onAccept: (Long) -> Unit
) {
    val status = assignment.optString("status")
    val teamInfo = assignment.optJSONObject("teamInfo")
    val emergency = assignment.optJSONObject("emergencyInfo")
    val quadrant = assignment.optJSONObject("quadrantInfo")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${stringResource(R.string.assignment_prefix)} #${
                        assignment.optLong(
                            "id",
                            0
                        )
                    }"
                )
                Spacer(modifier = Modifier.weight(1f))
                StatusChip(status = status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            teamInfo?.let { team ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Business, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = team.optString("code", ""), style = MaterialTheme.typography.body1)
                    val org = team.optJSONObject("organization")
                    if (org != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = org.optString("code", ""),
                            color = androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                }
            }

            emergency?.let { em ->
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = em.optString(
                            "emergencyTypeName",
                            em.optString("name", stringResource(R.string.label_details))
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = em.optString("emergencyIndex", ""),
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                }
            }

            quadrant?.let { q ->
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = q.optString("nombre", q.optString("name", "")))
                    val quadrantId = q.optLong("id", -1L)
                    if (quadrantId > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "#$quadrantId", color = androidx.compose.ui.graphics.Color.Gray)
                    }
                }
            }

            val notes = assignment.optString("notes")
            if (notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${stringResource(R.string.label_details)}: $notes",
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }

            if (status.uppercase() == "PENDING") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onAccept(assignment.optLong("id", -1L)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.accept))
                }
            }
        }
    }
}

private suspend fun acceptAssignment(
    context: android.content.Context,
    assignmentId: Long
): Pair<JSONObject?, String?> {
    return withContext(Dispatchers.IO) {
        for (host in HttpClient.defaultHosts) {
            try {
                val url = java.net.URL("$host/assignments/$assignmentId/status")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "PUT"
                    doOutput = true
                    connectTimeout = 8000
                    readTimeout = 8000
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                        .getString("jwt_token", null)
                        ?.takeIf { it.isNotBlank() }
                        ?.let { setRequestProperty("Authorization", "Bearer $it") }
                }
                val payload = JSONObject().apply { put("status", "ACCEPTED") }.toString()
                conn.outputStream.bufferedWriter().use { it.write(payload) }
                val code = conn.responseCode
                val responseBody = if (code in 200..299) {
                    conn.inputStream.bufferedReader().use { it.readText() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readText() }
                }
                conn.disconnect()
                if (code in 200..299 && !responseBody.isNullOrBlank()) {
                    return@withContext Pair(JSONObject(responseBody), null)
                }
                if (code !in 200..299) {
                    return@withContext Pair(
                        null,
                        responseBody?.takeIf { it.isNotBlank() }
                            ?: context.getString(R.string.assignment_accept_failed))
                }
            } catch (_: Exception) {
            }
        }
        Pair(null, context.getString(R.string.assignment_accept_failed))
    }
}
