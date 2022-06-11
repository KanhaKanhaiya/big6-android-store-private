package ml.test7777.big6.appstore.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.ktx.storage
import ml.test7777.big6.appstore.R
import ml.test7777.big6.appstore.adapters.ScreenshotsAdapter
import ml.test7777.big6.appstore.custom.App
import ml.test7777.big6.appstore.databinding.ActivityAppDetailsBinding
import java.io.File

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityAppDetailsBinding
private lateinit var app: App

private var downloadAndInstallAlertDialog: AlertDialog? = null
private var uninstallAlertDialog: AlertDialog? = null

val storage = Firebase.storage

private lateinit var appInstallPermissionResultLauncher: ActivityResultLauncher<Intent>
private lateinit var appUninstallResultLauncher: ActivityResultLauncher<Intent>
private lateinit var appInstallResultLauncher: ActivityResultLauncher<Intent>

class AppDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        appInstallPermissionResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                recheckInstallAppPermission(false)
            } else TODO("Log and show error")
        }

        appUninstallResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                showOrHideAppUninstallDialog()
            } else TODO("Log and show error")
        }

        appInstallResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                showOrHideInstallDialog(null)
            } else TODO("Log and show error")
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
        if (binding.installButton.text == getString(R.string.install) || binding.installButton.text === getString(R.string.update)) {
            checkInstallPermission()
        } else if (binding.installButton.text == getString(R.string.open)) {
            val openAppIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (openAppIntent !== null) {
                startActivity(openAppIntent)
            } else binding.installButton.text = getString(R.string.install)
        } else if (binding.installButton.text == getString(R.string.uninstall)) {
            val uri = Uri.fromParts("package", app.packageName, null)
            val uninstallAppIntent = Intent(Intent.ACTION_DELETE, uri)
            appUninstallResultLauncher.launch(uninstallAppIntent)
        }
    }

    @SuppressLint("InflateParams")
    private fun showOrHideAppUninstallDialog() {
        if (uninstallAlertDialog == null) {
            val builder: AlertDialog.Builder = this.let {
                AlertDialog.Builder(it)
            }

            builder.setTitle(R.string.uninstalling)

            val inflater = this.layoutInflater

            builder.setView(inflater.inflate(R.layout.installing_uninstalling_dialog, null))

            builder.setCancelable(false)

            uninstallAlertDialog = builder.show()
        } else {
            uninstallAlertDialog!!.hide()
            uninstallAlertDialog = null

            binding.installButton.text = getString(R.string.install)
        }
    }

    private fun checkInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.data = Uri.parse("package:$packageName")
                appInstallPermissionResultLauncher.launch(intent)
            } else recheckInstallAppPermission(true)
        }
    }

    private fun recheckInstallAppPermission(isPermissionGranted: Boolean) {
        if (isPermissionGranted) {
            installApp()
        } else checkInstallPermission()
    }

    @SuppressLint("InflateParams")
    private fun installApp() {
        val storageRef = storage.reference
        val pathReference = storageRef.child("apks/${app.packageName}")

        val localFolder = File(filesDir, "apks")

        if (!localFolder.exists()) {
            localFolder.mkdirs()
        } else {
            val localFile = File(localFolder, "${app.packageName}.apk")
            val task = pathReference.getFile(localFile)

            task.addOnSuccessListener {

                val alertDialog = showOrHideInstallDialog(task)

                val progressBar: LinearProgressIndicator? = alertDialog?.findViewById(R.id.installProgressIndicator)
                if (progressBar != null) {
                    progressBar.progress = (it.bytesTransferred / it.totalByteCount).toInt() * 100
                }

                if (progressBar != null) {
                    if (progressBar.progress == 100) {
                        alertDialog.setTitle(R.string.installing)
                        alertDialog.setView(layoutInflater.inflate(R.layout.installing_uninstalling_dialog, null))
                        if (localFile.exists()) {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(localFile.toUri(), "application/vnd.android.package-archive")
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            try {
                                applicationContext.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                Toast.makeText(this, "APK File Not Found", Toast.LENGTH_LONG).show()
                                TODO("Show a better error message and log to Crashlytics")
                            }
                        }
                    }
                }

            }.addOnFailureListener {
                TODO("Log to Crashlytics")
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showOrHideInstallDialog(task: FileDownloadTask?): AlertDialog? {
        if (downloadAndInstallAlertDialog == null && task != null) {
            val builder: AlertDialog.Builder = this.let {
                AlertDialog.Builder(it)
            }

            builder.setTitle(R.string.downloading)

            val inflater = this.layoutInflater

            builder.setView(inflater.inflate(R.layout.install_progress, null))

            builder.apply {
                setNegativeButton(R.string.cancel
                ) { dialog, _ ->
                    task.cancel()
                    dialog.dismiss()
                }
            }

            builder.setCancelable(false)

            downloadAndInstallAlertDialog = builder.show()

            return downloadAndInstallAlertDialog
        } else {
            downloadAndInstallAlertDialog!!.hide()
            downloadAndInstallAlertDialog = null
        }

        return null
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