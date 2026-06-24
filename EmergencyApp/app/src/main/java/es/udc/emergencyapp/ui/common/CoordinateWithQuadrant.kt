package es.udc.emergencyapp.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import es.udc.emergencyapp.net.HttpClient
import es.udc.emergencyapp.util.transformProjectedToGeographic
import es.udc.emergencyapp.util.transformWgs84ToUtm29
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun CoordinateWithQuadrantChip(lon: Double?, lat: Double?, showCoordinates: Boolean = true) {
    val ctx = LocalContext.current
    val lonVal = lon ?: Double.NaN
    val latVal = lat ?: Double.NaN

    val isProjected = kotlin.math.abs(lonVal) > 1000000 || kotlin.math.abs(latVal) > 1000000
    val (displayLon, displayLat) = if (isProjected && !lonVal.isNaN() && !latVal.isNaN()) {
        transformProjectedToGeographic(lonVal, latVal)
    } else Pair(lonVal, latVal)

    val lonText = if (displayLon.isNaN()) "-" else "%.6f".format(displayLon)
    val latText = if (displayLat.isNaN()) "-" else "%.6f".format(displayLat)

    var quadrantName by remember(lonVal, latVal) { mutableStateOf<String?>(null) }
    var loading by remember(lonVal, latVal) { mutableStateOf(true) }

    LaunchedEffect(lonVal, latVal) {
        if (lonVal.isNaN() || latVal.isNaN()) {
            loading = false
            return@LaunchedEffect
        }
        try {
            val (px, py) = if (isProjected) {
                Pair(lonVal, latVal)
            } else {
                transformWgs84ToUtm29(lonVal, latVal)
            }

            val path = "/quadrants/findByCoordinates?lat=${py}&lon=${px}"
            val pair = withContext(Dispatchers.IO) { HttpClient.getFromHosts(path, ctx) }
            val body = pair.first
            if (body != null) {
                try {
                    val jo = org.json.JSONObject(body)
                    quadrantName = if (jo.has("nombre")) jo.optString("nombre") else null
                } catch (_: Exception) {
                    quadrantName = null
                }
            } else {
                quadrantName = null
            }
        } catch (_: Exception) {
            quadrantName = null
        } finally {
            loading = false
        }
    }

    if (loading) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.primary.copy(alpha = 0.04f),
            modifier = Modifier.padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.size(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = "...", style = MaterialTheme.typography.subtitle1)
                }
            }
        }
    } else {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.primary.copy(alpha = 0.08f),
            modifier = Modifier.padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(
                        text = quadrantName ?: "-",
                        style = MaterialTheme.typography.subtitle1
                    )
                }
                if (showCoordinates) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Text(text = "$lonText, $latText", style = MaterialTheme.typography.caption)
                }
            }
        }
    }
}
