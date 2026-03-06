package es.udc.emergencyapp.data.dto

data class NoticeDto(
    val id: Long,
    val body: String?,
    val status: String?,
    val createdAt: String,
    val quadrantName: String? = null,
    val quadrantId: Int? = null,
    val userDto: UserDto? = null,
    val coordinates: CoordinatesDto? = null,
    val images: List<ImageDto> = emptyList()
)

data class ImageDto(val id: Long?, val name: String?, val url: String?)

data class UserDto(
    val id: Long,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String?,
    val dni: String?,
    val userRole: String?
)

data class CoordinatesDto(val lon: Double?, val lat: Double?)
