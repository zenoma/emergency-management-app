package es.udc.emergencyapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object DrawerBadgeState {
    var refreshTrigger by mutableStateOf(0)
}
