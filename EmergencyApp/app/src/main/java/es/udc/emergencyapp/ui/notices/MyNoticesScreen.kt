package es.udc.emergencyapp.ui.notices

// use material Card (not material3) to avoid extra dependency
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
                for (host in hostsToTry) {
                    try {
                        val url =
                            if (userId > 0) java.net.URL("$host/notices?userId=$userId") else java.net.URL(
                                "$host/notices/me"
                            )
                        val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                            requestMethod = "GET"
                            connectTimeout = 8000
                            readTimeout = 8000
                            if (!jwt.isNullOrEmpty()) setRequestProperty(
                                "Authorization",
                                "Bearer $jwt"
                            )
                        }
                        val code = conn.responseCode
                        if (code in 200..299) {
                            resp = conn.inputStream.bufferedReader().use { it.readText() }
                            successfulHost = host
                        } else {
                            android.util.Log.w("MyNotices", "GET $host/notices/me returned $code")
                        }
                        conn.disconnect()
                        if (resp != null) break
                    } catch (_: Exception) {
                    }
                }
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
                            val quadrantName = if (o.has("quadrantName")) o.optString("quadrantName") else null
                            val quadrantId = if (o.has("quadrantId")) if (o.isNull("quadrantId")) null else o.optInt("quadrantId") else null

                            val coords = if (o.has("coordinates") && !o.isNull("coordinates")) {
                                val c = o.getJSONObject("coordinates")
                                val lon = if (c.has("lon")) c.optDouble("lon") else if (c.has("x")) c.optDouble("x") else Double.NaN
                                val lat = if (c.has("lat")) c.optDouble("lat") else if (c.has("y")) c.optDouble("y") else Double.NaN
                                if (!lon.isNaN() && !lat.isNaN()) {
                                    es.udc.emergencyapp.data.dto.CoordinatesDto(lon, lat)
                                } else null
                            } else null

                            val imagesList = mutableListOf<es.udc.emergencyapp.data.dto.ImageDto>()
                            if (o.has("imageDtoList") && !o.isNull("imageDtoList")) {
                                val imgs = o.getJSONArray("imageDtoList")
                                for (j in 0 until imgs.length()) {
                                    try {
                                        val im = imgs.getJSONObject(j)
                                        val imgId = if (im.has("id") && !im.isNull("id")) im.optLong("id") else null
                                        val imgName = if (im.has("name") && !im.isNull("name")) im.optString("name") else null
                                        // construct a likely URL using the host that returned the notices JSON
                                        var imgUrl: String? = null
                                        try {
                                            val baseHost = successfulHost ?: hostsToTry.firstOrNull()
                                            if (!baseHost.isNullOrBlank()) {
                                                val altHost = if (baseHost.contains("localhost")) baseHost.replace("localhost", "10.0.2.2") else baseHost
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
                                                if (candidates.isNotEmpty()) imgUrl = candidates.first()
                                            }
                                        } catch (_: Exception) {}
                                        imagesList.add(es.udc.emergencyapp.data.dto.ImageDto(imgId, imgName, imgUrl))
                                    } catch (_: Exception) {
                                    }
                                }
                            }

                            // userDto parsing omitted (keeps null)
                            val notice = NoticeDto(id = id, body = body, status = status, createdAt = createdAt, quadrantName = quadrantName, quadrantId = quadrantId, userDto = null, coordinates = coords, images = imagesList)
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
        if (loading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            val list = noticesState.value ?: emptyList()
            if (list.isEmpty()) {
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
                                                val prefsLocal = vctx.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                                                val jwtLocal = prefsLocal.getString("jwt_token", null)
                                                val model = if (!jwtLocal.isNullOrEmpty()) {
                                                    val headers = com.bumptech.glide.load.model.LazyHeaders.Builder()
                                                        .addHeader("Authorization", "Bearer $jwtLocal")
                                                        .build()
                                                    com.bumptech.glide.load.model.GlideUrl(imageUrl, headers)
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
