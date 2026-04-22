package es.udc.emergencyapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Badge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.udc.emergencyapp.R

@Composable
fun ProfileScreen(
    name: String,
    email: String,
    dni: String,
    phone: String,
    role: String,
    currentLang: String,
    onLogout: () -> Unit,
    onChangeLanguage: (String) -> Unit
) {
    val context = LocalContext.current
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(elevation = 6.dp) {
                Column(
                    modifier = Modifier
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_1),
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (name.isNotBlank()) name else context.getString(R.string.profile),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (role.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.VerifiedUser, contentDescription = null)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(text = context.getString(R.string.profile_role_format, role))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Info rows (centered)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Filled.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onBackground
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = if (email.isNotBlank()) email else "-")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Filled.Phone,
                            contentDescription = null,
                            tint = MaterialTheme.colors.onBackground
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(text = if (phone.isNotBlank()) phone else "-")
                    }

                    if (dni.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Filled.Badge, contentDescription = null)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(text = context.getString(R.string.profile_dni_format, dni))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = context.getString(R.string.select_language),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                val alphaEs = if (currentLang == "es") 1f else 0.55f
                val alphaEn = if (currentLang == "en") 1f else 0.55f
                val alphaGl = if (currentLang == "gl") 1f else 0.55f

                Image(
                    painter = painterResource(id = R.drawable.flag_es),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onChangeLanguage("es") }
                        .alpha(alphaEs)
                        .padding(6.dp)
                )

                Spacer(modifier = Modifier.size(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.flag_en),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onChangeLanguage("en") }
                        .alpha(alphaEn)
                        .padding(6.dp)
                )

                Spacer(modifier = Modifier.size(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.flag_gl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clickable { onChangeLanguage("gl") }
                        .alpha(alphaGl)
                        .padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth(0.9f)) {
                Text(text = context.getString(R.string.logout))
            }
        }
    }
}
