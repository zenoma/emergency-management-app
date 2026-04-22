package es.udc.emergencyapp.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Utility to map emergency type names to an appropriate icon.
 * Use this across the Android app for consistent iconography.
 */
fun emergencyTypeIcon(typeName: String?): ImageVector {
    val n = typeName?.lowercase() ?: ""
    return when {
        n.contains("incend") || n.contains("fuego") -> Icons.Default.LocalFireDepartment
        n.contains("inund") || n.contains("agua") -> Icons.Default.Opacity
        n.contains("derrum") || n.contains("desprend") -> Icons.Default.Warning
        n.contains("accident") || n.contains("vial") || n.contains("trafic") -> Icons.Default.DirectionsCar
        n.contains("sanit") || n.contains("salud") || n.contains("sanitaria") -> Icons.Default.LocalHospital
        n.contains("quim") || n.contains("quím") -> Icons.Default.Science
        n.contains("industrial") || n.contains("riesgo industrial") -> Icons.Default.Warning
        n.contains("temporal") || n.contains("meteor") || n.contains("clima") -> Icons.Default.Cloud
        n.contains("otros") || n.contains("otro") -> Icons.Default.HelpOutline
        // fallback suggestions
        n.contains("riesgo") -> Icons.Default.Warning
        n.contains("incendio") -> Icons.Default.Whatshot
        else -> Icons.Default.Business
    }
}

// Backwards-compatible alias used in some modules
fun chooseIconForType(typeName: String?): ImageVector = emergencyTypeIcon(typeName)

/**
 * Return a canonical map icon key for the given emergency type.
 * Keys correspond to drawable names used in map assets (e.g. "emergency-fire").
 */
fun emergencyTypeMapKey(typeName: String?): String {
    val n = typeName?.lowercase() ?: ""
    return when {
        n.contains("incend") || n.contains("fuego") -> "emergency-fire"
        n.contains("inund") || n.contains("agua") || n.contains("flood") -> "emergency-water"
        n.contains("derrum") || n.contains("desprend") -> "emergency-montana"
        n.contains("accident") || n.contains("vial") || n.contains("trafic") -> "emergency-car"
        n.contains("sanit") || n.contains("salud") || n.contains("sanitaria") -> "emergency-medical"
        n.contains("quim") || n.contains("quím") || n.contains("chemical") -> "emergency-chemical"
        n.contains("industrial") || n.contains("riesgo industrial") -> "emergency-industrial"
        n.contains("temporal") || n.contains("meteor") || n.contains("torment") -> "emergency-storm"
        n.contains("otros") || n.contains("otro") -> "emergency-default"
        else -> "emergency-default"
    }
}
