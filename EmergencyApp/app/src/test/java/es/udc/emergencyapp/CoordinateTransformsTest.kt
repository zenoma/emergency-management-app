package es.udc.emergencyapp

import es.udc.emergencyapp.util.transformWgs84ToUtm29
import es.udc.emergencyapp.util.transformProjectedToGeographic
import org.junit.Test
import org.junit.Assert.*

class CoordinateTransformsTest {

    private val TOLERANCE_METERS = 500.0

    @Test
    fun wgs84ToUtm29_santiagoCompostela_returnsUtmZone29() {
        val (easting, northing) = transformWgs84ToUtm29(-8.544, 42.881)
        assertTrue("Expected easting ~536000, got $easting",
            easting in (530000.0 - TOLERANCE_METERS)..(540000.0 + TOLERANCE_METERS))
        assertTrue("Expected northing ~4748000, got $northing",
            northing in (4745000.0 - TOLERANCE_METERS)..(4750000.0 + TOLERANCE_METERS))
    }

    @Test
    fun wgs84ToUtm29_aCoruna_returnsReasonableUtm() {
        val (easting, northing) = transformWgs84ToUtm29(-8.406, 43.371)
        assertTrue("Easting $easting out of expected range for A Coruña",
            easting in 540000.0..560000.0)
        assertTrue("Northing $northing out of expected range for A Coruña",
            northing in 4800000.0..4810000.0)
    }

    @Test
    fun roundtrip_wgs84UtmWgs84_returnsWithinTolerance() {
        val originalLon = -7.86
        val originalLat = 42.24
        val (easting, northing) = transformWgs84ToUtm29(originalLon, originalLat)
        val (lonBack, latBack) = transformProjectedToGeographic(easting, northing)
        assertEquals(originalLon, lonBack, 0.001)
        assertEquals(originalLat, latBack, 0.001)
    }

    @Test
    fun wgs84ToUtm29_vigo_returnsReasonableUtm() {
        val (easting, northing) = transformWgs84ToUtm29(-8.72, 42.24)
        assertTrue("Easting $easting out of range for Vigo",
            easting in 520000.0..540000.0)
        assertTrue("Northing $northing out of range for Vigo",
            northing in 4675000.0..4685000.0)
    }

    @Test
    fun wgs84ToUtm29_ourense_returnsReasonableUtm() {
        val (easting, northing) = transformWgs84ToUtm29(-7.86, 42.34)
        assertTrue("Easting $easting out of range for Ourense",
            easting in 590000.0..610000.0)
        assertTrue("Northing $northing out of range for Ourense",
            northing in 4685000.0..4695000.0)
    }

    @Test
    fun roundtrip_multipleGalicianCities_returnsWithinTolerance() {
        val cities = listOf(
            Triple("Santiago", -8.544, 42.881),
            Triple("A Coruña", -8.406, 43.371),
            Triple("Vigo", -8.72, 42.24),
            Triple("Ourense", -7.86, 42.34),
            Triple("Lugo", -7.56, 43.01)
        )
        for ((name, lon, lat) in cities) {
            val (easting, northing) = transformWgs84ToUtm29(lon, lat)
            val (lonBack, latBack) = transformProjectedToGeographic(easting, northing)
            assertEquals("$name lon mismatch", lon, lonBack, 0.001)
            assertEquals("$name lat mismatch", lat, latBack, 0.001)
        }
    }
}
