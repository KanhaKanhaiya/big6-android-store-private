package app.web.thebig6.store.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.messaging.FirebaseMessagingService

private lateinit var database: DatabaseReference

class NotificationsService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        database = Firebase.database.reference
        database.child("AppStore").push().setValue(token)
    }
}