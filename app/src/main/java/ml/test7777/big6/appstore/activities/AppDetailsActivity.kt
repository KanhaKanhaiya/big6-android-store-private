package ml.test7777.big6.appstore.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import ml.test7777.big6.appstore.R
import ml.test7777.big6.appstore.adapters.ScreenshotsAdapter
import ml.test7777.big6.appstore.custom.App
import ml.test7777.big6.appstore.databinding.ActivityAppDetailsBinding

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityAppDetailsBinding
private lateinit var app: App

private lateinit var appInstallPermissionResultLauncher: ActivityResultLauncher<Intent>

class AppDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        appInstallPermissionResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data

            }
        }
        app = intent.getSerializableExtra("APP") as App

        setUpLayout()
    }

    private fun setUpLayout() {
        binding.appDetailsNameTextView.text = app.name
        binding.appDetailsDescriptionTextView.text = app.description
        binding.appDetailsDownloadSizeTextView.text = app.size
        binding.appDetailsUpdatedOnTextView.text = app.updatedOn
        binding.appDetailsVersionTextView.text = app.version
        binding.appDetailsWhatsNewTextView.text = app.whatsNew
        binding.installButton.text = installButtonText()

        val adapter = ScreenshotsAdapter(app.screenshots, this)

        binding.appDetailsScreenshotRecyclerView.adapter = adapter
        binding.appDetailsScreenshotRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.appDetailsScreenshotRecyclerView.setHasFixedSize(true)

        binding.installButton.setOnClickListener {
            installButtonClicked()
        }
    }

    private fun installButtonClicked() {
        if (binding.installButton.text === getString(R.string.install) || binding.installButton.text === getString(R.string.update)) {
            installApp()
        } else if (binding.installButton.text === getString(R.string.open)) {
            val openAppIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (openAppIntent !== null) {
                startActivity(openAppIntent)
            } else binding.installButton.text = getString(R.string.install)
        } else if (binding.installButton.text === getString(R.string.uninstall)) {
            TODO()
        }
    }

    private fun installApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.data = Uri.parse("package:$packageName")
            }
        }
    }

    private fun installButtonText() : String {
        val packageManager = this.packageManager
        val intent = Intent(Intent.ACTION_VIEW)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                val appInfo = packageManager.getPackageInfo(app.packageName, PackageManager.GET_ACTIVITIES)
                return if (appInfo.versionName === app.version) {
                    if (app.isOpenable) {
                        getString(R.string.open)
                    } else getString(R.string.uninstall)
                } else {
                    getString(R.string.update)
                }
            } catch (e: PackageManager.NameNotFoundException) {}
        }
        return getString(R.string.install)
    }
}