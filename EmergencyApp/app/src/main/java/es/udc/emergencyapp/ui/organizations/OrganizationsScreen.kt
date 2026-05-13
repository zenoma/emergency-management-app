package es.udc.emergencyapp.ui.organizations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.udc.emergencyapp.R
import es.udc.emergencyapp.data.dto.OrganizationDto
import es.udc.emergencyapp.data.dto.OrganizationTypeDto
import es.udc.emergencyapp.net.HttpClient
import es.udc.emergencyapp.ui.common.CoordinateWithQuadrantChip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

@Composable
private fun OrganizationFilters(
    types: List<OrganizationTypeDto>,
    searchQuery: String,
    selectedTypeId: Long?,
    onSearchChange: (String) -> Unit,
    onSelectType: (Long?) -> Unit,
    onClear: () -> Unit
) {
    val selectedTypeName = types.firstOrNull { it.id == selectedTypeId }?.name
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            label = { Text(text = stringResource(R.string.search_label)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { expanded = true }) {
                Text(text = selectedTypeName ?: stringResource(R.string.organizations_all))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(onClick = {
                    onSelectType(null)
                    expanded = false
                }) {
                    Text(text = stringResource(R.string.organizations_all))
                }
                types.forEach { type ->
                    DropdownMenuItem(onClick = {
                        onSelectType(type.id)
                        expanded = false
                    }) {
                        Text(text = type.name)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onClear) {
                Text(text = stringResource(R.string.organizations_clear_filters))
            }
        }
    }
}

@Composable
private fun OrganizationCard(
    item: OrganizationDto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        elevation = 4.dp,
        backgroundColor = Color.White
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(end = 112.dp)) {
                Text(
                    text = item.name ?: "-",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0B3A66)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.code ?: "-", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = item.headquartersAddress ?: "-")
                Spacer(modifier = Modifier.height(8.dp))
                item.coordinates?.let { coords ->
                    CoordinateWithQuadrantChip(lon = coords.lon, lat = coords.lat)
                } ?: run {
                    Text(text = stringResource(R.string.coordinates_not_provided))
                }
            }

            if (!item.organizationTypeName.isNullOrBlank()) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    color = Color(0xFFE8F1FA),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = item.organizationTypeName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color(0xFF0B3A66),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun OrganizationsScreen(onOpenOrganization: (Long) -> Unit = {}) {
    val context = LocalContext.current
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var types by remember { mutableStateOf(listOf<OrganizationTypeDto>()) }
    var organizations by remember { mutableStateOf(listOf<OrganizationDto>()) }
    var selectedTypeId by remember { mutableStateOf<Long?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loading = true
        error = null
        try {
            withContext(Dispatchers.IO) {
                val typesBody = HttpClient.getFromHosts("/organizationTypes", context).first
                val orgsBody = HttpClient.getFromHosts("/organizations", context).first
                types = parseTypes(typesBody)
                organizations = parseOrganizations(orgsBody)
            }
        } catch (e: Exception) {
            error = e.message ?: "Unknown error"
        } finally {
            loading = false
        }
    }

    val filteredOrganizations = organizations.filter { org ->
        val matchesType = selectedTypeId == null || org.organizationTypeId == selectedTypeId
        val query = searchQuery.trim()
        val matchesQuery = query.isBlank() || listOfNotNull(
            org.code,
            org.name,
            org.headquartersAddress,
            org.organizationTypeName
        ).any { it.contains(query, ignoreCase = true) }
        matchesType && matchesQuery
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.organizations_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B3A66)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = stringResource(R.string.organizations_subtitle), color = Color(0xFF6B7280))

        Spacer(modifier = Modifier.height(12.dp))

        OrganizationFilters(
            types = types,
            searchQuery = searchQuery,
            selectedTypeId = selectedTypeId,
            onSearchChange = { searchQuery = it },
            onSelectType = { selectedTypeId = it },
            onClear = {
                searchQuery = ""
                selectedTypeId = null
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(
                R.string.organizations_results_format,
                filteredOrganizations.size,
                organizations.size
            ),
            color = Color(0xFF6B7280),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            !error.isNullOrBlank() -> Text(text = error ?: "Error", color = Color.Red)

            filteredOrganizations.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.organizations_empty))
            }

            else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredOrganizations) { item ->
                    OrganizationCard(
                        item = item,
                        onClick = { onOpenOrganization(item.id) }
                    )
                }
            }
        }
    }
}

private fun parseTypes(body: String?): List<OrganizationTypeDto> {
    if (body.isNullOrBlank()) return emptyList()
    val arr = JSONArray(body)
    return buildList {
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            add(
                OrganizationTypeDto(
                    id = obj.optLong("id", -1L),
                    name = obj.optString("name", "")
                )
            )
        }
    }
}

private fun parseOrganizations(body: String?): List<OrganizationDto> {
    if (body.isNullOrBlank()) return emptyList()
    val arr = JSONArray(body)
    return buildList {
        for (i in 0 until arr.length()) {
            arr.getJSONObject(i).let { add(it.toOrganizationDto()) }
        }
    }
}

private fun parseOrganization(body: String): OrganizationDto {
    return JSONObject(body).toOrganizationDto()
}

private fun parseJsonArray(body: String?): List<JSONObject> {
    if (body.isNullOrBlank()) return emptyList()
    val arr = JSONArray(body)
    return buildList {
        for (i in 0 until arr.length()) {
            add(arr.getJSONObject(i))
        }
    }
}

fun JSONObject.toOrganizationDto(): OrganizationDto {
    val coords = optJSONObject("coordinates")
    return OrganizationDto(
        id = optLong("id", -1L),
        code = optString("code", null),
        name = optString("name", null),
        headquartersAddress = optString("headquartersAddress", null),
        coordinates = coords?.let { JSONObjectToCoordinates(it) },
        createdAt = optString("createdAt", null),
        organizationTypeId = if (has("organizationTypeId") && !isNull("organizationTypeId")) optLong(
            "organizationTypeId"
        ) else null,
        organizationTypeName = optString("organizationTypeName", null)
    )
}

private fun JSONObjectToCoordinates(obj: JSONObject) =
    es.udc.emergencyapp.data.dto.CoordinatesDto(
        lon = if (obj.has("lon") && !obj.isNull("lon")) obj.optDouble("lon") else null,
        lat = if (obj.has("lat") && !obj.isNull("lat")) obj.optDouble("lat") else null
    )
