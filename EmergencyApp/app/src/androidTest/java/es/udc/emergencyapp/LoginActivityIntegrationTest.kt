package es.udc.emergencyapp

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class LoginActivityIntegrationTest {

    @Test
    fun activity_launchesAndReachesResumedState() {
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }

    @Test
    fun activity_onRecreate_stateIsResumed() {
        val scenario = ActivityScenario.launch(LoginActivity::class.java)
        scenario.recreate()
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }
}
