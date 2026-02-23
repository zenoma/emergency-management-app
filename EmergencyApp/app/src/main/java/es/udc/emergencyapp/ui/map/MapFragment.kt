package es.udc.emergencyapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.udc.emergencyapp.databinding.FragmentMapBinding
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.sources.GeoJsonSource

class MapFragment : Fragment() {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var mapView: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        MapLibre.getInstance(requireContext())

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val view = binding.root
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)

        val lat = 42.6
        val lon = -8.0
        val zoom = 7.0

        val styleUrl = "https://api.maptiler.com/maps/topo-v2/style.json?key=3GSLdy5VE4yLq4OhlyYJ"

        mapView?.getMapAsync { map ->
            map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
                Thread {
                    val hostsToTry = listOf(
                        "http://10.0.2.2:8080",
                        "http://10.0.2.2:8000",
                        "http://10.0.3.2:8000",
                        "http://127.0.0.1:8000",
                        "http://192.168.1.100:8000"
                    )

                    val quadrantsJson = fetchQuadrantsJson(hostsToTry)
                    val jsonToUse = transformToWGS84GeoJson(quadrantsJson)

                    requireActivity().runOnUiThread {
                        try {
                            if (jsonToUse != null) {
                                android.util.Log.d(
                                    "MapFragment",
                                    "Fetched quadrants JSON length=${jsonToUse.length}"
                                )
                                addOrUpdateQuadrantsSource(style, jsonToUse)
                            } else {
                                android.util.Log.d(
                                    "MapFragment",
                                    "No quadrants to display (fetch returned null)"
                                )
                            }
                        } catch (e: Exception) {
                            android.util.Log.w(
                                "MapFragment",
                                "Failed to add/update quadrants source/layer",
                                e
                            )
                        }
                    }
                }.start()

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), zoom))
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart(); mapView?.onStart()
    }

    override fun onResume() {
        super.onResume(); mapView?.onResume()
    }

    override fun onPause() {
        super.onPause(); mapView?.onPause()
    }

    override fun onStop() {
        super.onStop(); mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory(); mapView?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView(); mapView?.onDestroy(); mapView = null; _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState); mapView?.onSaveInstanceState(outState)
    }

    private fun fetchQuadrantsJson(hosts: List<String>): String? {
        for (host in hosts) {
            try {
                val url = java.net.URL("$host/quadrants/active")
                val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 8000
                    readTimeout = 8000
                }
                val code = conn.responseCode
                val body = if (code == 200) conn.inputStream.bufferedReader().use { it.readText() } else null
                conn.disconnect()
                if (body != null) return body
            } catch (e: Exception) {
                android.util.Log.w("MapFragment", "Failed to fetch quadrants from $host", e)
            }
        }
        return null
    }

    private fun transformToWGS84GeoJson(raw: String?): String? {
        if (raw == null) return null
        return try {
            val trimmed = raw.trim()
            val ctFactory = org.locationtech.proj4j.CoordinateTransformFactory()
            val crsFactory = org.locationtech.proj4j.CRSFactory()
            val srcCRS = crsFactory.createFromParameters(
                null,
                "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs"
            )
            val tgtCRS = crsFactory.createFromName("EPSG:4326")
            val transform = ctFactory.createTransform(srcCRS, tgtCRS)

            fun transformPoint(x: Double, y: Double): org.json.JSONArray {
                val src = org.locationtech.proj4j.ProjCoordinate(x, y)
                val dst = org.locationtech.proj4j.ProjCoordinate()
                transform.transform(src, dst)
                val ja = org.json.JSONArray()
                ja.put(dst.x)
                ja.put(dst.y)
                return ja
            }

            if (trimmed.startsWith("[")) {
                val arr = org.json.JSONArray(raw)
                val features = org.json.JSONArray()
                for (i in 0 until arr.length()) {
                    val q = arr.getJSONObject(i)
                    val coordsArr = q.getJSONArray("coordinates")
                    val ring = org.json.JSONArray()
                    for (p in 0 until coordsArr.length()) {
                        val pt = coordsArr.getJSONObject(p)
                        val x = pt.getDouble("x")
                        val y = pt.getDouble("y")
                        ring.put(transformPoint(x, y))
                    }
                    val poly = org.json.JSONArray()
                    poly.put(ring)
                    val multipoly = org.json.JSONArray()
                    multipoly.put(poly)

                    val geom = org.json.JSONObject()
                    geom.put("type", "MultiPolygon")
                    geom.put("coordinates", multipoly)

                    val props = org.json.JSONObject()
                    props.put("id", q.optInt("id"))
                    props.put("nombre", q.optString("nombre"))

                    val feat = org.json.JSONObject()
                    feat.put("type", "Feature")
                    feat.put("properties", props)
                    feat.put("geometry", geom)
                    features.put(feat)
                }
                val fc = org.json.JSONObject()
                fc.put("type", "FeatureCollection")
                fc.put("features", features)
                fc.toString()
            } else {
                val obj = org.json.JSONObject(raw)
                if (obj.has("features")) obj.toString() else null
            }
        } catch (e: Exception) {
            android.util.Log.w("MapFragment", "Failed to transform quadrants geojson to WGS84", e)
            null
        }
    }

    private fun addOrUpdateQuadrantsSource(style: Style, jsonToUse: String) {
        val existing = style.getSourceAs<GeoJsonSource>("quadrants")
        if (existing != null) {
            try {
                existing.setGeoJson(jsonToUse)
                android.util.Log.d("MapFragment", "Updated existing quadrants source")
            } catch (e: Exception) {
                android.util.Log.w("MapFragment", "Failed to set geojson on existing source", e)
            }
        } else {
            style.addSource(GeoJsonSource("quadrants", jsonToUse))
            style.addLayer(
                FillLayer("quadrants-layer", "quadrants").withProperties(
                    fillColor("#D62B2B"),
                    fillOpacity(0.5f)
                )
            )
            try {
                style.addLayer(
                    org.maplibre.android.style.layers.LineLayer("quadrants-border", "quadrants").withProperties(
                        lineColor("#8B0000"),
                        lineWidth(2f)
                    )
                )
            } catch (e: Exception) {
                android.util.Log.w("MapFragment", "Could not add border layer", e)
            }
            android.util.Log.d("MapFragment", "Added new quadrants source and layer")
        }
    }
}
