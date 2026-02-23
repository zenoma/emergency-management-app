package es.udc.emergencyapp.ui.sendnotice

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
        val resolver = requireContext().contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        photoUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        takePictureLauncher.launch(photoUri)
    }

    private fun fetchLocation() {
        // lightweight: use last known location from fused provider if available
        try {
            val lm =
                requireContext().getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager
            val provider =
                if (lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) android.location.LocationManager.GPS_PROVIDER else android.location.LocationManager.NETWORK_PROVIDER
            val loc = lm.getLastKnownLocation(provider)
            if (loc != null) {
                lastLocation = loc
                binding.textLocation.text = "Location: ${loc.latitude}, ${loc.longitude}"
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

        // compute transformed variants and log them (helps detect axis/order issues)
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

        // send network request in background
        Thread {
            try {
                // Try common emulator/device host mappings. Login/profile use 10.0.2.2:8080 so try that first.
                val hostsToTry = listOf(
                    "http://10.0.2.2:8080",
                    "http://10.0.2.2:8000",
                    "http://10.0.3.2:8000",
                    "http://127.0.0.1:8000",
                    "http://192.168.1.100:8000"
                )

                // use previously computed transforms (v1) as the transformed coordinates
                val (tLon, tLat) = v1
                val coords = JSONObject().apply {
                    put("lon", tLon)
                    put("lat", tLat)
                }

                // include raw WGS84 and debug transforms to help server-side debugging
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

                // read jwt token from shared prefs if present
                val jwtToken = try {
                    requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE).getString("jwt_token", null)
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
                            // attach JWT if available
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
                        // try next host
                    }
                }

                if (!success) throw (lastEx ?: Exception("Unknown network error"))

                requireActivity().runOnUiThread {
                    if (successCode in 200..299) {
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

    // Helper to transform coordinates to match frontend proj4 usage
    // Frontend (frontend/src/app/utils/coordinatesTransformations.js) does:
    //   firstProjection = proj4("EPSG:4326")
    //   secondProjection = "+proj=utm +zone=29 +ellps=GRS80 ..."
    //   result = proj4(firstProjection, secondProjection).forward([longitude, latitude])
    // So we replicate that: source = EPSG:4326, target = same proj string, input order = (lon, lat)
    private fun transformWgs84ToUtm29(lon: Double, lat: Double): Pair<Double, Double> {
        return try {
            val ctFactory = CoordinateTransformFactory()
            val crsFactory = org.locationtech.proj4j.CRSFactory()
            // Source: EPSG:4326 (let proj4j resolve any internal axis ordering)
            val srcCRS: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")
            // Target: use the exact proj4 string used in frontend (GRS80, UTM zone 29)
            val tgtCRS: CoordinateReferenceSystem = crsFactory.createFromParameters(
                null,
                "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs"
            )
            val transform: CoordinateTransform = ctFactory.createTransform(srcCRS, tgtCRS)
            // IMPORTANT: frontend passes [longitude, latitude] -> we do the same
            val srcCoord = ProjCoordinate(lon, lat)
            val dstCoord = ProjCoordinate()
            transform.transform(srcCoord, dstCoord)
            Pair(dstCoord.x, dstCoord.y)
        } catch (e: Exception) {
            Log.w("SendNotice", "Coordinate transform failed, returning WGS84", e)
            Pair(lon, lat)
        }
    }

    // Alternative attempt: use EPSG:4326 by name and different target (try EPSG:25829 by code)
    private fun transformWgs84ToUtm29Alternative(lon: Double, lat: Double): Pair<Double, Double> {
        return try {
            val ctFactory = CoordinateTransformFactory()
            val crsFactory = org.locationtech.proj4j.CRSFactory()
            val srcCRS: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")
            val tgtCRS: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:25829")
            val transform: CoordinateTransform = ctFactory.createTransform(srcCRS, tgtCRS)
            val srcCoord = ProjCoordinate(lon, lat)
            val dstCoord = ProjCoordinate()
            transform.transform(srcCoord, dstCoord)
            Pair(dstCoord.x, dstCoord.y)
        } catch (e: Exception) {
            Log.w("SendNotice", "Alt coordinate transform failed", e)
            Pair(lon, lat)
        }
    }

    private fun formatPair(p: Pair<Double, Double>): String =
        String.format("%.3f,%.3f", p.first, p.second)
}
