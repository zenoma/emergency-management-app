package es.udc.emergencyapp.ui.notices

import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.load.model.LazyHeaders.Builder
import com.google.gson.Gson
import es.udc.emergencyapp.AppTheme
import es.udc.emergencyapp.data.dto.NoticeDto
import es.udc.emergencyapp.ui.setContentWithSystemBars
import es.udc.emergencyapp.util.DateUtils
import es.udc.emergencyapp.util.transformProjectedToGeographic

class NoticeDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = intent.getStringExtra("notice") ?: return
        val notice = Gson().fromJson(json, NoticeDto::class.java)

        setContentWithSystemBars {
            AppTheme {
                androidx.compose.material.Scaffold(
                    topBar = {
                        androidx.compose.material.TopAppBar(
                            title = { Text(text = "Notice") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icons.Filled.ArrowBack
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        androidx.compose.material.BottomAppBar {
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colors.background
                    ) {
                        NoticeDetailScreen(notice = notice)
                    }
                }
            }
        }
    }
}

@Composable
fun NoticeDetailScreen(notice: NoticeDto) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {

        val imageUrl = notice.images?.firstOrNull()?.url
        if (!imageUrl.isNullOrBlank()) {
            AndroidView(
                factory = { ctx ->
                    val iv = AppCompatImageView(ctx)
                    iv.layoutParams = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    )
                    iv.adjustViewBounds = true
                    try {
                        val prefsLocal =
                            ctx.getSharedPreferences(
                                "app_prefs",
                                android.content.Context.MODE_PRIVATE
                            )
                        val jwtLocal = prefsLocal.getString("jwt_token", null)
                        val model = if (!jwtLocal.isNullOrEmpty()) {
                            val headers = Builder()
                                .addHeader("Authorization", "Bearer $jwtLocal")
                                .build()
                            com.bumptech.glide.load.model.GlideUrl(imageUrl, headers)
                        } else imageUrl

                        com.bumptech.glide.Glide.with(ctx).load(model).centerCrop().into(iv)
                    } catch (e: Exception) {
                        android.util.Log.w("NoticeDetail", "Glide load failed", e)
                    }
                    iv
                }, modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = notice.body ?: "(no description)",
                            style = MaterialTheme.typography.h6
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "Date: ${DateUtils.formatServerDate(notice.createdAt)}",
                            style = MaterialTheme.typography.body2,
                            fontSize = 12.sp
                        )
                    }
                    val rawStatus = notice.status ?: ""
                    // Use common StatusChip composable for consistent status UI
                    es.udc.emergencyapp.ui.common.StatusChip(status = rawStatus)
                }

                Spacer(modifier = Modifier.size(8.dp))
                Divider()
                Spacer(modifier = Modifier.size(8.dp))

                // Show coordinates as a small chip with quadrant name fetched from backend
                notice.coordinates?.let { c ->
                    val lonRaw = c.lon ?: Double.NaN
                    val latRaw = c.lat ?: Double.NaN
                    val (lon, lat) = if (kotlin.math.abs(lonRaw) > 1000000 || kotlin.math.abs(latRaw) > 1000000) {
                        transformProjectedToGeographic(lonRaw, latRaw)
                    } else Pair(lonRaw, latRaw)

                    if (lon.isNaN()) "-" else "${"%.6f".format(lon)}"
                    if (lat.isNaN()) "-" else "${"%.6f".format(lat)}"

                    es.udc.emergencyapp.ui.common.CoordinateWithQuadrantChip(lon = lon, lat = lat)
                } ?: run {
                    Text(text = "Coordinates: (not provided)")
                }

                Spacer(modifier = Modifier.size(8.dp))
                Text(text = "Quadrant: ${notice.quadrantName ?: "-"} (${notice.quadrantId ?: "-"})")
            }
        }
    }
}

// Using shared CoordinateChip in ui.common
