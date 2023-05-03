package app.web.thebig6.appstore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.bumptech.glide.Glide
import app.web.thebig6.appstore.R
import app.web.thebig6.appstore.custom.classes.App
import app.web.thebig6.appstore.onClicks.AppListRecyclerView

class AppListAdapter (private val appList: List<App>, private val activityContext: Context, private val recyclerView: RecyclerView) : RecyclerView.Adapter<AppListAdapter.ViewHolder?>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIconImageView: ImageView = itemView.findViewById(R.id.appIconImageView)
        val appNameTextView: TextView = itemView.findViewById(R.id.appNameTextView)
        val appDescriptionTextView: TextView = itemView.findViewById(R.id.appDescriptionTextView)
        val appSizeTextView: TextView = itemView.findViewById(R.id.appSizeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val appListView = inflater.inflate(R.layout.applist, parent, false)
        appListView.setOnClickListener(AppListRecyclerView(appList, recyclerView, activityContext))
        return ViewHolder(appListView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = appList[position]

        val appIconImageView = holder.appIconImageView
        val appNameTextView = holder.appNameTextView
        val appDescriptionTextView = holder.appDescriptionTextView
        val appSizeTextView = holder.appSizeTextView

        Glide
            .with(activityContext)
            .load(app.icon)
            .into(appIconImageView)

        appNameTextView.text = app.name
        appDescriptionTextView.text = app.oneLineDescription
        appSizeTextView.text = app.size
    }

    override fun getItemCount(): Int {
        return appList.size
    }
}
