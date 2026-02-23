package es.udc.emergencyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInput = findViewById<EditText>(R.id.input_email)
        val passwordInput = findViewById<EditText>(R.id.input_password)
        val loginButton = findViewById<Button>(R.id.button_login)
        val signupLink = findViewById<TextView>(R.id.link_signup)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.fill_credentials), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Perform login on background thread
            Thread {
                try {
                    val url = URL("http://10.0.2.2:8080/users/login")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json; charset=utf-8")
                        doOutput = true
                        // increase timeouts to tolerate slower backends
                        connectTimeout = 15000
                        readTimeout = 15000
                    }
                    val payload = JSONObject().apply {
                        // Some backends expect 'userName' instead of 'email' as the login field.
                        put("userName", email)
                        put("email", email)
                        put("password", password)
                    }.toString()
                    android.util.Log.d("LoginActivityNet", "Sending login payload=$payload")
                    conn.outputStream.bufferedWriter().use { it.write(payload) }

                    val code = conn.responseCode
                    val resp = try {
                        if (code in 200..299) conn.inputStream.bufferedReader().use { it.readText() }
                        else conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                    } catch (e: Exception) {
                        android.util.Log.w("LoginActivityNet", "Failed reading response stream", e)
                        ""
                    }

                    android.util.Log.d("LoginActivityNet", "Login response code=$code body=$resp")

                    if (code == 200) {
                        val obj = JSONObject(resp)
                        val token = obj.optString("token", null)
                        val userObj = obj.optJSONObject("user")

                        // Persist token + user data
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
                            }
                            apply()
                        }

                        // Navigate to MainActivity on UI thread
                        runOnUiThread {
                            Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
                        }
                        android.util.Log.w("LoginActivityNet", "Login failed code=$code body=$resp")
                    }
                    conn.disconnect()
                } catch (e: Exception) {
                    android.util.Log.w("LoginActivityNet", "Exception during login", e)
                    runOnUiThread {
                        Toast.makeText(this, getString(R.string.login_failed) + ": " + (e.localizedMessage ?: ""), Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }

        signupLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getPersistedLanguage(newBase)
        val ctx = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(ctx)
    }
}
