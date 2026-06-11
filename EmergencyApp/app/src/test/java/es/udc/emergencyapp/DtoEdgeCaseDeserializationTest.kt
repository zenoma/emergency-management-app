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

class DtoEdgeCaseDeserializationTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = Gson()
    }

    @Test
    fun emergencyDto_withNullFields_doesNotThrow() {
        val json = """{"id":1}"""
        val dto = gson.fromJson(json, EmergencyDto::class.java)
        assertEquals(1L, dto.id)
        assertNull(dto.description)
        assertNull(dto.emergencyTypeName)
        assertNull(dto.emergencyIndex)
        assertNull(dto.createdAt)
    }

    @Test
    fun emergencyDto_emptyQuadrantInfo_parsesAsNull() {
        val json = """{"id":1,"description":"Test","quadrantInfo":[]}"""
        val dto = gson.fromJson(json, EmergencyDto::class.java)
        assertEquals(1L, dto.id)
        assertNotNull(dto.quadrantInfo)
        assertTrue(dto.quadrantInfo!!.isEmpty())
    }

    @Test
    fun emergencyDto_withUnknownFields_ignoredSilently() {
        val json =
            """{"id":1,"description":"Test","unknownField":"shouldNotBreak","extraObj":{"a":1}}"""
        val dto = gson.fromJson(json, EmergencyDto::class.java)
        assertEquals(1L, dto.id)
        assertEquals("Test", dto.description)
    }

    @Test
    fun noticeDto_withEmptyImages_parsesAsEmpty() {
        val json = """{"id":1,"createdAt":"2024-01-01T00:00:00","images":[]}"""
        val dto = gson.fromJson(json, NoticeDto::class.java)
        assertNotNull(dto.images)
        assertTrue(dto.images!!.isEmpty())
    }

    @Test
    fun noticeDto_withNullImages_explicitlySet_parsesAsNull() {
        val json = """{"id":1,"createdAt":"2024-01-01T00:00:00","images":null}"""
        val dto = gson.fromJson(json, NoticeDto::class.java)
        assertNull(dto.images)
    }

    @Test
    fun coordinatesDto_withNullCoords_returnsNullLonLat() {
        val json = """{"lon":null,"lat":null}"""
        val dto = gson.fromJson(json, CoordinatesDto::class.java)
        assertNull(dto.lon)
        assertNull(dto.lat)
    }


    @Test
    fun userDto_withNullFields_doesNotThrow() {
        val json = """{"id":1}"""
        val dto = gson.fromJson(json, UserDto::class.java)
        assertEquals(1L, dto.id)
        assertNull(dto.firstName)
        assertNull(dto.email)
    }

    @Test
    fun organizationDto_withMissingCoordinates_doesNotThrow() {
        val json = """{"id":1,"code":"ORG1"}"""
        val dto = gson.fromJson(json, OrganizationDto::class.java)
        assertEquals(1L, dto.id)
        assertEquals("ORG1", dto.code)
        assertNull(dto.coordinates)
    }

    @Test
    fun organizationDto_withNullType_returnsNullTypeId() {
        val json = """{"id":1,"code":"ORG1","organizationTypeId":null}"""
        val dto = gson.fromJson(json, OrganizationDto::class.java)
        assertNull(dto.organizationTypeId)
    }

    @Test
    fun emergencyTypeDto_invalidJson_returnsNullWithTryCatch() {
        val json = """not valid json"""
        val dto = try {
            gson.fromJson(json, OrganizationTypeDto::class.java)
        } catch (e: Exception) {
            null
        }
        assertNull(dto)
    }

    @Test
    fun emergencyDto_withSpecialCharacters_parsedCorrectly() {
        val json = """{"id":1,"description":"Emerxencia con acentos: ñ, é, í, ó, ú"}"""
        val dto = gson.fromJson(json, EmergencyDto::class.java)
        assertEquals("Emerxencia con acentos: ñ, é, í, ó, ú", dto.description)
    }

    @Test
    fun emergencyDto_withCoordinatesButNoQuadrant_parsedCorrectly() {
        val json = """{"id":1,"description":"Test","location":{"lon":-8.5,"lat":42.5}}"""
        val dto = gson.fromJson(json, EmergencyDto::class.java)
        dto.location!!.lon?.let { assertEquals(-8.5, it, 0.0) }
        dto.location!!.lat?.let { assertEquals(42.5, it, 0.0) }
        assertTrue(dto.quadrantInfo.isNullOrEmpty())
    }

    @Test
    fun noticeDto_withOnlyRequiredFields_parsedCorrectly() {
        val json = """{"id":1,"createdAt":"2024-01-01T00:00:00"}"""
        val dto = gson.fromJson(json, NoticeDto::class.java)
        assertEquals(1L, dto.id)
        assertEquals("2024-01-01T00:00:00", dto.createdAt)
        assertNull(dto.body)
        assertNull(dto.userDto)
        assertNull(dto.coordinates)
    }
}
