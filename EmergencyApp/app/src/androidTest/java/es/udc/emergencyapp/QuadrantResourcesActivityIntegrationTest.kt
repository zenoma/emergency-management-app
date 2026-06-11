package es.udc.emergencyapp

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.udc.emergencyapp.ui.emergencies.QuadrantResourcesActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuadrantResourcesActivityIntegrationTest {

    @Test
    fun activity_withValidExtras_reachesResumed() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            QuadrantResourcesActivity::class.java
        ).apply {
            putExtra("emergencyId", 1L)
            putExtra("quadrantId", 5L)
        }

        val scenario = ActivityScenario.launch<QuadrantResourcesActivity>(intent)
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withoutExtras_doesNotCrashOnLaunch() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            QuadrantResourcesActivity::class.java
        )

        val scenario = ActivityScenario.launch<QuadrantResourcesActivity>(intent)
        assertNotNull(scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withoutEmergencyId_doesNotCrash() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            QuadrantResourcesActivity::class.java
        ).putExtra("quadrantId", 5L)

        val scenario = ActivityScenario.launch<QuadrantResourcesActivity>(intent)
        assertNotNull(scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withoutQuadrantId_doesNotCrash() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            QuadrantResourcesActivity::class.java
        ).putExtra("emergencyId", 1L)

        val scenario = ActivityScenario.launch<QuadrantResourcesActivity>(intent)
        assertNotNull(scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withValidExtras_recreatesSuccessfully() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            QuadrantResourcesActivity::class.java
        ).apply {
            putExtra("emergencyId", 1L)
            putExtra("quadrantId", 5L)
        }

        val scenario = ActivityScenario.launch<QuadrantResourcesActivity>(intent)
        scenario.recreate()
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }
}
