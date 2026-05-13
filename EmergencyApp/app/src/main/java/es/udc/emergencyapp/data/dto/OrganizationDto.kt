package es.udc.emergencyapp.data.dto

data class OrganizationTypeDto(
    val id: Long,
    val name: String
)

data class OrganizationDto(
    val id: Long,
    val code: String?,
    val name: String?,
    val headquartersAddress: String?,
    val coordinates: CoordinatesDto? = null,
    val createdAt: String? = null,
    val organizationTypeId: Long? = null,
    val organizationTypeName: String? = null
)
