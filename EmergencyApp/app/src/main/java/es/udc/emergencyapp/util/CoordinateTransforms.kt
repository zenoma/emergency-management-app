package es.udc.emergencyapp.util

import org.locationtech.proj4j.CoordinateTransformFactory
import org.locationtech.proj4j.CRSFactory
import org.locationtech.proj4j.ProjCoordinate

/**
 * Transform projected UTM29 coordinates (meters) to geographic WGS84 (lon, lat).
 * Returns the input pair if transform fails.
 */
fun transformProjectedToGeographic(x: Double, y: Double): Pair<Double, Double> {
    return try {
        val ctFactory = CoordinateTransformFactory()
        val crsFactory = CRSFactory()
        val srcCRS = crsFactory.createFromParameters(null, "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs")
        val tgtCRS = crsFactory.createFromName("EPSG:4326")
        val transform = ctFactory.createTransform(srcCRS, tgtCRS)
        val src = ProjCoordinate(x, y)
        val dst = ProjCoordinate()
        transform.transform(src, dst)
        Pair(dst.x, dst.y)
    } catch (e: Exception) {
        android.util.Log.w("CoordinateTransforms", "Failed to transform projected coords", e)
        Pair(x, y)
    }
}

/**
 * Transform geographic WGS84 (lon, lat) to projected UTM29 (meters) used by the backend (EPSG:25829).
 * Returns the input pair if transform fails.
 */
fun transformWgs84ToUtm29(lon: Double, lat: Double): Pair<Double, Double> {
    return try {
        val ctFactory = CoordinateTransformFactory()
        val crsFactory = CRSFactory()
        val srcCRS = crsFactory.createFromName("EPSG:4326")
        val tgtCRS = crsFactory.createFromParameters(null, "+proj=utm +zone=29 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs")
        val transform = ctFactory.createTransform(srcCRS, tgtCRS)
        val src = ProjCoordinate(lon, lat)
        val dst = ProjCoordinate()
        transform.transform(src, dst)
        Pair(dst.x, dst.y)
    } catch (e: Exception) {
        android.util.Log.w("CoordinateTransforms", "Failed to transform WGS84 to projected coords", e)
        Pair(lon, lat)
    }
}
