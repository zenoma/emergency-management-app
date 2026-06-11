package es.udc.emergencyapp

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.udc.emergencyapp.ui.emergencies.EmergencyDetailActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmergencyDetailActivityIntegrationTest {

    @Test
    fun activity_withValidEmergencyExtra_reachesResumed() {
        val emergencyJson = """
            {
                "id": 1,
                "description": "Incendio",
                "emergencyTypeName": "Fire",
                "emergencyIndex": "UNO",
                "createdAt": "2024-08-15T14:30:00Z",
                "quadrantInfo": []
            }
        """.trimIndent()

        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            EmergencyDetailActivity::class.java
        ).putExtra("emergency", emergencyJson)

        val scenario = ActivityScenario.launch<EmergencyDetailActivity>(intent)
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withoutEmergencyExtra_launchesWithoutCrash() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            EmergencyDetailActivity::class.java
        )

        val scenario = ActivityScenario.launch<EmergencyDetailActivity>(intent)
        assertNotNull(scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withInvalidJsonExtra_launchesWithoutCrash() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            EmergencyDetailActivity::class.java
        ).putExtra("emergency", "not-valid-json")

        val scenario = ActivityScenario.launch<EmergencyDetailActivity>(intent)
        assertNotNull(scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withEmptyJsonExtra_doesNotCrash() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            EmergencyDetailActivity::class.java
        ).putExtra("emergency", "")

        val scenario = ActivityScenario.launch<EmergencyDetailActivity>(intent)
        assertNotNull(scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withNullJsonExtra_doesNotCrash() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            EmergencyDetailActivity::class.java
        )
        intent.putExtra("emergency", null as String?)

        val scenario = ActivityScenario.launch<EmergencyDetailActivity>(intent)
        assertNotNull(scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withFullEmergencyExtra_recreatesSuccessfully() {
        val emergencyJson = """
            {
                "id": 2,
                "description": "Inundación",
                "emergencyTypeName": "Flood",
                "emergencyIndex": "TRES",
                "createdAt": "2024-09-01T10:00:00Z",
                "resolvedAt": null,
                "location": { "lon": -8.0, "lat": 43.0 },
                "quadrantInfo": [
                    { "id": 5, "escala": "Crítica", "nombre": "Q-Ribeira" }
                ]
            }
        """.trimIndent()

        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            EmergencyDetailActivity::class.java
        ).putExtra("emergency", emergencyJson)

        val scenario = ActivityScenario.launch<EmergencyDetailActivity>(intent)
        scenario.recreate()
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }
}
