package app.web.thebig6.store.utils

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.core.content.FileProvider
import androidx.core.content.pm.PackageInfoCompat
import app.web.thebig6.store.App
import com.koushikdutta.ion.Ion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.solrudev.ackpine.installer.InstallFailure
import ru.solrudev.ackpine.installer.PackageInstaller
import ru.solrudev.ackpine.installer.createSession
import ru.solrudev.ackpine.session.SessionResult
import ru.solrudev.ackpine.session.await
import ru.solrudev.ackpine.session.parameters.Confirmation
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

fun isPackageInstalled(name: String, context: Context): Pair<Boolean, Long> {
    var result = false
    var versionCode = 0L
    try {
        val packageInfo =
            context.packageManager.getPackageInfo(name, PackageManager.GET_ACTIVITIES)
        versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
        result = true
    } catch (_: PackageManager.NameNotFoundException) {
    }

    return Pair(result, versionCode)
}

fun installApp(app: App, buttonText: MutableState<String>, context: Context) {
    val packageInstaller = PackageInstaller.getInstance(context)

    buttonText.value = "Downloading"
    val localAPK = File(context.filesDir, "APKs/${app.packageName}.apk")

    val ur = FileProvider.getUriForFile(
        context,
        "app.web.thebig6.store.fileprovider",
        localAPK
    )

    Ion.with(context)
        .load(app.url)
        .write(localAPK)
        .setCallback { e, file ->
            if (e == null) {
                Toast.makeText(context, file.path, Toast.LENGTH_LONG).show()
                val session = packageInstaller.createSession(ur) {
                    confirmation = Confirmation.IMMEDIATE
                    requireUserAction = false
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        buttonText.value = "Installing"
                        when (val result = session.await()) {
                            is SessionResult.Success -> buttonText.value =
                                if (app.openable == "true") "Open" else "Installed"

                            is SessionResult.Error -> {
                                val error = when (val failure = result.cause) {
                                    is InstallFailure.Aborted -> "Aborted"
                                    is InstallFailure.Blocked -> "Blocked by ${failure.otherPackageName}"
                                    is InstallFailure.Conflict -> "Conflicting with ${failure.otherPackageName}"
                                    is InstallFailure.Exceptional -> failure.exception.message
                                    is InstallFailure.Generic -> "Generic failure"
                                    is InstallFailure.Incompatible -> "Incompatible"
                                    is InstallFailure.Invalid -> "Invalid"
                                    is InstallFailure.Storage -> "Storage path: ${failure.storagePath}"
                                    is InstallFailure.Timeout -> "Timeout"
                                }
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            }

                        }
                    } catch (_: CancellationException) {
                        Toast.makeText(
                            context,
                            "Cancelled Installation",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (exception: Exception) {
                        Toast.makeText(
                            context,
                            "An error occurred",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    localAPK.delete()
}