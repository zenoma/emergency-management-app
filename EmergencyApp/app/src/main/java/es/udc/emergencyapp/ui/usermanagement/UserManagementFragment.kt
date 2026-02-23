package es.udc.emergencyapp.ui.usermanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.udc.emergencyapp.databinding.FragmentMyTeamBinding

class UserManagementFragment : Fragment() {
    private var _binding: FragmentMyTeamBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyTeamBinding.inflate(inflater, container, false)
        binding.root.findViewById<android.widget.TextView>(android.R.id.text1)?.text = "User Management (placeholder)"
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
