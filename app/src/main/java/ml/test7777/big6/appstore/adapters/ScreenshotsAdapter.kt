package ml.test7777.big6.appstore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ceylonlabs.imageviewpopup.ImagePopup
import ml.test7777.big6.appstore.R

class ScreenshotsAdapter (private val imagesList: List<String>, private val context: Context) : RecyclerView.Adapter<ScreenshotsAdapter.ViewHolder>() {

    inner class ViewHolder(screenshotView: View) : RecyclerView.ViewHolder(screenshotView) {
        val screenshotImageView: ImageView = screenshotView.findViewById(R.id.screenshotImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val appList = inflater.inflate(R.layout.screenshot, parent, false)
        return ViewHolder(appList)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imagesList[position]
        val screenshotImageView = holder.screenshotImageView

        Glide
            .with(context)
            .load(image)
            .into(screenshotImageView)

        val imagePopup = ImagePopup(context)
        imagePopup.initiatePopup(screenshotImageView.drawable)
        screenshotImageView.setOnClickListener {
            imagePopup.viewPopup()
        }
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

}