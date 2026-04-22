package es.udc.emergencyapp.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object DateUtils {
    // Public function to parse various server date representations and return a
    // localized, human-friendly string. Returns "-" when input is null or blank.
    fun formatForDisplay(dateStr: String?): String {
        if (dateStr.isNullOrBlank()) return "-"

        val outPattern = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.getDefault())

        // 1) Offset date-time with zone offset
        try {
            val odt = OffsetDateTime.parse(dateStr)
            return odt.format(outPattern)
        } catch (_: DateTimeParseException) {
        }

        // 2) Instant (Z)
        try {
            val instant = Instant.parse(dateStr)
            val zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
            return zdt.format(outPattern)
        } catch (_: DateTimeParseException) {
        }

        // 3) LocalDateTime
        try {
            val ldt = LocalDateTime.parse(dateStr)
            val zdt = ldt.atZone(ZoneId.systemDefault())
            return zdt.format(outPattern)
        } catch (_: DateTimeParseException) {
        }

        // 4) Common server patterns without zone
        val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS")
        for (p in patterns) {
            try {
                val fmt = DateTimeFormatter.ofPattern(p)
                val ldt = LocalDateTime.parse(dateStr, fmt)
                val zdt = ldt.atZone(ZoneId.systemDefault())
                return zdt.format(outPattern)
            } catch (_: Exception) {
            }
        }

        // Fallback: return original string
        return dateStr
    }
}
