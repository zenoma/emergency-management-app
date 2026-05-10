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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.gson.Gson
import es.udc.emergencyapp.AppTheme
import es.udc.emergencyapp.R
import es.udc.emergencyapp.data.dto.EmergencyDto
import es.udc.emergencyapp.ui.setContentWithSystemBars

class EmergencyDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = intent.getStringExtra("emergency") ?: return
        val emergency = try {
            Gson().fromJson(json, EmergencyDto::class.java)
        } catch (e: Exception) {
            null
        } ?: return

        setContentWithSystemBars {
            AppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = stringResource(R.string.emergency_title)) },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back_label)
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
                        EmergencyDetailScreen(emergency = emergency, onClose = { finish() })
                    }
                }
            }
        }
    }
}
