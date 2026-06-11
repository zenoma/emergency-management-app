package es.udc.emergencyapp

import com.google.gson.Gson
import es.udc.emergencyapp.data.dto.CoordinatesDto
import es.udc.emergencyapp.data.dto.EmergencyDto
import es.udc.emergencyapp.data.dto.NoticeDto
import es.udc.emergencyapp.data.dto.OrganizationDto
import es.udc.emergencyapp.data.dto.OrganizationTypeDto
import es.udc.emergencyapp.data.dto.UserDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DtoDeserializationTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = Gson()
    }

    @Test
    fun emergencyDto_fullJson_deserializesCorrectly() {
        val json = """
            {
                "id": 1,
                "description": "Incendio forestal en Carballeda",
                "emergencyTypeName": "Fire",
                "emergencyIndex": "DOS",
                "createdAt": "2024-08-15T14:30:00Z",
                "resolvedAt": null,
                "location": { "lon": -8.5, "lat": 42.5 },
                "quadrantInfo": [
                    { "id": 10, "escala": "Alta", "nombre": "Q-Carballeda" }
                ]
            }
        """.trimIndent()
        val dto = gson.fromJson(json, EmergencyDto::class.java)

        assertEquals(1L, dto.id)
        assertEquals("Incendio forestal en Carballeda", dto.description)
        assertEquals("Fire", dto.emergencyTypeName)
        assertEquals("DOS", dto.emergencyIndex)
        assertNotNull(dto.createdAt)
        assertNull(dto.resolvedAt)
        assertNotNull(dto.location)
        dto.location!!.lon?.let { assertEquals(-8.5, it, 0.0) }
        dto.location.lat?.let { assertEquals(42.5, it, 0.0) }
        assertEquals(1, dto.quadrantInfo.size)
        assertEquals(10L, dto.quadrantInfo[0].id)
        assertEquals("Alta", dto.quadrantInfo[0].escala)
        assertEquals("Q-Carballeda", dto.quadrantInfo[0].nombre)
    }

    @Test
    fun emergencyDto_minimalJson_usesDefaults() {
        val json = """{ "id": 2, "description": "Minimal" }"""
        val dto = gson.fromJson(json, EmergencyDto::class.java)

        assertEquals(2L, dto.id)
        assertEquals("Minimal", dto.description)
        assertNull(dto.emergencyTypeName)
        assertNull(dto.emergencyIndex)
        assertNull(dto.createdAt)
        assertNull(dto.location)
        assertTrue(dto.quadrantInfo.isNullOrEmpty())
    }

    @Test
    fun noticeDto_fullJson_deserializesCorrectly() {
        val json = """
            {
                "id": 5,
                "body": "Avistamento de fume",
                "status": "PENDING",
                "createdAt": "2024-09-01T10:30:00",
                "quadrantName": "Q-Lugo",
                "quadrantId": 3,
                "userDto": {
                    "id": 1,
                    "firstName": "Ana",
                    "lastName": "García",
                    "email": "ana@example.com",
                    "phoneNumber": "600123456",
                    "dni": "12345678Z",
                    "userRole": "RESPONSIBLE"
                },
                "coordinates": { "lon": -7.55, "lat": 43.01 },
                "images": [
                    { "id": 10, "name": "foto1.jpg", "url": "http://example.com/foto1.jpg" }
                ]
            }
        """.trimIndent()
        val dto = gson.fromJson(json, NoticeDto::class.java)

        assertEquals(5L, dto.id)
        assertEquals("Avistamento de fume", dto.body)
        assertEquals("PENDING", dto.status)
        assertEquals("Q-Lugo", dto.quadrantName)
        assertEquals(3, dto.quadrantId)
        assertEquals("Ana", dto.userDto?.firstName)
        assertEquals(1, dto.images.size)
        assertEquals("foto1.jpg", dto.images[0].name)
        dto.coordinates!!.lon?.let { assertEquals(-7.55, it, 0.0) }
        dto.coordinates.lat?.let { assertEquals(43.01, it, 0.0) }
    }

    @Test
    fun noticeDto_minimalJson_usesDefaults() {
        val json = """{ "id": 6, "createdAt": "2024-01-01T00:00:00" }"""
        val dto = gson.fromJson(json, NoticeDto::class.java)

        assertEquals(6L, dto.id)
        assertNull(dto.body)
        assertNull(dto.status)
        assertNull(dto.quadrantName)
        assertNull(dto.quadrantId)
        assertNull(dto.userDto)
        assertNull(dto.coordinates)
        assertTrue(dto.images.isNullOrEmpty())
    }

    @Test
    fun organizationDto_fullJson_deserializesCorrectly() {
        val json = """
            {
                "id": 3,
                "code": "BRIG-001",
                "name": "Brigade de Lugo",
                "headquartersAddress": "Rúa do Concello, 10",
                "coordinates": { "lon": -7.55, "lat": 43.01 },
                "createdAt": "2023-06-15",
                "organizationTypeId": 2,
                "organizationTypeName": "Brigade"
            }
        """.trimIndent()
        val dto = gson.fromJson(json, OrganizationDto::class.java)

        assertEquals(3L, dto.id)
        assertEquals("BRIG-001", dto.code)
        assertEquals("Brigade de Lugo", dto.name)
        assertEquals("Rúa do Concello, 10", dto.headquartersAddress)
        dto.coordinates!!.lon?.let { assertEquals(-7.55, it, 0.0) }
        assertEquals(2L, dto.organizationTypeId)
        assertEquals("Brigade", dto.organizationTypeName)
    }

    @Test
    fun organizationTypeDto_deserializesCorrectly() {
        val json = """{ "id": 1, "name": "Firefighters" }"""
        val dto = gson.fromJson(json, OrganizationTypeDto::class.java)

        assertEquals(1L, dto.id)
        assertEquals("Firefighters", dto.name)
    }

    @Test
    fun nonExistentField_ignoredSilently() {
        val json = """{ "id": 99, "description": "Test", "extraField": "shouldBeIgnored" }"""
        val dto = gson.fromJson(json, EmergencyDto::class.java)

        assertEquals(99L, dto.id)
        assertEquals("Test", dto.description)
    }

    @Test
    fun coordinatesDto_nullValues_allowed() {
        val json = """{ "lon": null, "lat": null }"""
        val dto = gson.fromJson(json, CoordinatesDto::class.java)

        assertNull(dto.lon)
        assertNull(dto.lat)
    }

    @Test
    fun userDto_deserializesCorrectly() {
        val json = """
            {
                "id": 42,
                "firstName": "Manuel",
                "lastName": "Rodríguez",
                "email": "manuel@example.com",
                "phoneNumber": "987654321",
                "dni": "87654321B",
                "userRole": "USER"
            }
        """.trimIndent()
        val dto = gson.fromJson(json, UserDto::class.java)

        assertEquals(42L, dto.id)
        assertEquals("Manuel", dto.firstName)
        assertEquals("Rodríguez", dto.lastName)
        assertEquals("manuel@example.com", dto.email)
        assertEquals("987654321", dto.phoneNumber)
        assertEquals("87654321B", dto.dni)
        assertEquals("USER", dto.userRole)
    }
}
