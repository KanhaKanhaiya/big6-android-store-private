package ml.test7777.big6.appstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import ml.test7777.big6.appstore.R
import ml.test7777.big6.appstore.custom.App

class AppListAdapter (private val appList: List<App>) : RecyclerView.Adapter<AppListAdapter.ViewHolder?>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appIconImageView: ImageView = itemView.findViewById(R.id.appIconImageView)
        val appNameTextView: TextView = itemView.findViewById(R.id.appNameTextView)
        val appDescriptionTextView: TextView = itemView.findViewById(R.id.appDescriptionTextView)
        val appSizeTextView: TextView = itemView.findViewById(R.id.appSizeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val appList = inflater.inflate(R.layout.applist, parent, false)
        return ViewHolder(appList)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = appList[position]

        val appIconImageView = holder.appIconImageView
        val appNameTextView = holder.appNameTextView
        val appDescriptionTextView = holder.appDescriptionTextView
        val appSizeTextView = holder.appSizeTextView

        appNameTextView.text = app.name
        appDescriptionTextView.text = app.oneLineDescription
        appSizeTextView.text = app.size

        TODO("Add Glide for Image Loading")
    }

    override fun getItemCount(): Int {
        return appList.size
    }
}