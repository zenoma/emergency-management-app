package es.udc.emergencyapp.ui.emergencies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import es.udc.emergencyapp.AppTheme
import es.udc.emergencyapp.ui.setContentWithSystemBars

class QuadrantResourcesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val emergencyId = intent?.extras?.getLong("emergencyId") ?: return
        val quadrantId = intent?.extras?.getLong("quadrantId") ?: return

        setContentWithSystemBars {
            AppTheme {
                val refreshCounter = remember { mutableStateOf(0) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Recursos cuadrante") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    refreshCounter.value = refreshCounter.value + 1
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = "Refrescar"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colors.background
                    ) {
                        QuadrantResourcesScreen(
                            emergencyId = emergencyId,
                            quadrantId = quadrantId,
                            onClose = { finish() },
                            externalRefreshCounter = refreshCounter.value
                        )
                    }
                }
            }
        }
    }
}
