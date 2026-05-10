package es.udc.emergencyapp.ui.emergencies

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Group
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import es.udc.emergencyapp.R
import es.udc.emergencyapp.net.HttpClient
import es.udc.emergencyapp.ui.common.AssignmentResourceCard
import es.udc.emergencyapp.ui.common.StatusChip
import es.udc.emergencyapp.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun QuadrantResourcesScreen(
    emergencyId: Long,
    quadrantId: Long,
    onClose: (() -> Unit)? = null,
    externalRefreshCounter: Int = 0
) {
    val loading = remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var assignments by remember { mutableStateOf<List<JSONObject>>(emptyList()) }

    val ctx = LocalContext.current
    val lastHostAttempt = remember { mutableStateOf<String?>(null) }
    val retryTrigger = remember { mutableStateOf(0) }
    val cacheKey = Pair(emergencyId, quadrantId)

    @Suppress("StaticDeclarationInLocalScope")
    val assignmentsCache = remember { Companion.assignmentsCache }
    LaunchedEffect(emergencyId, quadrantId, retryTrigger.value, externalRefreshCounter) {
        loading.value = true
        error = null
        if (retryTrigger.value == 0) {
            val cached = assignmentsCache[cacheKey]
            if (cached != null) {
                assignments = cached
                loading.value = false
                return@LaunchedEffect
            }
        }
        try {
            val path = "/assignments?quadrantId=${quadrantId}&emergencyId=${emergencyId}"
            val pair =
                withContext(Dispatchers.IO) { HttpClient.getFromHosts(path, ctx, 3000, 3000) }
            lastHostAttempt.value = pair.second
            val body = pair.first
            if (body != null) {
                val arr = JSONArray(body)
                val aList = mutableListOf<JSONObject>()
                for (i in 0 until arr.length()) {
                    try {
                        val o = arr.getJSONObject(i)
                        aList.add(o)
                    } catch (_: Exception) {
                    }
                }
                assignments = aList
                // store in cache
                try {
                    assignmentsCache[cacheKey] = aList
                } catch (_: Exception) {
                }
            } else {
                error = ctx.getString(R.string.no_response)
            }
        } catch (e: Exception) {
            error = e.localizedMessage ?: e.toString()
        } finally {
            loading.value = false
        }
    }

    Column(modifier = Modifier.padding(12.dp)) {
        Text(
            text = stringResource(R.string.resources_in_quadrant),
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (loading.value) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
            return@Column
        }

        if (error != null) {
            Text(text = error ?: "", color = MaterialTheme.colors.error)
            lastHostAttempt.value?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.caption
                )
            }
            androidx.compose.material.TextButton(onClick = {
                retryTrigger.value = retryTrigger.value + 1
            }) {
                Text(stringResource(R.string.retry))
            }
            return@Column
        }

        Text(
            text = stringResource(R.string.assignments_label),
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )

        if (assignments.isEmpty()) {
            Text(text = stringResource(R.string.no_assignments))
        } else {
            LazyColumn {
                items(assignments) { a -> ModernAssignmentRow(a) }
            }
        }
    }
}

// Companion object-style cache stored at file-level so it's shared across recompositions/activities
private object Companion {
    val assignmentsCache: MutableMap<Pair<Long, Long>, List<JSONObject>> = mutableMapOf()
}

@Composable
private fun TeamRow(team: JSONObject) {
    val name = team.optString("code") ?: ""
    val org = team.optJSONObject("organization")?.optString("name") ?: ""
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = name, style = MaterialTheme.typography.body1)
            Text(text = org, style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
private fun VehicleRow(vehicle: JSONObject) {
    val plate = vehicle.optString("vehiclePlate") ?: ""
    val type = vehicle.optString("type") ?: ""
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = plate, style = MaterialTheme.typography.body1)
            Text(text = type, style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
private fun AssignmentRow(assignment: JSONObject) {
    val status = assignment.optString("status")
    val assignedAt = assignment.optString("assignedAt")
    val acceptedAt = assignment.optString("acceptedAt")
    val completedAt = assignment.optString("completedAt")
    val teamJson =
        if (assignment.has("teamInfo") && !assignment.isNull("teamInfo")) assignment.optJSONObject("teamInfo") else null
    val vehicleJson =
        if (assignment.has("vehicleInfo") && !assignment.isNull("vehicleInfo")) assignment.optJSONObject(
            "vehicleInfo"
        ) else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.status_label, status),
                style = MaterialTheme.typography.body1
            )
            if (assignedAt.isNotBlank()) Text(
                text = stringResource(R.string.assigned_label, assignedAt),
                style = MaterialTheme.typography.caption
            )
            if (acceptedAt.isNotBlank()) Text(
                text = stringResource(R.string.accepted_label, acceptedAt),
                style = MaterialTheme.typography.caption
            )
            if (completedAt.isNotBlank()) Text(
                text = stringResource(R.string.completed_label, completedAt),
                style = MaterialTheme.typography.caption
            )

            teamJson?.let { t ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.team_label))
                Text(text = stringResource(R.string.code_label, t.optString("code")))
                val org = t.optJSONObject("organization")
                if (org != null) {
                    Text(
                        text = stringResource(
                            R.string.org_label,
                            org.optString("code"),
                            org.optString("name")
                        )
                    )
                }
            }

            vehicleJson?.let { v ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(R.string.vehicle_label))
                Text(text = stringResource(R.string.plate_label, v.optString("vehiclePlate")))
                Text(text = stringResource(R.string.type_label, v.optString("type")))
                val org = v.optJSONObject("organization")
                if (org != null) {
                    Text(
                        text = stringResource(
                            R.string.org_label,
                            org.optString("code"),
                            org.optString("name")
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernAssignmentRow(assignment: JSONObject) {
    AssignmentResourceCard(assignment)
}
