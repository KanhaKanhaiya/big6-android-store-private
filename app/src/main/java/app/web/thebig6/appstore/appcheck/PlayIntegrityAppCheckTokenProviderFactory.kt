package app.web.thebig6.appstore.appcheck

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.AppCheckProvider
import com.google.firebase.appcheck.AppCheckProviderFactory

class PlayIntegrityAppCheckTokenProviderFactory(appContext: Context)  : AppCheckProviderFactory {

    private val context = appContext
    override fun create(firebaseApp: FirebaseApp): AppCheckProvider {
        // Create and return an AppCheckProvider object.
        return PlayIntegrityAppCheckProvider(firebaseApp, context)
    }
}