package es.udc.emergencyapp

import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.ImageView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import es.udc.emergencyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lang = LocaleHelper.getPersistedLanguage(this)
        LocaleHelper.setLocale(this, lang)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        val navController = navHostFragment.navController
        fun navControllerSave(nc: androidx.navigation.NavController) = nc.currentDestination?.id
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isMap = destination.id == R.id.nav_map
            binding.appBarMain.toolbar.visibility = if (isMap) View.GONE else View.VISIBLE
            binding.appBarMain.fab?.visibility = if (isMap) View.GONE else View.VISIBLE
        }

        binding.navView.let {
            try {
                it.menu.clear()
                it.inflateMenu(R.menu.navigation_drawer)
                it.itemIconTintList = null
                android.util.Log.d("MainActivityNav", "Drawer menu item count=${it.menu.size}")
            } catch (e: Exception) {
                android.util.Log.w("MainActivityNav", "Failed to reinflate drawer menu", e)
            }
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_organizations,
                    R.id.nav_my_team,
                    R.id.nav_my_notices,
                    R.id.nav_profile,
                    R.id.nav_map
                ),
                binding.drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)

            it.setNavigationItemSelectedListener { menuItem ->
                val destId = menuItem.itemId
                val currentId = navController.currentDestination?.id
                if (destId != currentId) {
                    val options = androidx.navigation.NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                    if (destId == R.id.nav_profile) {
                        navController.navigate(R.id.nav_profile, null, options)
                    } else {
                        val navOptions = androidx.navigation.NavOptions.Builder()
                            .setLaunchSingleTop(true)
                            .setPopUpTo(navController.graph.startDestinationId, false)
                            .build()
                        navController.navigate(destId, null, navOptions)
                    }
                }
                binding.drawerLayout.closeDrawers()
                true
            }
            try {
                val header = it.getHeaderView(0)
                val headerImage = header.findViewById<ImageView>(R.id.imageView)
                val headerTitle = header.findViewById<android.widget.TextView>(R.id.header_name)
                val headerSubtitle = header.findViewById<android.widget.TextView>(R.id.header_email)

                val prefs = this.getSharedPreferences("app_prefs", MODE_PRIVATE)
                val token = prefs.getString("jwt_token", null)
                val name = prefs.getString("user_name", getString(R.string.nav_header_title))
                val email = prefs.getString("user_email", getString(R.string.nav_header_subtitle))

                if (token.isNullOrBlank()) {
                    // No logged user: show explicit login hint and icon
                    headerTitle?.text = getString(R.string.nav_header_login_title)
                    headerSubtitle?.text = getString(R.string.nav_header_login_subtitle)
                    headerImage?.setImageResource(android.R.drawable.ic_dialog_email)

                    // clicking header opens LoginActivity (make whole header clickable)
                    header.setOnClickListener {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        binding.drawerLayout.closeDrawers()
                    }
                } else {
                    // Show stored profile info and keep existing profile navigation
                    headerTitle?.text = name
                    headerSubtitle?.text = email
                    // make whole header navigate to profile
                    header.setOnClickListener {
                        val currentId = navControllerSave(navController)
                        if (currentId != R.id.nav_profile) {
                            val options = androidx.navigation.NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .build()
                            navController.navigate(R.id.nav_profile, null, options)
                        }
                        binding.drawerLayout.closeDrawers()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w(
                    "MainActivityNav",
                    "Failed to set header click or populate header",
                    e
                )
            }
        }

        binding.appBarMain.contentMain.bottomNavView?.setupWithNavController(navController)

        // If a JWT token is stored, attempt to refresh profile info from backend
        val prefs = this.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        if (!token.isNullOrBlank()) {
            android.util.Log.d("MainActivityNav", "Found jwt token, fetching profile")
            fetchProfileAndPopulate(token)
        } else {
            android.util.Log.d("MainActivityNav", "No jwt token found in prefs")
        }
    }

    // Fetch profile from backend and populate SharedPreferences + drawer header.
    // Uses emulator host mapping: http://10.0.2.2:8080/users/me (maps to localhost:8080)
    private fun fetchProfileAndPopulate(token: String) {
        Thread {
            try {
                val url = URL("http://10.0.2.2:8080/users/me")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    setRequestProperty("Authorization", "Bearer $token")
                    connectTimeout = 5000
                    readTimeout = 5000
                }
                val code = conn.responseCode
                if (code == 200) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val obj = JSONObject(body)
                    // defensive: try multiple field names
                    val name = when {
                        obj.has("name") -> obj.getString("name")
                        obj.has("username") -> obj.getString("username")
                        obj.has("fullName") -> obj.getString("fullName")
                        else -> null
                    }
                    val email = when {
                        obj.has("email") -> obj.getString("email")
                        obj.has("mail") -> obj.getString("mail")
                        else -> null
                    }
                    // save into prefs
                    val prefs = this.getSharedPreferences("app_prefs", MODE_PRIVATE)
                    prefs.edit().apply {
                        if (name != null) putString("user_name", name)
                        if (email != null) putString("user_email", email)
                        apply()
                    }

                    // update UI on main thread
                    runOnUiThread {
                        try {
                            val navView = binding.navView
                            val header = navView.getHeaderView(0)
                            val headerTitle = header.findViewById<android.widget.TextView>(R.id.header_name)
                            val headerSubtitle = header.findViewById<android.widget.TextView>(R.id.header_email)
                            if (name != null) headerTitle?.text = name
                            if (email != null) headerSubtitle?.text = email
                        } catch (e: Exception) {
                            android.util.Log.w("MainActivityNav", "Failed to update header UI", e)
                        }
                    }
                } else {
                    android.util.Log.w("MainActivityNav", "Profile fetch returned $code")
                }
                conn.disconnect()
            } catch (e: Exception) {
                android.util.Log.w("MainActivityNav", "Failed to fetch profile", e)
            }
        }.start()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getPersistedLanguage(newBase)
        val ctx = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(ctx)
    }
}
