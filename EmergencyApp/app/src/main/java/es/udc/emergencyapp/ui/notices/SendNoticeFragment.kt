package es.udc.emergencyapp.ui.notices

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import es.udc.emergencyapp.MainActivity
import es.udc.emergencyapp.R
import org.json.JSONObject
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateReferenceSystem
import org.locationtech.proj4j.CoordinateTransform
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class SendNoticeFragment : Fragment() {
    private var photoUri: Uri? = null
    private var lastLocation: Location? = null
    private var composeViewRef: ComposeView? = null

    // When permission is denied we show a specific text in the Compose UI.
    private var locationTextOverride: String? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (!success || photoUri == null) {
                Toast.makeText(requireContext(), "Failed to take photo", Toast.LENGTH_SHORT).show()
            } else {
                // re-render to show preview
                renderSendNotice()
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) startCameraCapture()
            else Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                .show()
        }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) fetchLocation()
            else Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT)
                .show()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())
        composeViewRef = composeView
        renderSendNotice()

        // request location
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation()
        } else {
            requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        return composeView
    }

    private fun onTakePhotoClicked() {
        // check camera permission at runtime
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startCameraCapture()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCameraCapture() {
        try {
            val resolver = requireContext().contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            Log.d("SendNotice", "Created photoUri=$photoUri")
            takePictureLauncher.launch(photoUri)
        } catch (e: Exception) {
            Log.w("SendNotice", "Failed to start camera capture", e)
            Toast.makeText(requireContext(), "Failed to start camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchLocation() {
        // Robust: check permission and query all enabled providers' last known location
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // We migrated to Compose; set an override string and re-render the ComposeView
                locationTextOverride = "Location: permission"
                renderSendNotice()
                return
            }

            val lm =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            var best: Location? = null
            try {
                val providers = lm.getProviders(true)
                Log.d("SendNotice", "Available providers: $providers")
                for (p in providers) {
                    try {
                        val l = lm.getLastKnownLocation(p)
                        if (l != null) {
                            if (best == null || l.time > best.time) best = l
                        }
                    } catch (se: SecurityException) {
                        Log.w("SendNotice", "No permission for provider $p", se)
                    } catch (ie: Exception) {
                        Log.w("SendNotice", "Failed reading provider $p", ie)
                    }
                }
            } catch (e: Exception) {
                Log.w("SendNotice", "Failed to iterate providers", e)
            }

            // fallback try specific providers
            if (best == null) {
                try {
                    val gps = try {
                        lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    } catch (e: Exception) {
                        null
                    }
                    val net = try {
                        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    } catch (e: Exception) {
                        null
                    }
                    best = listOfNotNull(gps, net).maxByOrNull { it.time }
                } catch (e: Exception) {
                    Log.w("SendNotice", "Fallback lastKnownLocation failed", e)
                }
            }

            if (best != null) {
                lastLocation = best
                // clear any previous override (permission message)
                locationTextOverride = null
            }
            // re-render UI with updated location
            renderSendNotice()
        } catch (e: Exception) {
            Log.w("SendNotice", "Failed to fetch location", e)
            renderSendNotice()
        }
    }

    private fun onSendClicked(body: String) {
        body.trim()
        if (body.isEmpty()) {
            Toast.makeText(requireContext(), "Enter a description", Toast.LENGTH_SHORT)
                .show(); return
        }
        if (lastLocation == null) {
            Toast.makeText(requireContext(), "Location unavailable", Toast.LENGTH_SHORT)
                .show(); return
        }
        // read photo bytes
        var photoBytes: ByteArray? = null
        try {
            if (photoUri != null) {
                val input: InputStream? =
                    requireContext().contentResolver.openInputStream(photoUri!!)
                photoBytes = input?.readBytes()
                input?.close()
            }
        } catch (e: Exception) {
            Log.w("SendNotice", "Failed to read photo", e)
        }

        var rawLon = 0.0
        var rawLat = 0.0
        var v1: Pair<Double, Double> = Pair(0.0, 0.0)
        try {
            rawLon = lastLocation!!.longitude
            rawLat = lastLocation!!.latitude
            v1 = transformWgs84ToUtm29(rawLon, rawLat)
        } catch (e: Exception) {
            Log.w("SendNotice", "Debug transform failed", e)
        }

        Thread {
            try {
                val hostsToTry = listOf(
                    "http://10.0.2.2:8080",
                    "http://10.0.2.2:8000",
                    "http://10.0.3.2:8000",
                    "http://127.0.0.1:8000",
                    "http://192.168.1.100:8000"
                )

                val (tLon, tLat) = v1
                val coords = JSONObject().apply {
                    put("lon", tLon)
                    put("lat", tLat)
                }

                val raw = JSONObject().apply {
                    put("lon", rawLon)
                    put("lat", rawLat)
                }

                val debugTransforms = JSONObject().apply {
                    val jv1 = JSONObject().apply { put("lon", v1.first); put("lat", v1.second) }
                    put("v1", jv1)
                }

                val payload = JSONObject().apply {
                    put("body", body)
                    put("coordinates", coords)
                    put("raw_wgs84", raw)
                    put("debug_transforms", debugTransforms)
                    if (photoBytes != null) {
                        put("photo", Base64.encodeToString(photoBytes, Base64.NO_WRAP))
                    }
                }
                val payloadStr = payload.toString()

                val jwtToken = try {
                    requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .getString("jwt_token", null)
                } catch (e: Exception) {
                    null
                }

                var lastEx: Exception? = null
                var success = false
                var successCode = -1
                var successResp = ""
                var usedHost: String? = null

                for (host in hostsToTry) {
                    try {
                        val url = URL("$host/notices")
                        val conn = (url.openConnection() as HttpURLConnection).apply {
                            requestMethod = "POST"
                            setRequestProperty("Content-Type", "application/json; charset=utf-8")
                            if (!jwtToken.isNullOrEmpty()) {
                                setRequestProperty("Authorization", "Bearer $jwtToken")
                                Log.d("SendNotice", "Attached JWT to request (masked)")
                            }
                            connectTimeout = 8000
                            readTimeout = 8000
                            doOutput = true
                        }
                        conn.outputStream.bufferedWriter().use { it.write(payloadStr) }
                        val code = conn.responseCode
                        val resp = if (code in 200..299) conn.inputStream.bufferedReader()
                            .use { it.readText() } else conn.errorStream?.bufferedReader()
                            ?.use { it.readText() } ?: ""
                        conn.disconnect()

                        success = true
                        successCode = code
                        successResp = resp
                        usedHost = host
                        break
                    } catch (e: Exception) {
                        lastEx = e
                        Log.w("SendNotice", "Failed to POST to $host", e)
                    }
                }

                if (!success) throw (lastEx ?: Exception("Unknown network error"))
                if (success && successCode in 200..299 && photoBytes != null) {
                    try {
                        var createdId: Long? = null
                        try {
                            val json = JSONObject(successResp)
                            if (json.has("id")) createdId = json.getLong("id")
                        } catch (e: Exception) {
                            Log.w("SendNotice", "Failed to parse create response JSON", e)
                        }
                        if (createdId != null && usedHost != null) {
                            val uploadUrl = URL("$usedHost/notices/$createdId/images")
                            val boundary = "----AndroidBoundary${System.currentTimeMillis()}"
                            val twoHyphens = "--"
                            val lineEnd = "\r\n"
                            val conn2 = (uploadUrl.openConnection() as HttpURLConnection).apply {
                                requestMethod = "POST"
                                setRequestProperty(
                                    "Content-Type",
                                    "multipart/form-data; boundary=$boundary"
                                )
                                if (!jwtToken.isNullOrEmpty()) setRequestProperty(
                                    "Authorization",
                                    "Bearer $jwtToken"
                                )
                                connectTimeout = 8000
                                readTimeout = 8000
                                doOutput = true
                            }
                            conn2.outputStream.use { out ->
                                out.write((twoHyphens + boundary + lineEnd).toByteArray())
                                out.write(("Content-Disposition: form-data; name=\"image\"; filename=\"photo.jpg\"" + lineEnd).toByteArray())
                                out.write(("Content-Type: image/jpeg" + lineEnd + lineEnd).toByteArray())
                                out.write(photoBytes)
                                out.write(lineEnd.toByteArray())
                                out.write((twoHyphens + boundary + twoHyphens + lineEnd).toByteArray())
                                out.flush()
                            }
                            val upCode = conn2.responseCode
                            val upResp = if (upCode in 200..299) conn2.inputStream.bufferedReader()
                                .use { it.readText() } else conn2.errorStream?.bufferedReader()
                                ?.use { it.readText() } ?: ""
                            conn2.disconnect()
                            Log.d("SendNotice", "Image upload result: code=$upCode resp=$upResp")
                        } else {
                            Log.w(
                                "SendNotice",
                                "Cannot upload image, missing created notice id or host"
                            )
                        }
                    } catch (e: Exception) {
                        Log.w("SendNotice", "Failed to upload image", e)
                    }
                }

                requireActivity().runOnUiThread {
                    if (successCode == 201) {
                        try {
                            AlertDialog.Builder(requireContext())
                                .setMessage(getString(R.string.notice_created))
                                .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                                    try {
                                        dialogInterface?.dismiss()
                                    } catch (e: Exception) { /* ignore */
                                    }
                                    try {
                                        (requireActivity() as? MainActivity)?.navigateToRoute("notices")
                                    } catch (e: Exception) {
                                        Log.w("SendNotice", "Navigation to My Notices failed", e)
                                    }
                                }
                                .setOnCancelListener { _ ->
                                    try {
                                        (requireActivity() as? MainActivity)?.navigateToRoute("notices")
                                    } catch (e: Exception) {
                                        Log.w("SendNotice", "Navigation to My Notices failed", e)
                                    }
                                }
                                .show()
                        } catch (e: Exception) {
                            Log.w("SendNotice", "Failed to show confirmation dialog", e)
                            Toast.makeText(
                                requireContext(),
                                "Notice sent (via $usedHost)",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else if (successCode in 200..299) {
                        Toast.makeText(
                            requireContext(),
                            "Notice sent (via $usedHost)",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to send: $successCode (via $usedHost)",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.w("SendNotice", "Exception sending notice", e)
                requireActivity().runOnUiThread {
                    val msg = e.localizedMessage ?: e.toString()
                    Toast.makeText(
                        requireContext(),
                        "Failed to send notice: $msg",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeViewRef = null
    }

    private fun transformWgs84ToUtm29(lon: Double, lat: Double): Pair<Double, Double> {
        return try {
            val ctFactory = CoordinateTransformFactory()
            val crsFactory = CRSFactory()
            val srcCRS: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")
            val tgtCRS: CoordinateReferenceSystem = crsFactory.createFromParameters(
                null,
                "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs"
            )
            val transform: CoordinateTransform = ctFactory.createTransform(srcCRS, tgtCRS)
            val srcCoord = ProjCoordinate(lon, lat)
            val dstCoord = ProjCoordinate()
            transform.transform(srcCoord, dstCoord)
            Pair(dstCoord.x, dstCoord.y)
        } catch (e: Exception) {
            Log.w("SendNotice", "Coordinate transform failed, returning WGS84", e)
            Pair(lon, lat)
        }
    }

    @Composable
    fun SendNoticeScreen(
        onTakePhoto: () -> Unit,
        onSend: (String) -> Unit,
        onRemovePhoto: () -> Unit,
        photoUri: Uri?,
        locationText: String
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(text = locationText)
                }
                IconButton(onClick = { onTakePhoto() }) {
                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Take photo")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val bodyState = remember { mutableStateOf("") }
            OutlinedTextField(
                value = bodyState.value,
                onValueChange = { bodyState.value = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Image preview area
            if (photoUri != null) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.height(220.dp)) {
                        AndroidView(
                            factory = { ctx ->
                                val iv = AppCompatImageView(ctx)
                                iv.scaleType = ImageView.ScaleType.CENTER_CROP
                                try {
                                    val prefsLocal =
                                        ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                    val jwtLocal = prefsLocal.getString("jwt_token", null)
                                    try {
                                        // Use direct Uri for content/file schemes; only wrap as GlideUrl for http(s)
                                        val scheme = photoUri?.scheme ?: ""
                                        val model: Any =
                                            if (scheme.startsWith("http") || scheme.startsWith("https")) {
                                                if (!jwtLocal.isNullOrEmpty()) {
                                                    val headers =
                                                        LazyHeaders.Builder()
                                                            .addHeader(
                                                                "Authorization",
                                                                "Bearer $jwtLocal"
                                                            )
                                                            .build()
                                                    GlideUrl(
                                                        photoUri.toString(),
                                                        headers
                                                    )
                                                } else photoUri.toString()
                                            } else {
                                                photoUri
                                            }
                                        Glide.with(ctx).load(model).centerCrop()
                                            .into(iv)
                                    } catch (e: Exception) {
                                        Log.w(
                                            "SendNotice",
                                            "Glide preview load failed, falling back",
                                            e
                                        )
                                        try {
                                            val `is` =
                                                ctx.contentResolver.openInputStream(photoUri!!)
                                            val bytes = `is`?.readBytes()
                                            `is`?.close()
                                            Log.d(
                                                "SendNotice",
                                                "Preview bytes size=${'$'}{bytes?.size}"
                                            )
                                            if (bytes != null && bytes.isNotEmpty()) {
                                                val bmp =
                                                    BitmapFactory.decodeByteArray(
                                                        bytes,
                                                        0,
                                                        bytes.size
                                                    )
                                                if (bmp != null) iv.setImageBitmap(bmp)
                                            }
                                        } catch (fe: Exception) {
                                            Log.w("SendNotice", "Fallback preview failed", fe)
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.w("SendNotice", "Glide preview failed", e)
                                }
                                iv
                            }, modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )
                        IconButton(
                            onClick = { onRemovePhoto() },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove photo",
                                tint = Color.Red
                            )
                        }
                    }
                }
            } else {
                // placeholder box
                Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    elevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Photo,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No photo selected", color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { onSend(bodyState.value) }) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Send")
                }
            }
        }
    }

    private fun renderSendNotice() {
        composeViewRef?.setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                SendNoticeScreen(
                    onTakePhoto = { onTakePhotoClicked() },
                    onSend = { onSendClicked(it) },
                    onRemovePhoto = {
                        photoUri = null
                        renderSendNotice()
                    },
                    photoUri = photoUri,
                    locationText = locationTextOverride
                        ?: lastLocation?.let { "Location: ${it.latitude}, ${it.longitude}" }
                        ?: "Location: unavailable"
                )
            }
        }
    }
}