package es.udc.emergencyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val firstNameInput = findViewById<EditText>(R.id.input_first_name)
        val lastNameInput = findViewById<EditText>(R.id.input_last_name)
        val emailInput = findViewById<EditText>(R.id.input_email)
        val passwordInput = findViewById<EditText>(R.id.input_password)
        val dniInput = findViewById<EditText>(R.id.input_dni)
        val phoneInput = findViewById<EditText>(R.id.input_phone)
        val signupButton = findViewById<Button>(R.id.button_signup)

        signupButton.setOnClickListener {
            val firstName = firstNameInput.text.toString().trim().ifBlank { "" }
            val lastName = lastNameInput.text.toString().trim().ifBlank { "" }
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            val dni = dniInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Thread {
                try {
                    val url = URL("http://10.0.2.2:8080/users/signUp")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json; charset=utf-8")
                        doOutput = true
                        connectTimeout = 15000
                        readTimeout = 15000
                    }
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
                    conn.outputStream.bufferedWriter().use { it.write(payload) }

                    val code = conn.responseCode
                    val resp = try {
                        if (code in 200..299) conn.inputStream.bufferedReader()
                            .use { it.readText() }
                        else conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                    } catch (e: Exception) {
                        android.util.Log.w("SignupActivityNet", "Failed reading response stream", e)
                        ""
                    }
                    android.util.Log.d("SignupActivityNet", "Signup response code=$code body=$resp")

                    if (code == 200 || code == 201) {
                        val obj = try {
                            JSONObject(resp)
                        } catch (_: Exception) {
                            null
                        }
                        val token = obj?.optString("token", null)
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
                            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    } else {
                        android.util.Log.w(
                            "SignupActivityNet",
                            "Signup failed code=$code body=$resp"
                        )
                        runOnUiThread {
                            Toast.makeText(this, "Signup failed", Toast.LENGTH_LONG).show()
                        }
                    }
                    conn.disconnect()
                } catch (e: Exception) {
                    android.util.Log.w("SignupActivityNet", "Exception during signup", e)
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Signup failed: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }.start()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getPersistedLanguage(newBase)
        val ctx = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(ctx)
    }
}
