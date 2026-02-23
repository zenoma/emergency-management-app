package es.udc.emergencyapp.ui.sendnotice

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import es.udc.emergencyapp.R
import es.udc.emergencyapp.databinding.FragmentSendNoticeBinding
import org.json.JSONObject
import org.locationtech.proj4j.CoordinateReferenceSystem
import org.locationtech.proj4j.CoordinateTransform
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class SendNoticeFragment : Fragment() {
    private var _binding: FragmentSendNoticeBinding? = null
    private val binding get() = _binding!!

    private var photoUri: Uri? = null
    private var lastLocation: Location? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri != null) {
                binding.imagePreview.setImageURI(photoUri)
            } else {
                Toast.makeText(requireContext(), "Failed to take photo", Toast.LENGTH_SHORT).show()
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
        _binding = FragmentSendNoticeBinding.inflate(inflater, container, false)

        binding.buttonTakePhoto.setOnClickListener { onTakePhotoClicked() }
        binding.buttonSend.setOnClickListener { onSendClicked() }

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

        return binding.root
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
                binding.textLocation.text = "Location: permission"
                return
            }

            val lm =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
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
                        lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                    } catch (e: Exception) {
                        null
                    }
                    val net = try {
                        lm.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
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
                binding.textLocation.text = "Location: ${best.latitude}, ${best.longitude}"
            } else {
                binding.textLocation.text = "Location: unavailable"
            }
        } catch (e: Exception) {
            Log.w("SendNotice", "Failed to fetch location", e)
            binding.textLocation.text = "Location: error"
        }
    }

    private fun onSendClicked() {
        val body = binding.inputBody.text.toString().trim()
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
                                        (requireActivity() as? es.udc.emergencyapp.MainActivity)?.navigateToDrawerItem(
                                            R.id.nav_my_notices
                                        )
                                    } catch (e: Exception) {
                                        Log.w("SendNotice", "Navigation to My Notices failed", e)
                                    }
                                }
                                .setOnCancelListener { _ ->
                                    try {
                                        (requireActivity() as? es.udc.emergencyapp.MainActivity)?.navigateToDrawerItem(
                                            R.id.nav_my_notices
                                        )
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
        _binding = null
    }

    private fun transformWgs84ToUtm29(lon: Double, lat: Double): Pair<Double, Double> {
        return try {
            val ctFactory = CoordinateTransformFactory()
            val crsFactory = org.locationtech.proj4j.CRSFactory()
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
}
