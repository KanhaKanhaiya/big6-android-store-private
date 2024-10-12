package app.web.thebig6.appstore

import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import app.web.thebig6.appstore.ui.theme.TheBig6StoreTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.initialize
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch
import ru.solrudev.ackpine.installer.PackageInstaller
import ru.solrudev.ackpine.installer.createSession
import ru.solrudev.ackpine.session.SessionResult
import ru.solrudev.ackpine.session.await
import ru.solrudev.ackpine.session.parameters.Confirmation
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheBig6StoreTheme {
                // A surface container using the 'background' color from the theme
                ActivityLayout()
            }
        }
        Firebase.initialize(this)
        requestPermissions()
    }

    private fun requestPermissions() {
        val list = mutableStateListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            list.add(android.Manifest.permission.REQUEST_INSTALL_PACKAGES)
        }
        PermissionX.init(this).permissions(list.toList()).explainReasonBeforeRequest()
            .request { _, _, _ -> }
    }

    @Composable
    fun ActivityLayout() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                val db = Firebase.firestore
                val appList = remember { mutableStateListOf<App>() }
                db.collection("AppStore").get().addOnSuccessListener { result ->
                    for (appObj in result) {
                        val app = appObj.toObject<App>()
                        appList.add(app)
                    }
                }
                AppList(appList)
            }
        }
    }

    @Composable
    fun AppList(appList: SnapshotStateList<App>) {
        Column {
            LazyColumn {
                itemsIndexed(appList) { _, item ->
                    AppRow(item)
                }
            }
        }
    }

    @Composable
    fun AppRow(item: App) {
        Card(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = item.name, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(5.dp))
                val buttonText = remember {
                    val isPackageInstalledCheck = isPackageInstalled(item.packageName)
                    //Toast.makeText(this@MainActivity, item.openable, Toast.LENGTH_LONG).show()
                    mutableStateOf(if (!isPackageInstalledCheck.first) "Install" else { if (item.versionCode != isPackageInstalledCheck.second) "Update" else { if (item.openable == "true") "Open" else "Installed" }})
                }
                Button(onClick = {
                    if (buttonText.value == "Install" || buttonText.value == "Update")
                    lifecycleScope.launch {
                        installApp(item.url, buttonText)
                    }
                    else if (buttonText.value == "Open")
                        startActivity(packageManager.getLaunchIntentForPackage(item.packageName))
                }) {
                    Text(text = buttonText.value)
                }
            }
        }
    }

    private fun isPackageInstalled(name: String) : Pair<Boolean, Long> {
        var result = false
        var versionCode = 0L
        try {
            val packageInfo = this.packageManager.getPackageInfo(name, PackageManager.GET_ACTIVITIES)
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else packageInfo.versionCode.toLong()
            result = true
        } catch (_: PackageManager.NameNotFoundException) {}

        return Pair(result, versionCode)
    }

    private suspend fun installApp(packageName: String, buttonText: MutableState<String>) {

        val packageInstaller = PackageInstaller.getInstance(this@MainActivity)

        buttonText.value = "Starting Download"
        val localAPK = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "test.apk")

        val req =
            DownloadManager.Request(Uri.parse("https://github.com/zhanghai/MaterialFiles/releases/download/v1.7.4/app-release-universal.apk"))
        req.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "test.apk")

        val downloadManager: DownloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(req)

        val ur = FileProvider.getUriForFile(
            this,
            "app.web.thebig6.appstore.fileprovider",
            localAPK
        )

        val session = packageInstaller.createSession(ur) {
            confirmation = Confirmation.IMMEDIATE
            requireUserAction = false
        }

        try {
            when (val result = session.await()) {
                is SessionResult.Success -> Toast.makeText(
                    this@MainActivity,
                    result.toString(),
                    Toast.LENGTH_LONG
                ).show()

                is SessionResult.Error -> Toast.makeText(
                    this@MainActivity,
                    result.cause.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (_: CancellationException) {
            println("Cancelled")
        } catch (exception: Exception) {
            println(exception)
        }


//            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
//            intent.setDataAndType(
//                ur, "application/vnd.android.package-archive"
//            )
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//
//        startActivity(intent)
        //}.addOnProgressListener {
        //    buttonText.value = ((it.bytesTransferred / it.totalByteCount) * 100).toString()
        //}

        localAPK.delete()
    }

    @Preview
    @Composable
    fun AppRowPreview(/*index: Int, item: App*/) {
        Card(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = "Test", fontSize = 25.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Install")
                }
            }
        }
    }

}

