package es.udc.emergencyapp.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.udc.emergencyapp.LocaleHelper
import es.udc.emergencyapp.MainActivity
import es.udc.emergencyapp.R
import es.udc.emergencyapp.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.buttonLogout.setOnClickListener {
            val prefs = requireContext().getSharedPreferences(
                "app_prefs",
                android.content.Context.MODE_PRIVATE
            )
            prefs.edit().clear().apply()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", null)
        val email = prefs.getString("user_email", null)
        val dni = prefs.getString("user_dni", null)
        val phone = prefs.getString("user_phone", null)
        val role = prefs.getString("user_role", null)

        if (!name.isNullOrBlank()) binding.profileName.text = name
        if (!email.isNullOrBlank()) binding.profileEmail.text = email
        if (!dni.isNullOrBlank()) binding.profileDni.text = getString(R.string.profile_dni_format, dni)
        if (!phone.isNullOrBlank()) binding.profilePhone.text = getString(R.string.profile_phone_format, phone)
        if (!role.isNullOrBlank()) binding.profileRole.text = getString(R.string.profile_role_format, role)

        // Language selector buttons (ImageButtons in layout)
        val btnEs = binding.buttonLangEs
        val btnEn = binding.buttonLangEn
        val btnGl = binding.buttonLangGl

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
        LocaleHelper.setLocale(requireContext(), lang)
        // recreate activity to apply language
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
