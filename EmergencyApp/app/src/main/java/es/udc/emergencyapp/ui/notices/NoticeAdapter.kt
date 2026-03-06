package es.udc.emergencyapp.ui.notices

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.udc.emergencyapp.R
import es.udc.emergencyapp.data.dto.NoticeDto
import es.udc.emergencyapp.util.DateUtils

class NoticeAdapter(private val onClick: (NoticeDto) -> Unit) :
    ListAdapter<NoticeDto, NoticeAdapter.VH>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_notice, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val thumb: ImageView = view.findViewById(R.id.ivThumb)
        private val title: TextView = view.findViewById(R.id.tvTitle)
        private val body: TextView = view.findViewById(R.id.tvBody)
        private val date: TextView = view.findViewById(R.id.tvDate)
        private val tvQuadrant: TextView = view.findViewById(R.id.tvQuadrant)
        private val tvStatus: TextView = view.findViewById(R.id.tvStatus)

        fun bind(n: NoticeDto) {
            title.text = n.quadrantName ?: itemView.context.getString(R.string.notice_default_title)
            body.text =
                n.body?.let { if (it.length > 120) it.substring(0, 120) + "…" else it } ?: ""
            date.text = DateUtils.formatServerDate(n.createdAt)
            tvQuadrant.text = n.quadrantName ?: ""
            val statusLabelKey = when (n.status) {
                "ACCEPTED" -> R.string.status_accepted
                "REJECTED" -> R.string.status_rejected
                else -> R.string.status_pending
            }
            tvStatus.text = itemView.context.getString(statusLabelKey)
            val statusColor = when (n.status) {
                "ACCEPTED" -> itemView.context.getColor(R.color.status_accepted)
                "REJECTED" -> itemView.context.getColor(R.color.status_rejected)
                else -> itemView.context.getColor(R.color.status_pending)
            }
            tvStatus.setTextColor(statusColor)
            val url = n.images.firstOrNull()?.url
            if (!url.isNullOrEmpty()) {
                Glide.with(itemView).load(url).centerCrop()
                    .placeholder(R.drawable.ic_placeholder)
                    .into(thumb)
            } else {
                // Use bundled raster drawable no_photo.png as placeholder
                thumb.setImageResource(R.drawable.no_photo)
                thumb.scaleType = ImageView.ScaleType.CENTER_INSIDE
                thumb.contentDescription = itemView.context.getString(R.string.notice_image_desc)
            }
            itemView.setOnClickListener { onClick(n) }
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<NoticeDto>() {
            override fun areItemsTheSame(a: NoticeDto, b: NoticeDto) = a.id == b.id
            override fun areContentsTheSame(a: NoticeDto, b: NoticeDto) = a == b
        }
    }
}
