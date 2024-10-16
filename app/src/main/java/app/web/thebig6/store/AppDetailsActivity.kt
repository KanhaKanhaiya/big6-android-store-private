package app.web.thebig6.store

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.IntentCompat
import app.web.thebig6.store.ui.theme.TheBig6StoreTheme
import app.web.thebig6.store.utils.installApp
import app.web.thebig6.store.utils.isPackageInstalled
import coil3.compose.AsyncImage

class AppDetailsActivity : ComponentActivity() {

    private var app: App? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheBig6StoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                    //   color = MaterialTheme.colorScheme.background
                ) {
                    app = IntentCompat.getSerializableExtra(intent, "app", App::class.java)
                    AppDetails()
                }
            }
        }
    }

    @Preview
    @Composable
    private fun AppDetails() {
        Column(
            Modifier
                .safeDrawingPadding()
                .padding(15.dp)
                .verticalScroll(rememberScrollState())) {
            Row {
                AsyncImage(
                    model = app!!.icon, "${app!!.name} Icon", modifier = Modifier
                        .clip(
                            RoundedCornerShape(20.dp)
                        )
                        .height(90.dp)
                )
                Column(Modifier.padding(start = 10.dp)) {
                    Text(
                        app!!.name,
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Cursive
                    )
                    Text(
                        app!!.oneLineDescription,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Thin,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }
            Spacer(Modifier.padding(15.dp))
            val buttonText = remember {
                val isPackageInstalledCheck = isPackageInstalled(
                    app!!.packageName,
                    context = this@AppDetailsActivity
                )
                mutableStateOf(
                    if (!isPackageInstalledCheck.first) "Install" else {
                        if (app!!.versionCode != isPackageInstalledCheck.second) "Update" else {
                            if (app!!.openable == "true") "Open" else "Installed"
                        }
                    }
                )
            }
            Button(onClick = {
                if (buttonText.value == "Install" || buttonText.value == "Update")
                    installApp(app!!, buttonText, this@AppDetailsActivity)
                else if (buttonText.value == "Open")
                    startActivity(packageManager.getLaunchIntentForPackage(app!!.packageName))
            }, modifier = Modifier.padding(bottom = 15.dp)) {
                Text(text = buttonText.value, modifier = Modifier.padding(horizontal = 50.dp))
            }
            HorizontalDivider(Modifier.padding(bottom = 20.dp))
            Text(
                "Screenshots",
                modifier = Modifier.padding(bottom = 5.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 20.dp)
            ) {
                app!!.screenshots!!.forEach { url ->
                    AsyncImage(
                        model = url,
                        "Screenshot of ${app!!.name}",
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clip(
                                RoundedCornerShape(15.dp)
                            )
                            .height(500.dp)
                    )
                }
            }
            Row {
                Text("Version : ", fontWeight = FontWeight.Light)
                Text(app!!.version)
            }
            Row {
                Text("Updated on : ", fontWeight = FontWeight.Light)
                Text(app!!.updatedOn)
            }
            Row {
                Text("Size : ", fontWeight = FontWeight.Light)
                Text(app!!.size)
            }
            Text(
                "What's New",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 25.dp, bottom = 10.dp)
            )
            Text(app!!.whatsNew)
            Text(
                "About ${app!!.name}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 25.dp, bottom = 10.dp)
            )
            Text(app!!.description)
        }
    }


}