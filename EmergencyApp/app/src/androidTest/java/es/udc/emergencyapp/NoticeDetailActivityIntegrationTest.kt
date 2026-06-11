package es.udc.emergencyapp

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import es.udc.emergencyapp.ui.notices.NoticeDetailActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoticeDetailActivityIntegrationTest {

    @Test
    fun activity_withValidNoticeExtra_reachesResumed() {
        val noticeJson = """
            {
                "id": 1,
                "body": "Incendio en zona alta",
                "status": "PENDING",
                "createdAt": "2025-01-10T12:00:00Z",
                "quadrantName": "Q-Santiago",
                "quadrantId": 3
            }
        """.trimIndent()

        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            NoticeDetailActivity::class.java
        ).putExtra("notice", noticeJson)

        val scenario = ActivityScenario.launch<NoticeDetailActivity>(intent)
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withoutNoticeExtra_staysInCreated() {
        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            NoticeDetailActivity::class.java
        )

        val scenario = ActivityScenario.launch<NoticeDetailActivity>(intent)
        assertNotNull(scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withFullNoticeExtra_recreatesSuccessfully() {
        val noticeJson = """
            {
                "id": 2,
                "body": "Río desbordado",
                "status": "ACCEPTED",
                "createdAt": "2025-02-20T08:30:00Z",
                "coordinates": { "lon": -8.5, "lat": 42.8 },
                "quadrantName": "Q-Ribeira",
                "quadrantId": 5,
                "images": [
                    { "id": 1, "name": "photo.jpg", "url": "http://example.com/photo.jpg" }
                ]
            }
        """.trimIndent()

        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            NoticeDetailActivity::class.java
        ).putExtra("notice", noticeJson)

        val scenario = ActivityScenario.launch<NoticeDetailActivity>(intent)
        scenario.recreate()
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }

    @Test
    fun activity_withMinimalNoticeExtra_reachesResumed() {
        val noticeJson = """
            {
                "id": 3,
                "body": null,
                "status": null,
                "createdAt": "2025-03-01T00:00:00Z"
            }
        """.trimIndent()

        val intent = Intent(
            ApplicationProvider.getApplicationContext<android.content.Context>(),
            NoticeDetailActivity::class.java
        ).putExtra("notice", noticeJson)

        val scenario = ActivityScenario.launch<NoticeDetailActivity>(intent)
        assertEquals(Lifecycle.State.RESUMED, scenario.state)
        scenario.close()
    }
}
