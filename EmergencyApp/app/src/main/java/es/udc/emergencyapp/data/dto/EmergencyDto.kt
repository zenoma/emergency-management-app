package es.udc.emergencyapp.data.dto

data class EmergencyDto(
    val id: Long,
    val description: String?,
    val emergencyTypeName: String?,
    val emergencyIndex: String?,
    val createdAt: String?,
    val resolvedAt: String? = null,
    val location: CoordinatesDto? = null,
    val quadrantInfo: List<QuadrantInfoDto> = emptyList()
)

data class QuadrantInfoDto(
    val id: Long,
    val escala: String?,
    val nombre: String?
)
