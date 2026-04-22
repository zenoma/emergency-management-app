package es.udc.emergencyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import es.udc.emergencyapp.ui.map.MapScreen
import es.udc.emergencyapp.ui.notices.MyNoticesScreen
import es.udc.emergencyapp.ui.profile.ProfileScreen
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var composeRouteSetter: ((String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lang = LocaleHelper.getPersistedLanguage(this)
        LocaleHelper.setLocale(this, lang)

        setContent {
            AppTheme {
                MainScreenSimple()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = LocaleHelper.getPersistedLanguage(newBase)
        val ctx = LocaleHelper.setLocale(newBase, lang)
        super.attachBaseContext(ctx)
    }

    fun navigateToRoute(route: String) {
        try {
            composeRouteSetter?.invoke(route)
        } catch (e: Exception) {
            android.util.Log.w("MainActivityNav", "Failed to set route $route", e)
        }
    }
}

@Composable
private fun MainScreenSimple() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    (LocalContext.current as? MainActivity)?.composeRouteSetter = { r ->
        try {
            navController.navigate(r) {
                launchSingleTop = true
            }
        } catch (e: Exception) {
            android.util.Log.w("MainActivityNav", "Failed to navigate to $r", e)
        }
    }

    val items = listOf(
        Pair("map", "Map"),
        Pair("notices", "Notices"),
        Pair("organizations", "Organizations"),
        Pair("myteam", "My Team"),
        Pair("profile", "Profile")
    )

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        scaffoldState = scaffoldState,
        // Disable swipe-to-open so map gestures are not intercepted by the drawer.
        drawerGesturesEnabled = false,
        topBar = {
            androidx.compose.material.TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text(text = "EmergencyApp") },
                navigationIcon = {
                    androidx.compose.material.IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallery_black_24dp),
                            contentDescription = null
                        )
                    }
                })
        },
        drawerContent = {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val jwtLocal = prefs.getString("jwt_token", null)
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    color = MaterialTheme.colors.primary,
                    contentColor = contentColorFor(MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(4.dp))
                        if (jwtLocal.isNullOrBlank()) {
                            androidx.compose.material.IconButton(onClick = {
                                try {
                                    val intent = Intent(context, LoginActivity::class.java)
                                    context.startActivity(intent)
                                    scope.launch { scaffoldState.drawerState.close() }
                                } catch (e: Exception) {
                                    android.util.Log.w("MainActivity", "Failed to open Login", e)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Login,
                                    contentDescription = "Login",
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        } else {
                            AndroidView(
                                factory = { ctx ->
                                    val iv = androidx.appcompat.widget.AppCompatImageView(ctx)
                                    val size = (64 * ctx.resources.displayMetrics.density).toInt()
                                    iv.layoutParams =
                                        android.view.ViewGroup.LayoutParams(size, size)
                                    iv.scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                                    try {
                                        val avatarUrl = prefs.getString("avatar_url", null)
                                        val model = if (!avatarUrl.isNullOrBlank()) {
                                            val headers =
                                                com.bumptech.glide.load.model.LazyHeaders.Builder()
                                                    .addHeader(
                                                        "Authorization",
                                                        "Bearer ${jwtLocal}"
                                                    )
                                                    .build()
                                            com.bumptech.glide.load.model.GlideUrl(
                                                avatarUrl,
                                                headers
                                            )
                                        } else R.drawable.avatar_1
                                        com.bumptech.glide.Glide.with(ctx).load(model).circleCrop()
                                            .into(iv)
                                    } catch (e: Exception) {
                                        android.util.Log.w(
                                            "MainActivity",
                                            "Failed to load avatar",
                                            e
                                        )
                                    }
                                    iv
                                },
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(androidx.compose.foundation.shape.CircleShape)
                                    .clickable {
                                        try {
                                            navController.navigate("profile") {
                                                launchSingleTop = true
                                            }
                                            scope.launch { scaffoldState.drawerState.close() }
                                        } catch (e: Exception) {
                                            android.util.Log.w(
                                                "MainActivity",
                                                "Failed to navigate to profile",
                                                e
                                            )
                                        }
                                    })

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = prefs.getString("user_name", "User") ?: "User",
                                modifier = Modifier.padding(4.dp)
                            )
                            Text(text = "Logout", modifier = Modifier
                                .padding(6.dp)
                                .clickable {
                                    try {
                                        prefs.edit().clear().apply()
                                        val i = Intent(context, MainActivity::class.java)
                                        i.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(i)
                                    } catch (e: Exception) {
                                        android.util.Log.w("MainActivity", "Logout failed", e)
                                    }
                                })
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                val routes = listOf(
                    Pair("map", "Map"),
                    Pair("notices", "Notices"),
                    Pair("organizations", "Organizations"),
                    Pair("myteam", "My Team"),
                    Pair("profile", "Profile"),
                    Pair("send_notice", "Send Notice"),
                    Pair("fire_management", "Fire Management"),
                    Pair("user_management", "User Management"),
                    Pair("notice_management", "Notice Management")
                )
                routes.forEach { (route, title) ->
                    Text(
                        text = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(route) { launchSingleTop = true }
                                // close drawer
                                scope.launch { scaffoldState.drawerState.close() }
                            }
                            .padding(12.dp)
                    )
                }
            }
        },
        bottomBar = {
            BottomAppBar(elevation = 8.dp, modifier = Modifier.navigationBarsPadding()) {
                BottomNavigation(modifier = Modifier.fillMaxWidth()) {
                    val current = navBackStackEntry?.destination?.route
                    items.forEach { (route, title) ->
                        BottomNavigationItem(
                            selected = current == route,
                            onClick = {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_gallery_black_24dp),
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "map"
            ) {
                composable("map") { MapScreen() }
                composable("notices") { MyNoticesScreen() }
                composable("organizations") { FeaturePlaceholder("Organizations") }
                composable("myteam") { FeaturePlaceholder("My Team") }
                composable("profile") { ProfileScreenCompose() }
                composable("send_notice") { FeaturePlaceholder("Send Notice") }
                composable("fire_management") { FeaturePlaceholder("Fire Management") }
                composable("user_management") { FeaturePlaceholder("User Management") }
                composable("notice_management") { FeaturePlaceholder("Notice Management") }
            }
        }
    }
}

@Composable
private fun ProfileScreenCompose() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val name = prefs.getString("user_name", "") ?: ""
    val email = prefs.getString("user_email", "") ?: ""
    val dni = prefs.getString("user_dni", "") ?: ""
    val phone = prefs.getString("user_phone", "") ?: ""
    val role = prefs.getString("user_role", "") ?: ""
    val currentLang = LocaleHelper.getPersistedLanguage(context)

    ProfileScreen(
        name = name,
        email = email,
        dni = dni,
        phone = phone,
        role = role,
        currentLang = currentLang,
        onLogout = {
            prefs.edit().clear().apply()
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        },
        onChangeLanguage = { lang ->
            LocaleHelper.persistLanguage(context, lang)
            LocaleHelper.setLocale(context, lang)
            (context as? AppCompatActivity)?.recreate()
        }
    )
}

@Composable
private fun FeaturePlaceholder(name: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = name, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Placeholder - migrate screen to Compose")
        }
    }
}
