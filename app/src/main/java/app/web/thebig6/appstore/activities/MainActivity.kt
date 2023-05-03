package app.web.thebig6.appstore.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import app.web.thebig6.appstore.R
import app.web.thebig6.appstore.adapters.AppListAdapter
import app.web.thebig6.appstore.custom.classes.App
import app.web.thebig6.appstore.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding
private lateinit var appsList: MutableList<App>

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        val user = Firebase.auth.currentUser

        if (user != null) {
            Firebase.crashlytics.setUserId(user.uid)
        }

        getData()
        // loginAndSignUp()
    }

    private fun loginAndSignUp() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            getData()
        } else {
            val signInLauncher = registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
            ) { res ->
                this.onSignInResult(res)
            }

            val providers = arrayListOf(
                AuthUI.IdpConfig.GoogleBuilder().build())

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.mipmap.ic_launcher)
                .setTheme(R.style.TheBig6ProjectAppStoreTheme)
                .setTosAndPrivacyPolicyUrls(
                    "https://big6.test7777.ml/android/terms-of-service/",
                    "https://big6.test7777.ml/android/privacy-policy/"
                )
                .build()

            signInLauncher.launch(signInIntent)
        }
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                getData()
            } else {
                Toast.makeText(this, "Please Login or SignUp", Toast.LENGTH_LONG).show()
                loginAndSignUp()
            }
        } else {

            if (response == null) {
                Toast.makeText(this, "Please Login or SignUp", Toast.LENGTH_LONG).show()
                loginAndSignUp()
            }

            if (response?.error?.errorCode  == ErrorCodes.NO_NETWORK) {
                Toast.makeText(this, "Please check your internet connection and try again", Toast.LENGTH_LONG).show()
                loginAndSignUp()
            }

            if (response?.error?.errorCode  == ErrorCodes.UNKNOWN_ERROR) {
                Toast.makeText(this, "Unknown error occurred. Please try again. Error Code 8", Toast.LENGTH_LONG).show()
                loginAndSignUp()
            }

        }

    }

    private fun getData() {
        val collectionRef = Firebase.firestore.collection("AppStore")
        collectionRef.get()
            .addOnSuccessListener { result ->

                appsList = ArrayList()

                for (document in result) {
                    appsList.add(document.toObject())
                }

                val adapter = AppListAdapter(appsList, this, binding.appsListRecyclerView)

                binding.appsListRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.appsListRecyclerView.setHasFixedSize(true)
                binding.appsListRecyclerView.adapter = adapter
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
