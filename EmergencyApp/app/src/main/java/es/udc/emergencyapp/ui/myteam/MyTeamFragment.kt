package es.udc.emergencyapp.ui.myteam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import es.udc.emergencyapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun TeamUserRow(user: TeamUserItem) {
    val roleBg = colorResource(id = R.color.palette_secondary)
    val onRole = colorResource(id = R.color.on_secondary)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp), elevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = listOfNotNull(user.firstName, user.lastName).joinToString(" "),
                    fontWeight = FontWeight.Bold
                )
                // show email and phone/dni as subtitle
                Text(text = user.email ?: "", color = Color.Gray)
                if (!user.phoneNumber.isNullOrBlank() || !user.dni.isNullOrBlank()) {
                    Text(
                        text = listOfNotNull(user.phoneNumber, user.dni).joinToString(" • "),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            // role badge with icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = onRole,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = user.role ?: "",
                    color = onRole,
                    modifier = Modifier
                        .background(roleBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun TeamScreenComposable(
    teamCode: String,
    orgName: String,
    members: List<TeamUserItem>,
    loading: Boolean = false,
    error: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My team",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B3A66)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            backgroundColor = Color(0xFF0B3450),
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFFFFE0B2),
                    modifier = Modifier.size(64.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = teamCode.ifBlank { "-" },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Text(
                        text = orgName,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Badge, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(text = "${members.size} members", color = Color.White)
                    }
                }
                // status / resource type column
                Column(horizontalAlignment = Alignment.End) {
                    val statusColor = if ("AVAILABLE" == "AVAILABLE") Color(0xFF4CAF50) else Color(0xFFF44336)
                    Text(text = "TEAM", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "AVAILABLE", color = statusColor, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text(
            text = "Members",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0B3A66),
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (!error.isNullOrBlank()) {
            Text(text = error, color = Color.Red)
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(members) { u ->
                    TeamUserRow(u)
                }
            }
        }
    }
}

class MyTeamComposeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())
        composeView.setContent {
            var teamCode by remember { mutableStateOf("") }
            var orgName by remember { mutableStateOf("") }
            var members by remember { mutableStateOf(listOf<TeamUserItem>()) }

            var loading by remember { mutableStateOf(true) }
            var error by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                loading = true
                error = null
                try {
                    val prefs = requireContext().getSharedPreferences("app_prefs", 0)
                    val token = prefs.getString("jwt_token", null)
                    withContext(Dispatchers.IO) {
                        val url = URL("http://10.0.2.2:8080/teams/myTeam")
                        val conn = (url.openConnection() as HttpURLConnection).apply {
                            requestMethod = "GET"
                            connectTimeout = 5000
                            readTimeout = 5000
                            if (!token.isNullOrBlank()) setRequestProperty(
                                "Authorization",
                                "Bearer $token"
                            )
                        }
                        try {
                            val code = conn.responseCode
                            if (code == 200) {
                                val body = conn.inputStream.bufferedReader().use { it.readText() }
                                val arr = JSONArray(body)
                                if (arr.length() > 0) {
                                    val team = arr.getJSONObject(0)
                                    teamCode = team.optString("code", "")
                                    val org = team.optJSONObject("organization")
                                    orgName = org?.optString("name", "") ?: ""
                                    val users = team.optJSONArray("userList")
                                    val list = mutableListOf<TeamUserItem>()
                                    var teamStatus = team.optString("status", "")
                                    var resourceType = team.optString("resourceType", "")
                                    if (users != null) {
                                        for (i in 0 until users.length()) {
                                            val u = users.getJSONObject(i)
                                            list.add(
                                                TeamUserItem(
                                                    u.optString("firstName", null),
                                                    u.optString("lastName", null),
                                                    u.optString("email", null),
                                                    u.optString("userRole", null),
                                                    u.optString("phoneNumber", null),
                                                    u.optString("dni", null)
                                                )
                                            )
                                        }
                                    }
                                    members = list
                                    // update local vars if needed (we'll set via mutableState above)
                                    // teamStatus and resourceType are not stored in state currently; could be shown in card
                                }
                            } else if (code == 401) {
                                error = "Unauthorized"
                            } else {
                                error = "Failed to fetch team: HTTP $code"
                            }
                        } finally {
                            conn.disconnect()
                        }
                    }
                } catch (e: Exception) {
                    error = e.message ?: "Unknown error"
                } finally {
                    loading = false
                }
            }

            MaterialTheme {
                TeamScreenComposable(
                    teamCode = teamCode,
                    orgName = orgName,
                    members = members,
                    loading = loading,
                    error = error
                )
            }
        }
        return composeView
    }
}
