package es.udc.emergencyapp.ui.notices

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import es.udc.emergencyapp.R
import es.udc.emergencyapp.data.dto.NoticeDto
import es.udc.emergencyapp.util.DateUtils

class NoticeDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_detail)

        val json = intent.getStringExtra("notice") ?: return
        val notice = Gson().fromJson(json, NoticeDto::class.java)

        val tvTitle = findViewById<android.widget.TextView>(R.id.tvDetailTitle)
        val tvStatus = findViewById<android.widget.TextView>(R.id.tvDetailStatus)
        val tvCreatedAt = findViewById<android.widget.TextView>(R.id.tvDetailCreatedAt)
        val tvBody = findViewById<android.widget.TextView>(R.id.tvDetailBody)

        val tvCoordinates = findViewById<android.widget.TextView>(R.id.tvDetailCoordinates)
        val tvQuadrantId = findViewById<android.widget.TextView>(R.id.tvDetailQuadrantId)

        tvTitle.text = notice.quadrantName ?: ""
        val rawStatus = notice.status ?: ""
        val statusLabel = when (rawStatus) {
            "ACCEPTED" -> getString(R.string.status_accepted)
            "REJECTED" -> getString(R.string.status_rejected)
            else -> getString(R.string.status_pending)
        }
        tvStatus.text = buildString {
            append(getString(R.string.label_status))
            append(": ")
            append(statusLabel)
        }
        val bgColor = when (rawStatus) {
            "ACCEPTED" -> getColor(R.color.status_accepted)
            "REJECTED" -> getColor(R.color.status_rejected)
            else -> getColor(R.color.status_pending)
        }
        tvStatus.background.setTint(bgColor)
        tvCreatedAt.text =
            buildString {
                append(getString(R.string.label_date))
                append(": ")
                append(DateUtils.formatServerDate(notice.createdAt))
            }
        tvBody.text = buildString {
            append(getString(R.string.label_description))
            append(": ")
            append((notice.body ?: ""))
        }

        notice.coordinates?.let { c ->
            tvCoordinates.text =
                buildString {
                    append(getString(R.string.label_coordinates))
                    append(": Lon=${c.lon}, Lat=${c.lat}")
                }
        }
        tvQuadrantId.text =
            buildString {
                append(getString(R.string.label_quadrant))
                append(": ${notice.quadrantName} (${notice.quadrantId})")
            }

        val pager = findViewById<ViewPager2>(R.id.pagerImages)
        val images = notice.images.mapNotNull { it.url }
        pager.adapter = ImagePagerAdapter(this, images)
    }
}
