package es.udc.emergencyapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.udc.emergencyapp.R
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.PropertyFactory.*
import org.maplibre.android.style.sources.GeoJsonSource

class MapFragment : Fragment() {
    private var mapView: MapView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize MapLibre (try to support variants that require an apiKey)
        try { MapLibre.getInstance(requireContext()) } catch (_: Exception) {}

        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)

        val lat = 42.6
        val lon = -8.0
        val zoom = 7.0

        val styleUrl = "https://api.maptiler.com/maps/topo-v2/style.json?key=3GSLdy5VE4yLq4OhlyYJ"

        mapView?.getMapAsync { map ->
            map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
                val quadrantsJson = """
                    { "type": "FeatureCollection", "features": [
                      { "type": "Feature", "properties": {"id":1}, "geometry": { "type": "Polygon", "coordinates": [[[-9.5,42.9],[-9.5,42.0],[-7.5,42.0],[-7.5,42.9],[-9.5,42.9]]] } }
                    ] }
                """.trimIndent()

                val markersJson = """
                    { "type": "FeatureCollection", "features": [
                      { "type": "Feature", "properties": {"type":"team","name":"Team A"}, "geometry": { "type": "Point", "coordinates": [-8.1333,42.5758] } },
                      { "type": "Feature", "properties": {"type":"vehicle","name":"Vehicle 1"}, "geometry": { "type": "Point", "coordinates": [-8.5449,42.8806] } }
                    ] }
                """.trimIndent()

                style.addSource(GeoJsonSource("quadrants", quadrantsJson))
                style.addLayer(
                    FillLayer("quadrants-layer", "quadrants").withProperties(
                        fillColor("#ff7800"),
                        fillOpacity(0.15f)
                    )
                )

                style.addSource(GeoJsonSource("markers", markersJson))
                style.addLayer(
                    CircleLayer("markers-layer", "markers").withProperties(
                        circleColor("#3EB489"),
                        circleRadius(6f),
                        circleStrokeWidth(1f),
                        circleStrokeColor("#ffffff")
                    )
                )

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), zoom))
            }
        }

        return view
    }

    override fun onStart() { super.onStart(); mapView?.onStart() }
    override fun onResume() { super.onResume(); mapView?.onResume() }
    override fun onPause() { super.onPause(); mapView?.onPause() }
    override fun onStop() { super.onStop(); mapView?.onStop() }
    override fun onLowMemory() { super.onLowMemory(); mapView?.onLowMemory() }
    override fun onDestroyView() { super.onDestroyView(); mapView?.onDestroy(); mapView = null }
    override fun onSaveInstanceState(outState: Bundle) { super.onSaveInstanceState(outState); mapView?.onSaveInstanceState(outState) }
}
