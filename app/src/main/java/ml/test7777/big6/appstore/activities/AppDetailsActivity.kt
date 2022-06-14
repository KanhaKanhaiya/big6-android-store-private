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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.ktx.storage
import ml.test7777.big6.appstore.R
import ml.test7777.big6.appstore.adapters.ScreenshotsAdapter
import ml.test7777.big6.appstore.custom.classes.App
import ml.test7777.big6.appstore.databinding.ActivityAppDetailsBinding
import org.apache.commons.codec.digest.DigestUtils
import java.io.*

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
        val binding = ActivityAppDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val user = Firebase.auth.currentUser

        if (user == null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else Firebase.crashlytics.setUserId(user.uid)

        appInstallPermissionResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                recheckInstallAppPermission(false)
            } else Toast.makeText(this, "Please grant permission to install apps. Error Code 4", Toast.LENGTH_LONG).show()

        }

        appUninstallResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                showOrHideAppUninstallDialog(binding)
            } else Toast.makeText(this, "An error occurred. Error Code 5", Toast.LENGTH_LONG).show()

        }

        appInstallResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                showOrHideInstallDialog(null)
            } else Toast.makeText(this, "An error occurred. Error Code 6", Toast.LENGTH_LONG).show()
        }

        app = intent.getSerializableExtra("APP") as App

        setUpLayout(binding)
    }

    private fun setUpLayout(binding: ActivityAppDetailsBinding) {
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
            installButtonClicked(binding)
        }
    }

    private fun installButtonClicked(binding: ActivityAppDetailsBinding) {
        if (binding.installButton.text == getString(R.string.install) || binding.installButton.text == getString(R.string.update)) {
            checkInstallPermission()
        } else if (binding.installButton.text == getString(R.string.open)) {
            val openAppIntent = packageManager.getLaunchIntentForPackage(app.packageName)
            if (openAppIntent !== null) {
                startActivity(openAppIntent)
            } else binding.installButton.text = getString(R.string.install)
        } else if (binding.installButton.text == getString(R.string.uninstall)) {
            checkInstallPermission()
            val uri = Uri.fromParts("package", app.packageName, null)
            val uninstallAppIntent = Intent(Intent.ACTION_DELETE, uri)
            appUninstallResultLauncher.launch(uninstallAppIntent)
        }
    }

    @SuppressLint("InflateParams")
    private fun showOrHideAppUninstallDialog(binding: ActivityAppDetailsBinding) {
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
                            try {
                                val size = localFile.length().toInt()
                                val array = ByteArray(size)
                                try {
                                    val inputStream = BufferedInputStream(FileInputStream(localFile))
                                    inputStream.read(array, 0, array.size)
                                    inputStream.close()
                                } catch (e: FileNotFoundException) {
                                    Toast.makeText(this, "File Not Found. Error Code 2", Toast.LENGTH_LONG).show()
                                    Firebase.crashlytics.recordException(e)
                                } catch (e: IOException) {
                                    Toast.makeText(this, "An Error Occurred. Error Code 3", Toast.LENGTH_LONG).show()
                                    Firebase.crashlytics.recordException(e)
                                }

                                val checksum = DigestUtils.sha512Hex(array).toString()

                                if (app.checksum == checksum) {
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(localFile.toUri(), "application/vnd.android.package-archive")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    try {
                                        appInstallResultLauncher.launch(intent)
                                    } catch (e: ActivityNotFoundException) {
                                        Toast.makeText(this, "File Not Found. Error Code 1", Toast.LENGTH_LONG).show()
                                        Firebase.crashlytics.recordException(e)
                                    }
                                } else Toast.makeText(this, "File Verification Error. Error Code 10", Toast.LENGTH_LONG).show()

                            } catch (e: Exception) {
                                Firebase.crashlytics.recordException(e)
                                Toast.makeText(this, "File Error. Error Code 9", Toast.LENGTH_LONG).show()
                            }

                        }
                    }
                }

            }.addOnFailureListener {
                Toast.makeText(this, "An Unknown Error Occurred. Error Code 7", Toast.LENGTH_LONG).show()
                Firebase.crashlytics.recordException(it)
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
            } catch (_: PackageManager.NameNotFoundException) {}
        }
        return getString(R.string.install)
    }
}
