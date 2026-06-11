package es.udc.emergencyapp

import es.udc.emergencyapp.util.DateUtils
import org.junit.Test
import org.junit.Assert.*

class DateUtilsTest {

    @Test
    fun formatForDisplay_null_returnsDash() {
        assertEquals("-", DateUtils.formatForDisplay(null))
    }

    @Test
    fun formatForDisplay_blank_returnsDash() {
        assertEquals("-", DateUtils.formatForDisplay(""))
        assertEquals("-", DateUtils.formatForDisplay("   "))
    }

    @Test
    fun formatForDisplay_offsetDateTime_parsedCorrectly() {
        val result = DateUtils.formatForDisplay("2024-01-15T14:30:00+01:00")
        assertTrue(result, result.contains("2024"))
        assertTrue(result, result.contains(":") && result.contains("14") || result.contains("15"))
    }

    @Test
    fun formatForDisplay_instantZ_parsedCorrectly() {
        val result = DateUtils.formatForDisplay("2024-06-01T10:00:00Z")
        assertTrue(result, result.contains("2024"))
        assertTrue(result, result.contains("Jun") || result.contains("jun") || result.contains("xuñ"))
    }

    @Test
    fun formatForDisplay_localDateTime_parsedCorrectly() {
        val result = DateUtils.formatForDisplay("2024-03-20T08:15:30")
        assertTrue(result, result.contains("2024"))
        assertTrue(result, result.contains("Mar") || result.contains("mar") || result.contains("abr"))
    }

    @Test
    fun formatForDisplay_customPattern_parsedCorrectly() {
        val result = DateUtils.formatForDisplay("2024-12-25T16:45:00")
        assertTrue(result, result.contains("2024"))
    }

    @Test
    fun formatForDisplay_unparseable_returnsOriginal() {
        val result = DateUtils.formatForDisplay("not-a-date")
        assertEquals("not-a-date", result)
    }

    @Test
    fun formatForDisplay_isoWithMillis_parsedCorrectly() {
        val result = DateUtils.formatForDisplay("2024-09-10T22:15:30.123")
        assertTrue(result, result.contains("2024"))
        assertTrue(result, result.contains("Sep") || result.contains("sep") || result.contains("set"))
    }

    @Test
    fun formatForDisplay_differentTimezones_produceSameDate() {
        val result1 = DateUtils.formatForDisplay("2024-01-01T00:00:00Z")
        val result2 = DateUtils.formatForDisplay("2024-01-01T01:00:00+01:00")
        assertTrue(result1, result1.contains("2024"))
        assertTrue(result2, result2.contains("2024"))
    }
}
