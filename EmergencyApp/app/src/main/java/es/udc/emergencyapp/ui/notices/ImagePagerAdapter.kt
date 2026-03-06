package es.udc.emergencyapp.ui.notices

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import es.udc.emergencyapp.R


class ImagePagerAdapter(private val ctx: Context, private val urls: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notice_image, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val url = if (urls.isEmpty()) null else urls[position]
        if (url == null) {
            // no images: show bundled placeholder image
            holder.image.scaleType = ImageView.ScaleType.CENTER_INSIDE
            Glide.with(ctx)
                .load(R.drawable.no_photo)
                .into(holder.image)
        } else {
            holder.image.scaleType = ImageView.ScaleType.CENTER_CROP
            Glide.with(ctx)
                .load(url)
                .placeholder(R.drawable.no_photo)
                .error(R.drawable.no_photo)
                .into(holder.image)
        }
    }

    override fun getItemCount() = if (urls.isEmpty()) 1 else urls.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.ivLarge)
    }
}
