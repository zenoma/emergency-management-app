package es.udc.emergencyapp.ui.notices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import es.udc.emergencyapp.data.dto.NoticeDto
import es.udc.emergencyapp.databinding.FragmentMyNoticesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class MyNoticesFragment : Fragment() {
    private var _binding: FragmentMyNoticesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyNoticesBinding.inflate(inflater, container, false)
        val adapter = NoticeAdapter { notice -> openDetail(notice) }
        binding.recyclerNotices.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNotices.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { loadNotices(adapter) }
        loadNotices(adapter)
        return binding.root
    }

    private fun loadNotices(adapter: NoticeAdapter) {
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            val list = withContext(Dispatchers.IO) { fetchNotices() }
            adapter.submitList(list)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun fetchNotices(): List<NoticeDto> {
        try {
            val hostsToTry = listOf(
                "http://10.0.2.2:8080",
                "http://10.0.2.2:8000",
                "http://127.0.0.1:8000",
                "http://192.168.1.100:8000"
            )
            val prefs = requireContext().getSharedPreferences(
                "app_prefs",
                android.content.Context.MODE_PRIVATE
            )
            val userId = prefs.getLong("user_id", -1L)
            for (host in hostsToTry) {
                try {
                    val query = if (userId > 0) "?userId=$userId" else ""
                    val url = URL("$host/notices$query")
                    val conn = (url.openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        connectTimeout = 5000
                        readTimeout = 5000
                    }
                    val code = conn.responseCode
                    val resp = if (code in 200..299) conn.inputStream.bufferedReader()
                        .use { it.readText() }
                    else conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                    conn.disconnect()
                    if (code in 200..299) {
                        val arr = JSONArray(resp)
                        val out = mutableListOf<NoticeDto>()
                        for (i in 0 until arr.length()) {
                            val o = arr.getJSONObject(i)
                            val images = mutableListOf<es.udc.emergencyapp.data.dto.ImageDto>()
                            if (o.has("imageDtoList")) {
                                val ia = o.getJSONArray("imageDtoList")
                                for (j in 0 until ia.length()) {
                                    val io = ia.getJSONObject(j)
                                    val name = io.optString("name")
                                    val url = if (name.isNullOrEmpty()) null else "$host/images/${
                                        o.getLong("id")
                                    }/$name"
                                    images.add(
                                        es.udc.emergencyapp.data.dto.ImageDto(
                                            io.optLong("id"),
                                            name,
                                            url
                                        )
                                    )
                                }
                            }
                            val userDto = if (o.has("userDto")) {
                                val u = o.getJSONObject("userDto")
                                es.udc.emergencyapp.data.dto.UserDto(
                                    u.optLong("id"),
                                    u.optString("firstName"),
                                    u.optString("lastName"),
                                    u.optString("email"),
                                    u.optString("phoneNumber"),
                                    u.optString("dni"),
                                    u.optString("userRole")
                                )
                            } else null

                            val coordinates = if (o.has("coordinates")) {
                                val c = o.getJSONObject("coordinates")
                                es.udc.emergencyapp.data.dto.CoordinatesDto(
                                    c.optDouble("lon"),
                                    c.optDouble("lat")
                                )
                            } else null

                            out.add(
                                NoticeDto(
                                    o.getLong("id"),
                                    o.optString("body"),
                                    o.optString("status"),
                                    o.optString("createdAt"),
                                    o.optString("quadrantName"),
                                    if (o.has("quadrantId")) o.optInt("quadrantId") else null,
                                    userDto,
                                    coordinates,
                                    images
                                )
                            )
                        }
                        return out
                    }
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
        }
        return emptyList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openDetail(notice: NoticeDto) {
        val i = android.content.Intent(requireContext(), NoticeDetailActivity::class.java)
        i.putExtra("notice", Gson().toJson(notice))
        startActivity(i)
    }
}
