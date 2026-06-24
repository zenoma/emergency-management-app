package es.udc.emergencyapp.util

import kotlin.math.*

private const val A = 6378137.0          // WGS84 semimajor axis
private const val F = 1.0 / 298.257223563 // WGS84 flattening
private const val E2 = 2.0 * F - F * F   // squared eccentricity
private const val K0 = 0.9996            // UTM scale factor
private const val FE = 500000.0          // false easting
private const val FN_N = 0.0             // false northing (north)
private const val FN_S = 10000000.0      // false northing (south)
private const val ZONE_WIDTH = 6.0       // degrees per UTM zone
private const val ZONE_29_CENTRAL_MERIDIAN = -9.0 // zone 29 central meridian

/**
 * Transform geographic WGS84 (lon, lat) to projected UTM29 (meters, EPSG:25829).
 * Uses the standard UTM projection formulas (no proj4j dependency).
 * Only valid for zone 29 (lon -12° to -6°). Throws for out-of-range coordinates.
 */
fun transformWgs84ToUtm29(lon: Double, lat: Double): Pair<Double, Double> {
    return try {
        val zone = ((lon + 180.0) / 6.0).toInt() + 1
        if (zone != 29) {
            throw IllegalArgumentException("Lon=$lon is in UTM zone $zone, not zone 29 (Galicia)")
        }
        val latRad = Math.toRadians(lat)
        val lonRad = Math.toRadians(lon)
        val lon0Rad = Math.toRadians(ZONE_29_CENTRAL_MERIDIAN)

        val sinLat = sin(latRad)
        val cosLat = cos(latRad)
        val tanLat = sinLat / cosLat

        val N = A / sqrt(1.0 - E2 * sinLat * sinLat)
        val T = tanLat * tanLat
        val C = E2 / (1.0 - E2) * cosLat * cosLat
        val dLon = lonRad - lon0Rad
        val A_ = dLon * cosLat  // UTM uses A = dLon * cos(lat) in Taylor series

        // Meridional arc
        val e2 = E2
        val e4 = e2 * e2
        val e6 = e4 * e2
        val A0 = 1.0 - e2 / 4.0 - 3.0 * e4 / 64.0 - 5.0 * e6 / 256.0
        val A2 = 3.0 / 8.0 * (e2 + e4 / 4.0 + 15.0 * e6 / 128.0)
        val A4 = 15.0 / 256.0 * (e4 + 3.0 * e6 / 4.0)
        val A6 = 35.0 * e6 / 3072.0
        val m = A * (A0 * latRad - A2 * sin(2.0 * latRad) + A4 * sin(4.0 * latRad) - A6 * sin(6.0 * latRad))

        val easting = FE + K0 * N * (A_ + (1.0 - T + C) * A_.pow(3) / 6.0
            + (5.0 - 18.0 * T + T * T + 72.0 * C - 58.0 * e2 / (1.0 - E2)) * A_.pow(5) / 120.0)

        val northing = FN_N + K0 * (m + N * tanLat * (A_ * A_ / 2.0
            + (5.0 - T + 9.0 * C + 4.0 * C * C) * A_.pow(4) / 24.0
            + (61.0 - 58.0 * T + T * T + 600.0 * C - 330.0 * e2 / (1.0 - E2)) * A_.pow(6) / 720.0))

        Pair(easting, northing)
    } catch (e: Exception) {
        android.util.Log.w("CoordinateTransforms", "WGS84→UTM29 failed", e)
        Pair(lon, lat)
    }
}

/**
 * Transform UTM29 (EPSG:25829, meters) back to geographic WGS84 (lon, lat).
 * Uses standard UTM inverse formulas.
 */
fun transformProjectedToGeographic(easting: Double, northing: Double): Pair<Double, Double> {
    return try {
        val e1 = (1.0 - sqrt(1.0 - E2)) / (1.0 + sqrt(1.0 - E2))
        val e12 = e1 * e1
        val e13 = e12 * e1
        val e14 = e13 * e1

        val M = (northing - FN_N) / K0
        val mu = M / (A * (1.0 - E2 / 4.0 - 3.0 * E2 * E2 / 64.0 - 5.0 * E2 * E2 * E2 / 256.0))

        val phi1 = mu + (3.0 * e1 / 2.0 - 27.0 * e13 / 32.0) * sin(2.0 * mu)
            + (21.0 * e12 / 16.0 - 55.0 * e14 / 32.0) * sin(4.0 * mu)
            + (151.0 * e13 / 96.0) * sin(6.0 * mu)
            + (1097.0 * e14 / 512.0) * sin(8.0 * mu)

        val sinPhi1 = sin(phi1)
        val cosPhi1 = cos(phi1)
        val tanPhi1 = sinPhi1 / cosPhi1

        val N1 = A / sqrt(1.0 - E2 * sinPhi1 * sinPhi1)
        val T1 = tanPhi1 * tanPhi1
        val C1 = E2 / (1.0 - E2) * cosPhi1 * cosPhi1
        val R1 = A * (1.0 - E2) / (1.0 - E2 * sinPhi1 * sinPhi1).pow(1.5)
        val D = (easting - FE) / (K0 * N1)

        val latRad = phi1 - (N1 * tanPhi1 / R1) *
            (D * D / 2.0 - (5.0 + 3.0 * T1 + 10.0 * C1 - 4.0 * C1 * C1 - 9.0 * E2 / (1.0 - E2)) * D.pow(4) / 24.0
            + (61.0 + 90.0 * T1 + 298.0 * C1 + 45.0 * T1 * T1 - 252.0 * E2 / (1.0 - E2) - 3.0 * C1 * C1) * D.pow(6) / 720.0)

        val lonRad = Math.toRadians(ZONE_29_CENTRAL_MERIDIAN) +
            (D - (1.0 + 2.0 * T1 + C1) * D.pow(3) / 6.0
            + (5.0 - 2.0 * C1 + 28.0 * T1 - 3.0 * C1 * C1 + 8.0 * E2 / (1.0 - E2) + 24.0 * T1 * T1) * D.pow(5) / 120.0) / cosPhi1

        Pair(Math.toDegrees(lonRad), Math.toDegrees(latRad))
    } catch (e: Exception) {
        android.util.Log.w("CoordinateTransforms", "UTM29→WGS84 failed", e)
        Pair(easting, northing)
    }
}
