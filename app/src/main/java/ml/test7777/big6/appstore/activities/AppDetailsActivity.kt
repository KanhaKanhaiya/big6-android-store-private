package ml.test7777.big6.appstore.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ml.test7777.big6.appstore.adapters.ScreenshotsAdapter
import ml.test7777.big6.appstore.custom.App
import ml.test7777.big6.appstore.databinding.ActivityAppDetailsBinding

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityAppDetailsBinding

class AppDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val app = intent.getSerializableExtra("APP") as App

        setUpLayout(app)
    }

    private fun setUpLayout(app: App) {
        binding.appDetailsNameTextView.text = app.name
        binding.appDetailsDescriptionTextView.text = app.description
        binding.appDetailsDownloadSizeTextView.text = app.size
        binding.appDetailsUpdatedOnTextView.text = app.updatedOn
        binding.appDetailsVersionTextView.text = app.version
        binding.appDetailsWhatsNewTextView.text = app.whatsNew
        binding.installButton.text = installButtonText(app)

        val adapter = ScreenshotsAdapter(app.screenshots, this)

        binding.appDetailsScreenshotRecyclerView.adapter = adapter
        binding.appDetailsScreenshotRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.appDetailsScreenshotRecyclerView.setHasFixedSize(true)
    }

    private fun installButtonText(app: App) : String {
        val packageManager = this.packageManager
        val intent = Intent(Intent.ACTION_VIEW)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                val appInfo = packageManager.getPackageInfo(app.packageName, PackageManager.GET_ACTIVITIES)
                return if (appInfo.versionName === app.version) {
                    "Open"
                } else {
                    "Update"
                }
            } catch (e: PackageManager.NameNotFoundException) {}
        }
        return "Install"
    }
}