package app.web.thebig6.appstore

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
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
import app.web.thebig6.appstore.ui.theme.TheBig6ProjectAppStoreTheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.io.File

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheBig6ProjectAppStoreTheme {
                // A surface container using the 'background' color from the theme
                ActivityLayout()
            }
        }

    }


    @SuppressLint("UnrememberedMutableState")
//@Preview(showBackground = true)
    @Composable
    fun ActivityLayout() {
        TheBig6ProjectAppStoreTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    val db = Firebase.firestore
                    val appList = mutableStateListOf<App>()
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
    }

    @Composable
    fun AppList(appList: SnapshotStateList<App>) {
        Column {
            LazyColumn {
                itemsIndexed(appList) { index, item ->
                    AppRow(index, item)
                }
            }
        }
    }

    @Composable
    fun AppRow(index: Int, item: App) {
        Card(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = "Test", fontSize = 25.sp)
                Spacer(modifier = Modifier.height(5.dp))
                val buttonText = remember {
                    mutableStateOf("Install")
                }
                Button(onClick = {
                    installApp(item.packageName, buttonText)
                }) {
                    Text(text = buttonText.value)
                }
            }
        }
    }

    fun installApp(packageName: String, buttonText: MutableState<String>) {
        buttonText.value = "Starting"
        val localapk = File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "test.apk")


        val req = DownloadManager.Request(Uri.parse("https://github.com/zhanghai/MaterialFiles/releases/download/v1.7.4/app-release-universal.apk"))
        req.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "test.apk")

        val downloadManager: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(req)

        val ur = FileProvider.getUriForFile(
            this,
            "app.web.thebig6.appstore.fileprovider",
            localapk
        )
            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
            intent.setDataAndType(
                ur, "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(intent)
        //}.addOnProgressListener {
        //    buttonText.value = ((it.bytesTransferred / it.totalByteCount) * 100).toString()
        //}
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

