package es.udc.emergencyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.udc.emergencyapp.net.HttpClient
import es.udc.emergencyapp.ui.setContentWithSystemBars
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentWithSystemBars {
            AppTheme {
                SignupScreen(onSignup = { firstName, lastName, email, password, dni, phone ->
                    performSignup(firstName, lastName, email, password, dni, phone)
                })
            }
        }
    }

    private fun performSignup(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        dni: String,
        phone: String
    ) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, getString(R.string.fill_credentials), Toast.LENGTH_SHORT).show()
            return
        }

        Thread {
            try {
                val payload = JSONObject().apply {
                    put("firstName", firstName)
                    put("lastName", lastName)
                    put("email", email)
                    put("password", password)
                    put("dni", dni)
                    runCatching {
                        put("phoneNumber", phone.toLong())
                    }.onFailure {
                        put("phoneNumber", phone)
                    }
                }.toString()

                android.util.Log.d("SignupActivityNet", "Signup payload=$payload")

                val pair = HttpClient.postToHosts("/users/signUp", this@SignupActivity, payload)
                val resp = pair.first

                android.util.Log.d("SignupActivityNet", "Signup response body=$resp")

                if (!resp.isNullOrEmpty()) {
                    val obj = try {
                        JSONObject(resp)
                    } catch (_: Exception) {
                        null
                    }
                    val token = obj?.optString("token", "")
                    val userObj = obj?.optJSONObject("user")

                    val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                    prefs.edit().apply {
                        if (token != null) putString("jwt_token", token)
                        if (userObj != null) {
                            val first = userObj.optString("firstName", firstName)
                            val last = userObj.optString("lastName", lastName)
                            putString("user_name", ("$first $last").trim())
                            putString("user_email", userObj.optString("email", email))
                            putString("user_phone", userObj.optString("phoneNumber", ""))
                            putString("user_dni", userObj.optString("dni", ""))
                            putString("user_role", userObj.optString("userRole", ""))
                        } else {
                            val combined = (firstName + " " + lastName).trim().ifBlank { email }
                            putString("user_name", combined)
                            putString("user_email", email)
                        }
                        apply()
                    }

                    runOnUiThread {
                        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } else {
                    android.util.Log.w("SignupActivityNet", "Signup failed or empty response")
                    runOnUiThread {
                        Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("SignupActivityNet", "Exception during signup", e)
                runOnUiThread {
                    Toast.makeText(this, getString(R.string.login_failed) + ": ${e.localizedMessage}", Toast.LENGTH_LONG)
                        .show()
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
private fun SignupScreen(onSignup: (String, String, String, String, String, String) -> Unit) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

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

                Text(text = stringResource(R.string.signup_title), style = MaterialTheme.typography.h6, fontSize = 20.sp)

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(text = context.getString(R.string.first_name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(text = context.getString(R.string.last_name)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dni,
                    onValueChange = { dni = it },
                    label = { Text(text = context.getString(R.string.dni)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(text = context.getString(R.string.phone)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSignup(
                            firstName.trim(),
                            lastName.trim(),
                            email.trim(),
                            password,
                            dni.trim(),
                            phone.trim()
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = context.getString(R.string.signup))
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = {}) {
                    Text(text = context.getString(R.string.login))
                }
            }
        }
    }
}
