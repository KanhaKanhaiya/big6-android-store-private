package app.web.thebig6.appstore.onClicks

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.web.thebig6.appstore.activities.AppDetailsActivity
import app.web.thebig6.appstore.custom.classes.App

class AppListRecyclerView (private val appsList: List<App>, private val recyclerView: RecyclerView, private val context: Context) : View.OnClickListener {
    override fun onClick(p0: View?) {
        val itemPosition = recyclerView.indexOfChild(p0)
        val app = appsList[itemPosition]
        val intent = Intent(context, AppDetailsActivity::class.java)
        intent.putExtra("APP", app)
        context.startActivity(intent)
    }
}
