package es.udc.emergencyapp

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignupActivityIntegrationTest {

    @Test
    fun activity_launchesAndReachesResumedState() {
        val scenario = ActivityScenario.launch(SignupActivity::class.java)
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }

    @Test
    fun activity_onRecreate_stateIsResumed() {
        val scenario = ActivityScenario.launch(SignupActivity::class.java)
        scenario.recreate()
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }
}
