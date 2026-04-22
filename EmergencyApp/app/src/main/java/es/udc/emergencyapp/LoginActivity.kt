package es.udc.emergencyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import es.udc.emergencyapp.ui.setContentWithSystemBars
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentWithSystemBars {
            AppTheme {
                LoginScreen(
                    onLogin = { email, password -> performLogin(email, password) },
                    onSignup = { startActivity(Intent(this, SignupActivity::class.java)) }
                )
            }
        }
    }

    private fun performLogin(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.fill_credentials), Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            try {
                val url = URL("http://10.0.2.2:8080/users/login")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    doOutput = true
                    connectTimeout = 15000
                    readTimeout = 15000
                }
                val payload = JSONObject().apply {
                    put("userName", email)
                    put("email", email)
                    put("password", password)
                }.toString()
                Log.d("LoginActivityNet", "Sending login payload=$payload")
                conn.outputStream.bufferedWriter().use { it.write(payload) }

                val code = conn.responseCode
                val resp = try {
                    if (code in 200..299) conn.inputStream.bufferedReader().use { it.readText() }
                    else conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                } catch (e: Exception) {
                    Log.w("LoginActivityNet", "Failed reading response stream", e)
                    ""
                }

                Log.d("LoginActivityNet", "Login response code=$code body=$resp")

                if (code == 200) {
                    val obj = JSONObject(resp)
                    val token = obj.optString("token", null)
                    val userObj = obj.optJSONObject("user")

                    val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    prefs.edit().apply {
                        if (token != null) putString("jwt_token", token)
                        if (userObj != null) {
                            val first = userObj.optString("firstName", "")
                            val last = userObj.optString("lastName", "")
                            putString("user_name", (first + " " + last).trim())
                            putString("user_email", userObj.optString("email", ""))
                            putString("user_phone", userObj.optString("phoneNumber", ""))
                            putString("user_dni", userObj.optString("dni", ""))
                            putString("user_role", userObj.optString("userRole", ""))
                            val uid = try {
                                userObj.optLong("id", -1L)
                            } catch (e: Exception) {
                                -1L
                            }
                            if (uid > 0) putLong("user_id", uid)
                        }
                        apply()
                    }

                    runOnUiThread {
                        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG)
                            .show()
                    }
                    Log.w("LoginActivityNet", "Login failed code=$code body=$resp")
                }
                conn.disconnect()
            } catch (e: Exception) {
                Log.w("LoginActivityNet", "Exception during login", e)
                runOnUiThread {
                    Toast.makeText(
                        this,
                        getString(R.string.login_failed) + ": " + (e.localizedMessage ?: ""),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getPersistedLanguage(newBase)
        val ctx = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(ctx)
    }
}

@Composable
private fun LoginScreen(onLogin: (String, String) -> Unit, onSignup: () -> Unit) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colors.secondary,
                    modifier = Modifier.size(72.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Welcome", style = MaterialTheme.typography.h6, fontSize = 20.sp)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = context.getString(R.string.email)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = context.getString(R.string.password)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onLogin(email.trim(), password) },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.login))
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onSignup) {
                    Text(text = context.getString(R.string.signup))
                }
            }
        }
    }
}
