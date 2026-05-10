package es.udc.emergencyapp.ui.myteam

data class TeamUserItem(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val role: String? = null,
    val phoneNumber: String? = null,
    val dni: String? = null
)

data class TeamSummary(
    val id: Long = -1L,
    val code: String = "",
    val orgName: String = "",
    val members: List<TeamUserItem> = emptyList()
)
