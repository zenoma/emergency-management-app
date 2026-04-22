package es.udc.emergencyapp.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.ProjCoordinate
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory.fillColor
import org.maplibre.android.style.layers.PropertyFactory.fillOpacity
import org.maplibre.android.style.layers.PropertyFactory.iconAllowOverlap
import org.maplibre.android.style.layers.PropertyFactory.iconIgnorePlacement
import org.maplibre.android.style.layers.PropertyFactory.iconImage
import org.maplibre.android.style.layers.PropertyFactory.iconSize
import org.maplibre.android.style.layers.PropertyFactory.lineColor
import org.maplibre.android.style.layers.PropertyFactory.lineWidth
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource

/**
 * A Compose wrapper that hosts a MapView and reproduces the previous MapFragment behaviour.
 * This keeps the original map logic but exposes it as a composable so it can be used in NavHost.
 */
@Composable
fun MapScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    try {
        MapLibre.getInstance(context)
    } catch (_: Exception) {
    }

    val mapView = remember { MapView(context) }

    DisposableEffect(mapView) {
        try {
            mapView.onCreate(null)
        } catch (_: Exception) {
        }

        val lifecycleOwner = context as? LifecycleOwner
        val observer = lifecycleOwner?.let { owner ->
            val obs = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_START -> mapView.onStart()
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    Lifecycle.Event.ON_STOP -> mapView.onStop()
                    Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                    else -> {}
                }
            }
            owner.lifecycle.addObserver(obs)
            obs
        }

        onDispose {
            try {
                mapView.onDestroy()
            } catch (_: Exception) {
            }
            if (lifecycleOwner != null && observer != null) lifecycleOwner.lifecycle.removeObserver(
                observer
            )
        }
    }

    AndroidView(factory = { ctx ->
        val mv = mapView
        mv.getMapAsync { map ->
            try {
                val lat = 42.6
                val lon = -8.0
                val zoom = 7.0
                val styleUrl =
                    "https://api.maptiler.com/maps/topo-v2/style.json?key=3GSLdy5VE4yLq4OhlyYJ"
                map.setStyle(styleUrl) { style ->
                    Thread {
                        val hostsToTry = listOf(
                            "http://10.0.2.2:8080",
                            "http://10.0.2.2:8000",
                            "http://10.0.3.2:8000",
                            "http://127.0.0.1:8000",
                            "http://192.168.1.100:8000"
                        )

                        val prefs = ctx.getSharedPreferences(
                            "app_prefs",
                            Context.MODE_PRIVATE
                        )
                        val jwt = prefs.getString("jwt_token", null)
                        val quadrantsJson = es.udc.emergencyapp.net.HttpClient.getFromHosts("/quadrants/active", jwt, hostsToTry).first
                        val emergenciesJson = es.udc.emergencyapp.net.HttpClient.getFromHosts("/emergencies", jwt, hostsToTry).first
                        val jsonToUse = transformToWGS84GeoJson(quadrantsJson)

                        (ctx as? android.app.Activity)?.runOnUiThread {
                            try {
                                if (jsonToUse != null) addOrUpdateQuadrantsSource(style, jsonToUse)
                                try {
                                    if (!emergenciesJson.isNullOrBlank()) addOrUpdateEmergencyMarkers(
                                        map,
                                        emergenciesJson,
                                        ctx
                                    )
                                } catch (e: Exception) {
                                    android.util.Log.w(
                                        "MapScreen",
                                        "Failed to add emergency markers",
                                        e
                                    )
                                }
                            } catch (e: Exception) {
                                android.util.Log.w(
                                    "MapScreen",
                                    "Failed to add/update quadrants source/layer",
                                    e
                                )
                            }
                        }
                    }.start()

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), zoom))
                }
            } catch (e: Exception) {
                android.util.Log.w("MapScreen", "Map init failed", e)
            }
        }
        mv
    }, modifier = modifier)
}

// Network functions centralized in es.udc.emergencyapp.net.HttpClient

private fun chooseEmergencyIconName(typeKey: String?): String {
    val lowered = (typeKey ?: "").lowercase()
    val n = try {
        java.text.Normalizer.normalize(lowered, java.text.Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    } catch (e: Exception) {
        lowered
    }
    return when {
        n.contains("incend") || n.contains("fire") -> "emergency-fire"
        n.contains("inund") || n.contains("flood") || n.contains("water") || n.contains("inundacion") -> "emergency-water"
        n.contains("torment") || n.contains("temporal") || n.contains("storm") || n.contains("meteor") || n.contains(
            "evento"
        ) -> "emergency-storm"

        n.contains("derrum") || n.contains("desprend") || n.contains("land") || n.contains("mont") -> "emergency-montana"
        n.contains("accident") || n.contains("vial") || n.contains("car") -> "emergency-car"
        n.contains("sanit") || n.contains("salud") || n.contains("medical") -> "emergency-medical"
        n.contains("quim") || n.contains("chemical") -> "emergency-chemical"
        n.contains("industrial") || n.contains("factory") || n.contains("industr") -> "emergency-industrial"
        else -> "emergency-default"
    }
}

private fun bitmapFromDrawable(context: Context, resId: Int, sizePx: Int): Bitmap {
    val drawable = androidx.core.content.ContextCompat.getDrawable(context, resId)
        ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

private fun tintBitmapToColor(src: Bitmap, color: Int): Bitmap {
    val result = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val paint = Paint()
    paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(src, 0f, 0f, paint)
    return result
}

private fun ensureEmergencyImagesAndLayer(
    style: org.maplibre.android.maps.Style,
    context: Context
) {
    try {
        fun norm(name: String): String = name.replace(" ", "_").replace("-", "_").lowercase()

        fun findDrawableId(vararg candidates: String): Int {
            for (c in candidates) {
                val id = context.resources.getIdentifier(norm(c), "drawable", context.packageName)
                if (id != 0) return id
            }
            return 0
        }

        val mapping = mapOf(
            "emergency-fire" to arrayOf("incendio", "incendio_png", "incendio"),
            "emergency-water" to arrayOf("inundacion", "inundacion_png", "inundacion"),
            "emergency-default" to arrayOf("otros", "emergencia_otros"),
            "emergency-medical" to arrayOf(
                "emergencia_medica",
                "emergencia-medica",
                "emergencia_medica"
            ),
            "emergency-industrial" to arrayOf(
                "riesgo_industrial",
                "riesgo-industrial",
                "riesgo_industrial"
            ),
            "emergency-montana" to arrayOf("montana"),
            "emergency-chemical" to arrayOf(
                "quimical_hazard",
                "quimical hazard",
                "quimical_hazard"
            ),
            "emergency-storm" to arrayOf("tormenta"),
            "emergency-terrain" to arrayOf("montana"),
            "emergency-factory" to arrayOf("riesgo_industrial"),
            "emergency-science" to arrayOf("quimical_hazard"),
            "emergency-car" to arrayOf("coche"),
            "team-icon" to arrayOf("team_icon", "team-icon", "teamicon"),
            "vehicle-icon" to arrayOf("vehicle_icon", "vehicle-icon", "vehicleicon", "coche")
        )

        val markerPx = 32
        for ((key, candidates) in mapping) {
            try {
                val id = findDrawableId(*candidates)
                if (id != 0) {
                    val bmp = bitmapFromDrawable(context, id, markerPx)
                    val redBmp = try {
                        tintBitmapToColor(bmp, Color.RED)
                    } catch (t: Throwable) {
                        bmp
                    }
                    style.addImage(key, redBmp)
                } else {
                    android.util.Log.w(
                        "MapScreen",
                        "No drawable found for map icon '$key' (candidates=${candidates.joinToString()})"
                    )
                }
            } catch (ex: Exception) {
                android.util.Log.w("MapScreen", "Could not add image for $key", ex)
            }
        }

        try {
            val existing = style.getLayer("emergencies-layer")
            if (existing == null) {
                val symbolLayer = SymbolLayer("emergencies-layer", "emergencies").withProperties(
                    iconImage(Expression.get("icon")),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true),
                    iconSize(1.5f)
                )
                style.addLayer(symbolLayer)
            }
        } catch (ex: Exception) {
            android.util.Log.w("MapScreen", "Could not add emergencies symbol layer", ex)
        }
    } catch (e: Exception) {
        android.util.Log.w("MapScreen", "Failed to add emergency images/layer", e)
    }
}

private fun addOrUpdateEmergencyMarkers(
    map: org.maplibre.android.maps.MapLibreMap,
    emergenciesJson: String,
    ctx: Context
) {
    try {
        val style = map.style
        if (style != null) addOrUpdateEmergencySource(
            style,
            emergenciesJson,
            ctx
        ) else map.getStyle { s -> addOrUpdateEmergencySource(s, emergenciesJson, ctx) }
    } catch (e: Exception) {
        android.util.Log.w("MapScreen", "Failed to add/update emergency markers", e)
    }
}

private fun addOrUpdateEmergencySource(
    style: org.maplibre.android.maps.Style,
    emergenciesRaw: String,
    ctx: Context
) {
    try {
        ensureEmergencyImagesAndLayer(style, ctx)

        val arr = org.json.JSONArray(emergenciesRaw)
        val features = org.json.JSONArray()
        for (i in 0 until arr.length()) {
            try {
                val e = arr.getJSONObject(i)
                if ((e.has("resolvedAt") && !e.isNull("resolvedAt")) || e.optString("status", "")
                        .equals("resolved", ignoreCase = true)
                ) continue
                val loc = when {
                    e.has("location") -> e.getJSONObject("location")
                    e.has("point") -> e.getJSONObject("point")
                    else -> null
                }
                if (loc == null) continue
                val lon = if (loc.has("lon")) loc.optDouble(
                    "lon",
                    Double.NaN
                ) else if (loc.has("x")) loc.optDouble("x", Double.NaN) else Double.NaN
                val lat = if (loc.has("lat")) loc.optDouble(
                    "lat",
                    Double.NaN
                ) else if (loc.has("y")) loc.optDouble("y", Double.NaN) else Double.NaN
                if (lon.isNaN() || lat.isNaN()) continue

                val (finalLon, finalLat) = if (kotlin.math.abs(lon) > 1000000 || kotlin.math.abs(lat) > 1000000) {
                    val geo = transformProjectedToGeographic(lon, lat)
                    Pair(geo.first, geo.second)
                } else Pair(lon, lat)

                val typeKey =
                    if (e.has("emergencyTypeName")) e.optString("emergencyTypeName") else if (e.has(
                            "type"
                        )
                    ) e.optString("type") else ""
                val iconName = chooseEmergencyIconName(typeKey)

                val feature = org.json.JSONObject()
                feature.put("type", "Feature")
                val props = org.json.JSONObject()
                props.put("id", e.optInt("id", -1))
                props.put("title", e.optString("description", ""))
                props.put("icon", iconName)
                feature.put("properties", props)
                val geom = org.json.JSONObject()
                geom.put("type", "Point")
                val coords = org.json.JSONArray()
                coords.put(finalLon)
                coords.put(finalLat)
                geom.put("coordinates", coords)
                feature.put("geometry", geom)
                features.put(feature)
            } catch (ee: Exception) {
                android.util.Log.w("MapScreen", "Skipping malformed emergency entry", ee)
            }
        }
        val fc = org.json.JSONObject()
        fc.put("type", "FeatureCollection")
        fc.put("features", features)

        val existing = style.getSourceAs<GeoJsonSource>("emergencies")
        if (existing != null) {
            try {
                existing.setGeoJson(fc.toString())
            } catch (e: Exception) {
                android.util.Log.w(
                    "MapScreen",
                    "Failed to set emergencies geojson on existing source",
                    e
                )
            }
        } else style.addSource(GeoJsonSource("emergencies", fc.toString()))
    } catch (e: Exception) {
        android.util.Log.w("MapScreen", "Failed to build emergency source", e)
    }
}

private fun transformProjectedToGeographic(x: Double, y: Double): Pair<Double, Double> {
    return try {
        val ctFactory = CoordinateTransformFactory()
        val crsFactory = CRSFactory()
        val srcCRS = crsFactory.createFromParameters(
            null,
            "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs"
        )
        val tgtCRS = crsFactory.createFromName("EPSG:4326")
        val transform = ctFactory.createTransform(srcCRS, tgtCRS)
        val src = ProjCoordinate(x, y)
        val dst = ProjCoordinate()
        transform.transform(src, dst)
        Pair(dst.x, dst.y)
    } catch (e: Exception) {
        android.util.Log.w("MapScreen", "Failed to transform projected emergency coords", e)
        Pair(x, y)
    }
}

private fun transformToWGS84GeoJson(raw: String?): String? {
    if (raw == null) return null
    return try {
        val trimmed = raw.trim()
        val ctFactory = CoordinateTransformFactory()
        val crsFactory = CRSFactory()
        val srcCRS = crsFactory.createFromParameters(
            null,
            "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs"
        )
        val tgtCRS = crsFactory.createFromName("EPSG:4326")
        val transform = ctFactory.createTransform(srcCRS, tgtCRS)

        fun transformPoint(x: Double, y: Double): org.json.JSONArray {
            val src = ProjCoordinate(x, y)
            val dst = ProjCoordinate()
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
                val poly = org.json.JSONArray(); poly.put(ring)
                val multipoly = org.json.JSONArray(); multipoly.put(poly)
                val geom = org.json.JSONObject(); geom.put(
                    "type",
                    "MultiPolygon"
                ); geom.put("coordinates", multipoly)
                val props = org.json.JSONObject(); props.put(
                    "id",
                    q.optInt("id")
                ); props.put("nombre", q.optString("nombre"))
                val feat = org.json.JSONObject(); feat.put(
                    "type",
                    "Feature"
                ); feat.put("properties", props); feat.put("geometry", geom)
                features.put(feat)
            }
            val fc = org.json.JSONObject(); fc.put("type", "FeatureCollection"); fc.put(
                "features",
                features
            ); fc.toString()
        } else {
            val obj = org.json.JSONObject(raw)
            if (obj.has("features")) obj.toString() else null
        }
    } catch (e: Exception) {
        android.util.Log.w("MapScreen", "Failed to transform quadrants geojson to WGS84", e)
        null
    }
}

private fun addOrUpdateQuadrantsSource(style: org.maplibre.android.maps.Style, jsonToUse: String) {
    val existing = style.getSourceAs<GeoJsonSource>("quadrants")
    if (existing != null) {
        try {
            existing.setGeoJson(jsonToUse); android.util.Log.d(
                "MapScreen",
                "Updated existing quadrants source"
            )
        } catch (e: Exception) {
            android.util.Log.w("MapScreen", "Failed to set geojson on existing source", e)
        }
    } else {
        style.addSource(GeoJsonSource("quadrants", jsonToUse))
        style.addLayer(
            FillLayer(
                "quadrants-layer",
                "quadrants"
            ).withProperties(fillColor("#D62B2B"), fillOpacity(0.5f))
        )
        try {
            style.addLayer(
                LineLayer(
                    "quadrants-border",
                    "quadrants"
                ).withProperties(lineColor("#8B0000"), lineWidth(2f))
            )
        } catch (e: Exception) {
            android.util.Log.w("MapScreen", "Could not add border layer", e)
        }
        android.util.Log.d("MapScreen", "Added new quadrants source and layer")
    }
}
