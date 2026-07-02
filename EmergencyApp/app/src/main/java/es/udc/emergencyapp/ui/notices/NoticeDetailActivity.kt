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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.load.model.LazyHeaders.Builder
import com.google.gson.Gson
import es.udc.emergencyapp.AppTheme
import es.udc.emergencyapp.R
import es.udc.emergencyapp.data.dto.NoticeDto
import es.udc.emergencyapp.ui.setContentWithSystemBars
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
                            title = { Text(text = stringResource(R.string.notice_title)) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icons.Filled.ArrowBack
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back_label)
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
                            text = notice.body ?: stringResource(R.string.description_default),
                            style = MaterialTheme.typography.h6
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = stringResource(
                                R.string.date_label,
                                es.udc.emergencyapp.util.DateUtils.formatForDisplay(notice.createdAt)
                            ),
                            style = MaterialTheme.typography.body2,
                            fontSize = 12.sp
                        )
                    }
                    val rawStatus = notice.status ?: ""
                    es.udc.emergencyapp.ui.common.StatusChip(status = rawStatus)
                }

                Spacer(modifier = Modifier.size(8.dp))
                Divider()
                Spacer(modifier = Modifier.size(8.dp))

                notice.coordinates?.let { c ->
                    es.udc.emergencyapp.ui.common.CoordinateWithQuadrantChip(lon = c.lon, lat = c.lat)
                } ?: run {
                    Text(text = stringResource(R.string.coordinates_not_provided))
                }

                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(
                        R.string.quadrant_format,
                        notice.quadrantName ?: "-",
                        notice.quadrantId ?: "-"
                    )
                )
            }
        }
    }
}

// Using shared CoordinateChip in ui.common
