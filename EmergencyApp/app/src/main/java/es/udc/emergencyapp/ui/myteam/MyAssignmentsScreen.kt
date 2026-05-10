package es.udc.emergencyapp.ui.myteam

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp
import es.udc.emergencyapp.net.HttpClient
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
    var error by remember { mutableStateOf<String?>(null) }
    var assignments by remember { mutableStateOf(listOf<JSONObject>()) }

    LaunchedEffect(teamId) {
        loading = true
        error = null
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
                        if (item.optString("status").uppercase() != "COMPLETED") {
                            list.add(item)
                        }
                    }
                    assignments = list
                } else {
                    error = "No response"
                }
            }
        } catch (e: Exception) {
            error = e.localizedMessage ?: e.toString()
        } finally {
            loading = false
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(text = "Mis assignments", style = MaterialTheme.typography.h5)

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

        if (!error.isNullOrBlank()) {
            Text(text = error ?: "Error", color = MaterialTheme.colors.error)
            return@Column
        }

        if (assignments.isEmpty()) {
            Text(text = "No hay asignaciones activas")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(assignments) { a ->
                    AssignmentCard(
                        assignment = a,
                        onAccept = { assignmentId ->
                            scope.launch {
                                val updated = acceptAssignment(context, assignmentId)
                                if (updated != null) {
                                    assignments = assignments.map { current ->
                                        if (current.optLong("id", -1L) == assignmentId) updated else current
                                    }.filter { it.optString("status").uppercase() != "COMPLETED" }
                                } else {
                                    error = "No se ha podido aceptar la asignación"
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
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "#${assignment.optLong("id", 0)}")
            StatusChip(status = status)
            val emergency = assignment.optJSONObject("emergencyInfo")
            if (emergency != null) {
                Text(text = emergency.optString("name", "Emergencia"))
                Text(text = emergency.optString("description", ""))
            }
            val notes = assignment.optString("notes")
            if (notes.isNotBlank()) {
                Text(text = notes)
            }

            if (status.uppercase() == "PENDING") {
                Button(
                    onClick = { onAccept(assignment.optLong("id", -1L)) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(text = "Aceptar")
                }
            }
        }
    }
}

private suspend fun acceptAssignment(
    context: android.content.Context,
    assignmentId: Long
): JSONObject? {
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
                    return@withContext JSONObject(responseBody)
                }
            } catch (_: Exception) {
            }
        }
        null
    }
}
