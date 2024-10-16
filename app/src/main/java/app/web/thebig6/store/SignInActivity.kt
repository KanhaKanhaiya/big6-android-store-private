package app.web.thebig6.store

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import app.web.thebig6.store.ui.theme.TheBig6StoreTheme
import com.google.android.gms.auth.api.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheBig6StoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ActivityLayout(innerPadding)
                }
            }
        }
        Firebase.initialize(this)
        if (Firebase.auth.currentUser != null)
            startActivity(Intent(this@SignInActivity, MainActivity::class.java))
    }

    @Composable
    fun ActivityLayout(padding: PaddingValues) {
        Column(
            Modifier
                .padding(padding)
                .safeDrawingPadding()
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(onClick = {
                login()
            }, shape = RoundedCornerShape(5.dp)) {
                Image(painter = painterResource(R.drawable.googleg_standard_color_18), null)
                Text("Sign in with Google", Modifier.padding(5.dp))
            }
        }
    }


    private fun login() {
        if (Firebase.auth.currentUser == null) {
            val credentialManager = CredentialManager.create(this)
            val getSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(
                serverClientId = "296051853956-i1qq0hm42anqivkt7uu8ogloik4uigo1.apps.googleusercontent.com"
            )
                .build()
            val request: GetCredentialRequest =
                GetCredentialRequest.Builder().addCredentialOption(getSignInWithGoogleOption)
                    .build()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = this@SignInActivity,
                    )
                    when (val credential = result.credential) {

                        // Passkey credential
//                        is PublicKeyCredential -> {
//                            // Share responseJson such as a GetCredentialResponse on your server to
//                            // validate and authenticate
//                            responseJson = credential.authenticationResponseJson
//                        }

                        // Password credential
//                        is PasswordCredential -> {
//                            // Send ID and password to your server to validate and authenticate.
//                            val username = credential.id
//                            val password = credential.password
//                        }

                        // GoogleIdToken credential
                        is CustomCredential -> {
                            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                try {
                                    // Use googleIdTokenCredential and extract the ID to validate and
                                    // authenticate on your server.
                                    val googleIdTokenCredential = GoogleIdTokenCredential
                                        .createFrom(credential.data)
                                    Firebase.auth.signInWithCredential(
                                        GoogleAuthProvider.getCredential(
                                            googleIdTokenCredential.idToken,
                                            null
                                        )
                                    ).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            if (Firebase.auth.currentUser != null)
                                                startActivity(
                                                    Intent(
                                                        this@SignInActivity,
                                                        MainActivity::class.java
                                                    )
                                                )
                                        }
                                    }
                                    // You can use the members of googleIdTokenCredential directly for UX
                                    // purposes, but don't use them to store or control access to user
                                    // data. For that you first need to validate the token:
//                                    // pass googleIdTokenCredential.getIdToken() to the backend server.
//                                    GoogleIdTokenVerifier verifier = ... // see validation instructions
//                                    val idToken = verifier.verify(idTokenString);
                                    // To get a stable account identifier (e.g. for storing user data),
                                    // use the subject ID:
                                    //idToken.getPayload().getSubject()
                                } catch (e: GoogleIdTokenParsingException) {
                                    // Log.e(TAG, "Received an invalid google id token response", e)
                                }
                            } else {
                                // Catch any unrecognized custom credential type here.
                                // Log.e(TAG, "Unexpected type of credential")
                            }
                        }

                        else -> {
                            // Catch any unrecognized credential type here.
                            //     Log.e(TAG, "Unexpected type of credential")
                        }
                    }
                } catch (e: GetCredentialException) {
                    // handleFailure(e)
                }
            }
        }
    }
}