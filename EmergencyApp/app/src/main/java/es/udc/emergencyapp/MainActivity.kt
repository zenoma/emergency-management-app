package es.udc.emergencyapp

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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

        binding.appBarMain.fab?.setOnClickListener { _ ->
            val navController = findNavController(R.id.nav_host_fragment_content_main)
                val currentId = navController.currentDestination?.id
                if (currentId != R.id.nav_profile) {
                    val options = androidx.navigation.NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                    navController.navigate(R.id.nav_profile, null, options)
                }
            }

        val navHostFragment =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment?)!!
        val navController = navHostFragment.navController
        // Make the map destination full-screen: hide toolbar, FAB and bottom nav when nav_map is active
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isMap = destination.id == R.id.nav_map
            // hide only the top app bar and FAB for the map; keep bottom navigation and drawer available
            binding.appBarMain.toolbar.visibility = if (isMap) View.GONE else View.VISIBLE
            binding.appBarMain.fab?.visibility = if (isMap) View.GONE else View.VISIBLE
        }

        binding.navView?.let {
            try {
                it.menu.clear()
                it.inflateMenu(R.menu.navigation_drawer)
                it.itemIconTintList = null
                // Ensure menu matches navigation resources (no dashboard item)
                android.util.Log.d("MainActivityNav", "Drawer menu item count=${it.menu.size()}")
            } catch (e: Exception) {
                android.util.Log.w("MainActivityNav", "Failed to reinflate drawer menu", e)
            }
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_organizations, R.id.nav_my_team, R.id.nav_my_notices, R.id.nav_profile, R.id.nav_map
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
                        // If nav_map is selected, map fragment id exists in nav graph
                        navController.navigate(destId, null, navOptions)
                    }
                }
                binding.drawerLayout?.closeDrawers()
                true
            }
        }

        binding.appBarMain.contentMain.bottomNavView?.let {
                appBarConfiguration = AppBarConfiguration(
                    setOf(
                        R.id.nav_organizations, R.id.nav_my_team, R.id.nav_my_notices, R.id.nav_map
                    )
                )
            setupActionBarWithNavController(navController, appBarConfiguration)
            it.setupWithNavController(navController)
        }
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
