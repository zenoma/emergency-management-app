package es.udc.emergencyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val nameInput = findViewById<EditText>(R.id.input_name)
        val emailInput = findViewById<EditText>(R.id.input_email)
        val passwordInput = findViewById<EditText>(R.id.input_password)
        val signupButton = findViewById<Button>(R.id.button_signup)

        signupButton.setOnClickListener {
            // Persist entered name/email so Profile can show them, then proceed to MainActivity
            val name = nameInput.text.toString().ifBlank { "User" }
            val email = emailInput.text.toString().ifBlank { "user@example.com" }
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("user_name", name)
                .putString("user_email", email)
                .apply()

            Toast.makeText(this, "Signup successful (mock)", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getPersistedLanguage(newBase)
        val ctx = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(ctx)
    }
}
