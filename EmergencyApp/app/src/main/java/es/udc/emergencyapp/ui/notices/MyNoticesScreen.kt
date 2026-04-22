package es.udc.emergencyapp.ui.notices

// use material Card (not material3) to avoid extra dependency
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.gson.Gson
import es.udc.emergencyapp.data.dto.NoticeDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MyNoticesScreen() {
    val ctx = LocalContext.current
    val noticesState = remember { mutableStateOf<List<NoticeDto>?>(null) }
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

            val userId = prefs.getLong("user_id", -1L)
            var resp: String? = null
            var successfulHost: String? = null
            withContext(Dispatchers.IO) {
                val path = if (userId > 0) "/notices?userId=$userId" else "/notices/me"
                val pair = es.udc.emergencyapp.net.HttpClient.getFromHosts(path, jwt, hostsToTry)
                resp = pair.first
                successfulHost = pair.second
            }

            if (resp != null) {
                try {
                    // Manual parse so we can build image URLs from imageDtoList
                    val ja = org.json.JSONArray(resp)
                    val parsed = mutableListOf<NoticeDto>()
                    for (i in 0 until ja.length()) {
                        try {
                            val o = ja.getJSONObject(i)
                            val id = o.optLong("id")
                            val body = if (o.has("body")) o.optString("body") else ""
                            val status = if (o.has("status")) o.optString("status") else null
                            val createdAt = if (o.has("createdAt")) o.optString("createdAt") else ""
                            val quadrantName =
                                if (o.has("quadrantName")) o.optString("quadrantName") else null
                            val quadrantId =
                                if (o.has("quadrantId")) if (o.isNull("quadrantId")) null else o.optInt(
                                    "quadrantId"
                                ) else null

                             val coords = if (o.has("coordinates") && !o.isNull("coordinates")) {
                                val c = o.getJSONObject("coordinates")
                                val lonRaw = if (c.has("lon")) c.optDouble("lon") else if (c.has("x")) c.optDouble("x") else Double.NaN
                                val latRaw = if (c.has("lat")) c.optDouble("lat") else if (c.has("y")) c.optDouble("y") else Double.NaN
                                if (!lonRaw.isNaN() && !latRaw.isNaN()) {
                                    val (finalLon, finalLat) = if (kotlin.math.abs(lonRaw) > 1000000 || kotlin.math.abs(latRaw) > 1000000) {
                                        es.udc.emergencyapp.util.transformProjectedToGeographic(lonRaw, latRaw)
                                    } else Pair(lonRaw, latRaw)
                                    es.udc.emergencyapp.data.dto.CoordinatesDto(finalLon, finalLat)
                                } else null
                            } else null

                            val imagesList = mutableListOf<es.udc.emergencyapp.data.dto.ImageDto>()
                            if (o.has("imageDtoList") && !o.isNull("imageDtoList")) {
                                val imgs = o.getJSONArray("imageDtoList")
                                for (j in 0 until imgs.length()) {
                                    try {
                                        val im = imgs.getJSONObject(j)
                                        val imgId =
                                            if (im.has("id") && !im.isNull("id")) im.optLong("id") else null
                                        val imgName =
                                            if (im.has("name") && !im.isNull("name")) im.optString("name") else null
                                        // construct a likely URL using the host that returned the notices JSON
                                        var imgUrl: String? = null
                                        try {
                                            val baseHost =
                                                successfulHost ?: hostsToTry.firstOrNull()
                                            if (!baseHost.isNullOrBlank()) {
                                                val altHost =
                                                    if (baseHost.contains("localhost")) baseHost.replace(
                                                        "localhost",
                                                        "10.0.2.2"
                                                    ) else baseHost
                                                val candidates = mutableListOf<String>()
                                                if (!imgName.isNullOrBlank()) {
                                                    candidates.add("$baseHost/images/$id/$imgName")
                                                    candidates.add("$baseHost/notices/$id/images/$imgName")
                                                    candidates.add("$altHost/images/$id/$imgName")
                                                }
                                                if (imgId != null) {
                                                    candidates.add("$baseHost/images/$id/$imgId")
                                                    candidates.add("$baseHost/notices/$id/images/$imgId")
                                                    candidates.add("$altHost/images/$id/$imgId")
                                                }
                                                if (candidates.isNotEmpty()) imgUrl =
                                                    candidates.first()
                                            }
                                        } catch (_: Exception) {
                                        }
                                        imagesList.add(
                                            es.udc.emergencyapp.data.dto.ImageDto(
                                                imgId,
                                                imgName,
                                                imgUrl
                                            )
                                        )
                                    } catch (_: Exception) {
                                    }
                                }
                            }

                            // userDto parsing omitted (keeps null)
                            val notice = NoticeDto(
                                id = id,
                                body = body,
                                status = status,
                                createdAt = createdAt,
                                quadrantName = quadrantName,
                                quadrantId = quadrantId,
                                userDto = null,
                                coordinates = coords,
                                images = imagesList
                            )
                            parsed.add(notice)
                        } catch (pe: Exception) {
                            android.util.Log.w("MyNotices", "Skipping malformed notice entry", pe)
                        }
                    }
                    noticesState.value = parsed
                } catch (e: Exception) {
                    android.util.Log.w("MyNotices", "Failed to parse notices JSON", e)
                    errorMsg.value = "Parse error"
                    noticesState.value = emptyList()
                }
            } else {
                noticesState.value = emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.w("MyNotices", "Failed to load notices", e)
            errorMsg.value = e.localizedMessage ?: e.toString()
            noticesState.value = emptyList()
        } finally {
            loading.value = false
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        // Filters: search and status
        val searchQuery = remember { mutableStateOf("") }
        val statusFilter = remember { mutableStateOf<String?>(null) }
        val showAll = remember { mutableStateOf(false) }
        val defaultLimit = 5

        // compute statuses available in the loaded list
        val allNotices = noticesState.value ?: emptyList()
        val statuses = remember(allNotices) {
            listOf<String?>(null) + allNotices.mapNotNull { it.status }.distinct()
        }

        // Search / filter UI
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = { Text("Search") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.padding(6.dp))
                // simple status dropdown
                var expanded by remember { mutableStateOf(false) }
                TextButton(onClick = { expanded = true }) {
                    Text(text = statusFilter.value ?: "All")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(onClick = {
                        statusFilter.value = null; expanded = false
                    }) { Text("All") }
                    for (s in statuses.filterNotNull()) {
                        DropdownMenuItem(onClick = {
                            statusFilter.value = s; expanded = false
                        }) { Text(s) }
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                val filteredCount = allNotices.count {
                    val matchesQuery = searchQuery.value.isBlank() || (it.body ?: "").contains(
                        searchQuery.value,
                        true
                    )
                    val matchesStatus =
                        statusFilter.value == null || statusFilter.value == it.status
                    matchesQuery && matchesStatus
                }
                Text(text = "Found: $filteredCount", modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = { showAll.value = !showAll.value }) {
                    Text(if (showAll.value) "Show less" else "Show all")
                }
            }
        }
        if (loading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            val rawList = noticesState.value ?: emptyList()
            // apply filters
            val filteredList = rawList.filter {
                val matchesQuery =
                    searchQuery.value.isBlank() || (it.body ?: "").contains(searchQuery.value, true)
                val matchesStatus = statusFilter.value == null || statusFilter.value == it.status
                matchesQuery && matchesStatus
            }
            val list = if (showAll.value) filteredList else filteredList.take(defaultLimit)
            if (filteredList.isEmpty()) {
                if (errorMsg.value != null) {
                    Text(text = "Error: ${errorMsg.value}", modifier = Modifier.padding(16.dp))
                } else {
                    Text(text = "No notices found", modifier = Modifier.padding(16.dp))
                }
            } else {
                LazyColumn {
                    items(list) { n ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    try {
                                        val intent = Intent(ctx, NoticeDetailActivity::class.java)
                                        intent.putExtra("notice", Gson().toJson(n))
                                        ctx.startActivity(intent)
                                    } catch (_: Exception) {
                                    }
                                }, shape = RoundedCornerShape(12.dp), elevation = 6.dp
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                // Image (if available)
                                val imageUrl = n.images?.firstOrNull()?.url
                                if (!imageUrl.isNullOrBlank()) {
                                    AndroidView(
                                        factory = { vctx ->
                                            val iv =
                                                androidx.appcompat.widget.AppCompatImageView(vctx)
                                            iv.scaleType =
                                                android.widget.ImageView.ScaleType.CENTER_CROP
                                            try {
                                                val prefsLocal = vctx.getSharedPreferences(
                                                    "app_prefs",
                                                    android.content.Context.MODE_PRIVATE
                                                )
                                                val jwtLocal =
                                                    prefsLocal.getString("jwt_token", null)
                                                val model = if (!jwtLocal.isNullOrEmpty()) {
                                                    val headers =
                                                        com.bumptech.glide.load.model.LazyHeaders.Builder()
                                                            .addHeader(
                                                                "Authorization",
                                                                "Bearer $jwtLocal"
                                                            )
                                                            .build()
                                                    com.bumptech.glide.load.model.GlideUrl(
                                                        imageUrl,
                                                        headers
                                                    )
                                                } else imageUrl

                                                com.bumptech.glide.Glide.with(vctx)
                                                    .load(model)
                                                    .centerCrop()
                                                    .into(iv)
                                            } catch (e: Exception) {
                                                android.util.Log.w(
                                                    "MyNotices",
                                                    "Glide load failed",
                                                    e
                                                )
                                            }
                                            iv
                                        }, modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(
                                                RoundedCornerShape(
                                                    topStart = 12.dp,
                                                    topEnd = 12.dp
                                                )
                                            )
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .background(Color.LightGray)
                                    )
                                }

                                // Text overlay at bottom of image
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp)
                                        .background(Color(0x66000000))
                                ) {
                                    Text(
                                        text = n.body ?: "(no description)",
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.h6
                                    )
                                    Text(
                                        text = "Status: ${n.status ?: ""} • ${n.createdAt}",
                                        color = Color.White,
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Use Row from androidx.compose.foundation.layout; no custom Row implementation needed.
