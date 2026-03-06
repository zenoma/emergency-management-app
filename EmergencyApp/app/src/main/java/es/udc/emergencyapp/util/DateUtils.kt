package es.udc.emergencyapp.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    // Formato que devuelve el backend: "yyyy-MM-dd'T'HH:mm:ss" (sin zona)
    private val serverFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
    )

    private val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

    init {
        // Tratar las fechas del servidor como en UTC para evitar desfases si vienen sin zona.
        serverFormats.forEach { it.timeZone = TimeZone.getTimeZone("UTC") }
        outputFormat.timeZone = TimeZone.getDefault()
    }

    fun formatServerDate(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return ""
        for (fmt in serverFormats) {
            try {
                val d: Date = fmt.parse(dateStr)
                return outputFormat.format(d)
            } catch (ignored: Exception) {
            }
        }
        // fallback: return original if parsing fails
        return dateStr
    }
}
