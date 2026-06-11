package es.udc.emergencyapp

import androidx.compose.ui.graphics.Color
import org.junit.Test
import org.junit.Assert.*

class AppThemeMappingTest {

    @Test
    fun statusColor_accepted_returnsGreen() {
        val c = statusColor("ACCEPTED")
        assertEquals(0.180f, c.red, 0.01f)
        assertEquals(0.490f, c.green, 0.01f)
        assertEquals(0.196f, c.blue, 0.01f)
        assertEquals(1.0f, c.alpha, 0.01f)
    }

    @Test
    fun statusColor_rejected_returnsRed() {
        val c = statusColor("REJECTED")
        assertEquals(0.957f, c.red, 0.01f)
        assertEquals(0.263f, c.green, 0.01f)
        assertEquals(0.212f, c.blue, 0.01f)
        assertEquals(1.0f, c.alpha, 0.01f)
    }

    @Test
    fun statusColor_completed_returnsGray() {
        val c = statusColor("COMPLETED")
        assertEquals(0.376f, c.red, 0.01f)
        assertEquals(0.490f, c.green, 0.01f)
        assertEquals(0.545f, c.blue, 0.01f)
        assertEquals(1.0f, c.alpha, 0.01f)
    }

    @Test
    fun statusColor_pending_returnsOrange() {
        val c = statusColor("PENDING")
        assertEquals(1.0f, c.red, 0.01f)
        assertEquals(0.596f, c.green, 0.01f)
        assertEquals(0.0f, c.blue, 0.01f)
        assertEquals(1.0f, c.alpha, 0.01f)
    }

    @Test
    fun statusColor_busy_returnsDarkRed() {
        val c = statusColor("BUSY")
        assertEquals(0.827f, c.red, 0.01f)
        assertEquals(0.184f, c.green, 0.01f)
        assertEquals(0.184f, c.blue, 0.01f)
        assertEquals(1.0f, c.alpha, 0.01f)
    }

    @Test
    fun statusColor_available_returnsGreen() {
        val c = statusColor("AVAILABLE")
        assertEquals(0.298f, c.red, 0.01f)
        assertEquals(0.686f, c.green, 0.01f)
        assertEquals(0.314f, c.blue, 0.01f)
        assertEquals(1.0f, c.alpha, 0.01f)
    }

    @Test
    fun statusColor_lowercaseAccepted_returnsGreen() {
        val c = statusColor("accepted")
        assertEquals(0.180f, c.red, 0.01f)
        assertEquals(0.490f, c.green, 0.01f)
        assertEquals(0.196f, c.blue, 0.01f)
    }

    @Test
    fun statusColor_unknownStatus_returnsGray() {
        val c = statusColor("UNKNOWN_STATUS")
        assertEquals(0.620f, c.red, 0.01f)
        assertEquals(0.620f, c.green, 0.01f)
        assertEquals(0.620f, c.blue, 0.01f)
    }

    @Test
    fun statusColor_null_returnsGray() {
        val c = statusColor(null)
        assertEquals(0.620f, c.red, 0.01f)
        assertEquals(0.620f, c.green, 0.01f)
        assertEquals(0.620f, c.blue, 0.01f)
    }

    @Test
    fun statusColor_emptyString_returnsGray() {
        val c = statusColor("")
        assertEquals(0.620f, c.red, 0.01f)
        assertEquals(0.620f, c.green, 0.01f)
        assertEquals(0.620f, c.blue, 0.01f)
    }

    @Test
    fun indexColor_resuelto_returnsGray() {
        val c = indexColor("RESUELTO")
        assertEquals(0.620f, c.red, 0.01f)
        assertEquals(0.620f, c.green, 0.01f)
        assertEquals(0.620f, c.blue, 0.01f)
    }

    @Test
    fun indexColor_cero_returnsGreen() {
        val c = indexColor("CERO")
        assertEquals(0.298f, c.red, 0.01f)
        assertEquals(0.686f, c.green, 0.01f)
        assertEquals(0.314f, c.blue, 0.01f)
    }

    @Test
    fun indexColor_uno_returnsYellow() {
        val c = indexColor("UNO")
        assertEquals(1.0f, c.red, 0.01f)
        assertEquals(0.757f, c.green, 0.01f)
        assertEquals(0.027f, c.blue, 0.01f)
    }

    @Test
    fun indexColor_dos_returnsOrange() {
        val c = indexColor("DOS")
        assertEquals(1.0f, c.red, 0.01f)
        assertEquals(0.596f, c.green, 0.01f)
        assertEquals(0.0f, c.blue, 0.01f)
    }

    @Test
    fun indexColor_tres_returnsRed() {
        val c = indexColor("TRES")
        assertEquals(0.957f, c.red, 0.01f)
        assertEquals(0.263f, c.green, 0.01f)
        assertEquals(0.212f, c.blue, 0.01f)
    }

    @Test
    fun indexColor_lowercaseDos_returnsOrange() {
        val c = indexColor("dos")
        assertEquals(1.0f, c.red, 0.01f)
        assertEquals(0.596f, c.green, 0.01f)
        assertEquals(0.0f, c.blue, 0.01f)
    }

    @Test
    fun indexColor_unknown_returnsBlue() {
        val c = indexColor("DESCONOCIDO")
        assertEquals(0.129f, c.red, 0.01f)
        assertEquals(0.588f, c.green, 0.01f)
        assertEquals(0.953f, c.blue, 0.01f)
    }

    @Test
    fun indexColor_null_returnsBlue() {
        val c = indexColor(null)
        assertEquals(0.129f, c.red, 0.01f)
        assertEquals(0.588f, c.green, 0.01f)
        assertEquals(0.953f, c.blue, 0.01f)
    }

    @Test
    fun indexColor_empty_returnsBlue() {
        val c = indexColor("")
        assertEquals(0.129f, c.red, 0.01f)
        assertEquals(0.588f, c.green, 0.01f)
        assertEquals(0.953f, c.blue, 0.01f)
    }
}
