package es.udc.emergencyapp

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityIntegrationTest {

    @Test
    fun activity_launchesSuccessfully() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        assertNotNull(scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withExtraRoute_launchesSuccessfully() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            MainActivity::class.java
        ).putExtra(MainActivity.EXTRA_ROUTE, "profile")

        val scenario = ActivityScenario.launch<MainActivity>(intent)
        assertNotNull(scenario.state)
        scenario.close()
    }
}
