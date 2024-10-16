package app.web.thebig6.store

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import app.web.thebig6.store.ui.theme.TheBig6StoreTheme
import app.web.thebig6.store.utils.isPackageInstalled
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.permissionx.guolindev.PermissionX

class MainActivity : FragmentActivity() {

    private var requiresUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheBig6StoreTheme {
                // A surface container using the 'background' color from the theme
                ActivityLayout()
            }
        }
        if (Firebase.auth.currentUser == null)
            startActivity(Intent(this, SignInActivity::class.java))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !this.packageManager.canRequestPackageInstalls()) {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:app.web.thebig6.store")
                )
            )
        }
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !this.packageManager.canRequestPackageInstalls()) {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:app.web.thebig6.store")
                )
            )
        }
    }

    private fun requestPermissions() {
        val list = mutableStateListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        list.add(Manifest.permission.REQUEST_INSTALL_PACKAGES)
        PermissionX.init(this).permissions(list.toList()).explainReasonBeforeRequest()
            .request { _, _, _ -> }
    }

    @Composable
    fun ActivityLayout() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(Modifier.safeDrawingPadding()) {
                val db = Firebase.firestore
                val appList = remember { mutableStateListOf<App>() }
                db.collection("AppStore").get().addOnSuccessListener { result ->
                    for (appObj in result) {
                        val app = appObj.toObject<App>()
                        if (app.packageName == "app.web.thebig6.store") {
                            if (app.versionCode != isPackageInstalled(
                                    app.packageName,
                                    this@MainActivity
                                ).second
                            ) {
                                requiresUpdate = true
                            }
                        }
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
                    if (!requiresUpdate)
                        AppRow(item)
                    else if (item.packageName == "app.web.thebig6.store")
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
                    val isPackageInstalledCheck =
                        isPackageInstalled(item.packageName, this@MainActivity)
                    mutableStateOf(
                        if (!isPackageInstalledCheck.first) "Install" else {
                            if (item.versionCode != isPackageInstalledCheck.second) "Update" else {
                                if (item.openable == "true") "Open" else "Installed"
                            }
                        }
                    )
                }
                Button(onClick = {
                    if (buttonText.value == "Install" || buttonText.value == "Update" || buttonText.value == "Installed")
                        startActivity(
                            Intent(
                                this@MainActivity,
                                AppDetailsActivity::class.java
                            ).putExtra("app", item)
                        )
                    else if (buttonText.value == "Open")
                        startActivity(packageManager.getLaunchIntentForPackage(item.packageName))
                }) {
                    Text(text = buttonText.value)
                    Icon(Icons.AutoMirrored.Sharp.ArrowForward, "Arrow Forward")
                }
            }
        }
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

