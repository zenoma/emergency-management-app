package es.udc.emergencyapp

import es.udc.emergencyapp.util.emergencyTypeMapKey
import org.junit.Assert.assertEquals
import org.junit.Test

class EmergencyTypeIconTest {

    @Test
    fun mapKey_incendioForestal_returnsFire() {
        assertEquals("emergency-fire", emergencyTypeMapKey("Incendio forestal"))
    }

    @Test
    fun mapKey_fuego_returnsFire() {
        assertEquals("emergency-fire", emergencyTypeMapKey("Fuego"))
    }

    @Test
    fun mapKey_incendioMixedCase_returnsFire() {
        assertEquals("emergency-fire", emergencyTypeMapKey("INCENDIO"))
        assertEquals("emergency-fire", emergencyTypeMapKey("InCenDio"))
    }

    @Test
    fun mapKey_inundacion_returnsWater() {
        assertEquals("emergency-water", emergencyTypeMapKey("Inundación"))
    }

    @Test
    fun mapKey_agua_returnsWater() {
        assertEquals("emergency-water", emergencyTypeMapKey("Agua"))
    }

    @Test
    fun mapKey_flood_returnsWater() {
        assertEquals("emergency-water", emergencyTypeMapKey("Flood"))
    }

    @Test
    fun mapKey_derrumbe_returnsMontana() {
        assertEquals("emergency-montana", emergencyTypeMapKey("Derrumbe"))
    }

    @Test
    fun mapKey_desprendimiento_returnsMontana() {
        assertEquals("emergency-montana", emergencyTypeMapKey("Desprendimiento"))
    }

    @Test
    fun mapKey_accidente_returnsCar() {
        assertEquals("emergency-car", emergencyTypeMapKey("Accidente de tráfico"))
    }

    @Test
    fun mapKey_vial_returnsCar() {
        assertEquals("emergency-car", emergencyTypeMapKey("Vial"))
    }

    @Test
    fun mapKey_trafico_returnsCar() {
        assertEquals("emergency-car", emergencyTypeMapKey("trafic"))
    }

    @Test
    fun mapKey_sanitario_returnsMedical() {
        assertEquals("emergency-medical", emergencyTypeMapKey("Emergencia sanitaria"))
    }

    @Test
    fun mapKey_salud_returnsMedical() {
        assertEquals("emergency-medical", emergencyTypeMapKey("Salud pública"))
    }

    @Test
    fun mapKey_sanitaria_returnsMedical() {
        assertEquals("emergency-medical", emergencyTypeMapKey("Sanitaria"))
    }

    @Test
    fun mapKey_quimico_returnsChemical() {
        assertEquals("emergency-chemical", emergencyTypeMapKey("Riesgo químico"))
    }

    @Test
    fun mapKey_quimicoAccent_returnsChemical() {
        assertEquals("emergency-chemical", emergencyTypeMapKey("Químico"))
    }

    @Test
    fun mapKey_chemical_returnsChemical() {
        assertEquals("emergency-chemical", emergencyTypeMapKey("Chemical"))
    }

    @Test
    fun mapKey_industrial_returnsIndustrial() {
        assertEquals("emergency-industrial", emergencyTypeMapKey("Riesgo industrial"))
    }

    @Test
    fun mapKey_temporal_returnsStorm() {
        assertEquals("emergency-storm", emergencyTypeMapKey("Temporal"))
    }

    @Test
    fun mapKey_meteorologico_returnsStorm() {
        assertEquals("emergency-storm", emergencyTypeMapKey("Meteorológico"))
    }

    @Test
    fun mapKey_tormenta_returnsStorm() {
        assertEquals("emergency-storm", emergencyTypeMapKey("Tormenta"))
    }

    @Test
    fun mapKey_otros_returnsDefault() {
        assertEquals("emergency-default", emergencyTypeMapKey("Otros"))
    }

    @Test
    fun mapKey_otro_returnsDefault() {
        assertEquals("emergency-default", emergencyTypeMapKey("Otro tipo"))
    }

    @Test
    fun mapKey_null_returnsDefault() {
        assertEquals("emergency-default", emergencyTypeMapKey(null))
    }

    @Test
    fun mapKey_unknownType_returnsDefault() {
        assertEquals("emergency-default", emergencyTypeMapKey("Tipo desconocido"))
    }

    @Test
    fun mapKey_emptyString_returnsDefault() {
        assertEquals("emergency-default", emergencyTypeMapKey(""))
    }

    @Test
    fun mapKey_riesgoIndustrialMatchedBeforeGenericRiesgo() {
        assertEquals("emergency-industrial", emergencyTypeMapKey("riesgo industrial"))
    }

    @Test
    fun mapKey_containsSubstring_respectsFirstMatch() {
        assertEquals("emergency-fire", emergencyTypeMapKey("Incendio con accidente"))
    }
}
