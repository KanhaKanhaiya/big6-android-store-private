package ml.test7777.big6.appstore.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ml.test7777.big6.appstore.activities.ui.theme.TheBig6ProjectAppStoreTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SettingsLayout()
        }
    }
}

@Composable
fun SettingsLayout() {
    TheBig6ProjectAppStoreTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SettingsButton(text = "Your Account", onClick = {  })
            SettingsButton(text = "Help", onClick = {  })
            SettingsButton(text = "Change Language", onClick = {  })
        }
    }
}

@Composable
fun SettingsButton(text: String, onClick: () -> Unit) {
    Button(onClick = { onClick() }, modifier = Modifier.height(10.dp)) {
        Text(text = text)
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun DefaultPreview() {
    SettingsLayout()
}