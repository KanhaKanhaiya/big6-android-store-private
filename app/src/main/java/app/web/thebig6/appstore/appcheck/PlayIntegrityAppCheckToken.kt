package app.web.thebig6.appstore.appcheck

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.android.play.core.integrity.StandardIntegrityManager.PrepareIntegrityTokenRequest
import com.google.android.play.core.integrity.StandardIntegrityManager.StandardIntegrityTokenProvider
import com.google.android.play.core.integrity.StandardIntegrityManager.StandardIntegrityTokenRequest
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.AppCheckProvider
import com.google.firebase.appcheck.AppCheckToken
import kotlinx.coroutines.CompletableDeferred


class PlayIntegrityAppCheckToken(
    private val token: String,
    private val expiration: Long,
) : AppCheckToken() {
    override fun getToken(): String = token
    override fun getExpireTimeMillis(): Long = expiration
}

class PlayIntegrityAppCheckProvider(firebaseApp: FirebaseApp, appContext: Context) :
    AppCheckProvider {
    private val context = appContext
    override fun getToken(): Task<AppCheckToken> {
        // Logic to exchange proof of authenticity for an App Check token and
        //   expiration time.
        // ...

        //val def = CompletableDeferred<Task<AppCheckToken>>(retrieveToken())

        return retrieveToken()

    }

    private fun retrieveToken(): Task<AppCheckToken> {
        val standardIntegrityManager: StandardIntegrityManager =
            IntegrityManagerFactory.createStandard(context)
        val cloudProjectNumber = 296051853956;

        var integrityTokenProvider: StandardIntegrityTokenProvider

        var token: AppCheckToken? = null

        standardIntegrityManager.prepareIntegrityToken(
            PrepareIntegrityTokenRequest.builder()
                .setCloudProjectNumber(cloudProjectNumber)
                .build()
        )
            .addOnSuccessListener { tokenProvider: StandardIntegrityTokenProvider ->
                integrityTokenProvider = tokenProvider

                val integrityTokenResponse = integrityTokenProvider.request(
                    StandardIntegrityTokenRequest.builder()
                        .build()
                )

                integrityTokenResponse.addOnSuccessListener { response ->
                    // Refresh the token early to handle clock skew.
                    val expMillis = 60000L

                    // Create AppCheckToken object.
                    val appCheckToken: AppCheckToken =
                        PlayIntegrityAppCheckToken(response.token(), expMillis)
                    token = appCheckToken
                    // return@addOnSuccessListener Tasks.forResult(appCheckToken)
                }
            }
            .addOnFailureListener { exception: Exception? -> }

//        integrityTokenProvider.request()

        return Tasks.forResult(token)
    }
}