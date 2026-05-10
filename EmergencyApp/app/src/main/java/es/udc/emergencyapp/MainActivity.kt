package es.udc.emergencyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.edit
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.firebase.messaging.FirebaseMessaging
import es.udc.emergencyapp.net.HttpClient
import es.udc.emergencyapp.ui.map.MapScreen
import es.udc.emergencyapp.ui.notices.MyNoticesScreen
import es.udc.emergencyapp.ui.notices.SendNoticeFragment
import es.udc.emergencyapp.ui.profile.ProfileScreen
import es.udc.emergencyapp.ui.myteam.MyAssignmentsScreen
import org.json.JSONObject
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

    override fun onStart() {
        super.onStart()
        syncMobileDeviceToken()
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
            Log.w("MainActivityNav", "Failed to set route $route", e)
        }
    }

    private fun syncMobileDeviceToken() {
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userId = prefs.getLong("user_id", -1L)
        if (userId <= 0) return

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { fcmToken ->
                Thread {
                    try {
                        val payload = JSONObject().apply {
                            put("fcmToken", fcmToken)
                        }.toString()
                        val response = HttpClient.postToHosts("/users/$userId/mobileDevice", this@MainActivity, payload)
                        Log.d(
                            "MainActivityNet",
                            "Mobile device sync host=${response.second} body=${response.first}"
                        )
                    } catch (e: Exception) {
                        Log.w("MainActivityNet", "Failed to sync mobile device", e)
                    }
                }.start()
            }
            .addOnFailureListener { e ->
                Log.w("MainActivityNet", "Failed to get FCM token", e)
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
            navController.navigate(r) { launchSingleTop = true }
        } catch (e: Exception) {
            Log.w("MainActivityNav", "Failed to navigate to $r", e)
        }
    }

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        scaffoldState = scaffoldState,
        drawerGesturesEnabled = false,
        topBar = {
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                title = { Text(text = "EmergencyApp") },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { scaffoldState.drawerState.open() } }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        drawerContent = {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val jwtLocal = prefs.getString("jwt_token", null)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.06f))
            ) {
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
                                IconButton(onClick = {
                                    try {
                                        val intent = Intent(context, LoginActivity::class.java)
                                        context.startActivity(intent)
                                        scope.launch { scaffoldState.drawerState.close() }
                                    } catch (e: Exception) {
                                        Log.w("MainActivity", "Failed to open Login", e)
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
                                        val iv = AppCompatImageView(ctx)
                                        val size =
                                            (64 * ctx.resources.displayMetrics.density).toInt()
                                        iv.layoutParams = ViewGroup.LayoutParams(size, size)
                                        iv.scaleType = ImageView.ScaleType.CENTER_CROP
                                        try {
                                            val avatarUrl = prefs.getString("avatar_url", null)
                                            val model = if (!avatarUrl.isNullOrBlank()) {
                                                val headers = LazyHeaders.Builder()
                                                    .addHeader(
                                                        "Authorization",
                                                        "Bearer $jwtLocal"
                                                    )
                                                    .build()
                                                GlideUrl(avatarUrl, headers)
                                            } else R.drawable.avatar_1
                                            Glide.with(ctx).load(model).circleCrop().into(iv)
                                        } catch (e: Exception) {
                                            Log.w("MainActivity", "Failed to load avatar", e)
                                        }
                                        iv
                                    }, modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            try {
                                                navController.navigate("profile") {
                                                    launchSingleTop = true
                                                }
                                                scope.launch { scaffoldState.drawerState.close() }
                                            } catch (e: Exception) {
                                                Log.w(
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
                                Text(
                                    text = "Logout", modifier = Modifier
                                        .padding(6.dp)
                                        .clickable {
                                            try {
                                                prefs.edit { clear() }
                                                val i = Intent(context, MainActivity::class.java)
                                                i.flags =
                                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                context.startActivity(i)
                                            } catch (e: Exception) {
                                                Log.w("MainActivity", "Logout failed", e)
                                            }
                                        })
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    // Logical blocks
                    val block1 = listOf(
                        Triple("map", "Map", Icons.Filled.Map),
                        Triple("myteam", "My Team", Icons.Filled.Group),
                        Triple("organizations", "Organizations", Icons.Filled.Business),
                        Triple("emergencies", "Emergencies", Icons.Filled.Description)
                    )
                    val block2 = listOf(
                        Triple("send_notice", "Send Notice", Icons.Filled.Send),
                        Triple("notices", "My Notices", Icons.Filled.Description)
                    )

                    @Composable
                    fun renderBlock(itemsBlock: List<Triple<String, String, ImageVector>>) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {
                            itemsBlock.forEach { (route, title, icon) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate(route) { launchSingleTop = true }
                                            scope.launch { scaffoldState.drawerState.close() }
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Text(text = title, style = MaterialTheme.typography.body1)
                                }
                            }
                        }
                    }

                    renderBlock(block1)
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    )
                    renderBlock(block2)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        },
        bottomBar = {
            val ctx = LocalContext.current
            val prefsBb = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            val jwtBb = prefsBb.getString("jwt_token", null)

            BottomAppBar(elevation = 8.dp, modifier = Modifier.navigationBarsPadding()) {
                BottomNavigation(modifier = Modifier.fillMaxWidth()) {
                    val current = navBackStackEntry?.destination?.route
                    BottomNavigationItem(
                        selected = current == "map",
                        onClick = { navController.navigate("map") { launchSingleTop = true } },
                        icon = { Icon(imageVector = Icons.Filled.Map, contentDescription = null) },
                        label = { Text(text = "Map") })
                    BottomNavigationItem(
                        selected = current == "send_notice",
                        onClick = {
                            navController.navigate("send_notice") {
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(imageVector = Icons.Filled.Send, contentDescription = null) },
                        label = { Text(text = "Send") })
                    if (!jwtBb.isNullOrBlank()) {
                        BottomNavigationItem(
                            selected = current == "myteam",
                            onClick = {
                                navController.navigate("myteam") {
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Group,
                                    contentDescription = null
                                )
                            },
                            label = { Text(text = "My Team") })
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
            NavHost(navController = navController, startDestination = "map") {
                composable("map") { es.udc.emergencyapp.ui.ScreenContainer { MapScreen() } }
                composable("notices") { es.udc.emergencyapp.ui.ScreenContainer { MyNoticesScreen() } }
                composable("emergencies") { es.udc.emergencyapp.ui.ScreenContainer { es.udc.emergencyapp.ui.emergencies.EmergenciesScreen() } }
                composable("organizations") { es.udc.emergencyapp.ui.ScreenContainer { FeaturePlaceholder("Organizations") } }
                composable("myteam") {
                    es.udc.emergencyapp.ui.ScreenContainer {
                        es.udc.emergencyapp.ui.myteam.MyTeamScreen(onOpenAssignments = { teamId ->
                            navController.navigate("myassignments/$teamId") { launchSingleTop = true }
                        })
                    }
                }
                composable(
                    "myassignments/{teamId}",
                    arguments = listOf(navArgument("teamId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val teamId = backStackEntry.arguments?.getLong("teamId") ?: -1L
                    es.udc.emergencyapp.ui.ScreenContainer { MyAssignmentsScreen(teamId) }
                }
                composable("profile") { es.udc.emergencyapp.ui.ScreenContainer { ProfileScreenCompose() } }
                composable("send_notice") { es.udc.emergencyapp.ui.ScreenContainer { SendNoticeHost() } }
                composable("fire_management") { FeaturePlaceholder("Fire Management") }
                composable("user_management") { FeaturePlaceholder("User Management") }
                composable("notice_management") { FeaturePlaceholder("Notice Management") }
            }
        }
    }
}

@Composable
fun SendNoticeHost() {
    val activity = LocalContext.current as? AppCompatActivity
    val containerId = remember { View.generateViewId() }
    AndroidView(factory = { ctx ->
        val fc = FragmentContainerView(ctx).apply {
            id = containerId
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        // attach fragment
        activity?.supportFragmentManager?.commit {
            replace(containerId, SendNoticeFragment())
        }
        fc
    }, modifier = Modifier.fillMaxSize())
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
            prefs.edit { clear() }
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        },
        onChangeLanguage = { lang: String ->
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
