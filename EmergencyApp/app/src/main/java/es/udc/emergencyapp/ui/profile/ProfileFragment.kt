package es.udc.emergencyapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import es.udc.emergencyapp.LocaleHelper
import es.udc.emergencyapp.R


class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val logoutButton = view.findViewById<Button>(R.id.button_logout)
        logoutButton.setOnClickListener {
            // Mock logout: go back to LoginActivity and clear task
            val intent = Intent(requireContext(), es.udc.emergencyapp.LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        // Load persisted user info (from signup) and populate profile fields
        val prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", null)
        val email = prefs.getString("user_email", null)
        val nameView = view.findViewById<android.widget.TextView>(R.id.profile_name)
        val emailView = view.findViewById<android.widget.TextView>(R.id.profile_email)
        if (!name.isNullOrBlank()) nameView.text = name
        if (!email.isNullOrBlank()) emailView.text = email

        // Language selector buttons (ImageButtons in layout)
        val btnEs = view.findViewById<android.widget.ImageButton>(R.id.button_lang_es)
        val btnEn = view.findViewById<android.widget.ImageButton>(R.id.button_lang_en)
        val btnGl = view.findViewById<android.widget.ImageButton>(R.id.button_lang_gl)

        // Visualize currently selected language by adjusting alpha
        fun updateLangSelection(selected: String) {
            btnEs.alpha = if (selected == "es") 1f else 0.55f
            btnEn.alpha = if (selected == "en") 1f else 0.55f
            btnGl.alpha = if (selected == "gl") 1f else 0.55f
        }

        val current = LocaleHelper.getPersistedLanguage(requireContext())
        updateLangSelection(current)

        btnEs.setOnClickListener { changeLanguage("es") }
        btnEn.setOnClickListener { changeLanguage("en") }
        btnGl.setOnClickListener { changeLanguage("gl") }

        return view
    }

    private fun changeLanguage(lang: String) {
        LocaleHelper.persistLanguage(requireContext(), lang)
        val ctx = LocaleHelper.setLocale(requireContext(), lang)
        // recreate activity to apply language
        requireActivity().recreate()
    }
}
