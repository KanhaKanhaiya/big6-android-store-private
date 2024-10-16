package app.web.thebig6.store.services

import com.google.firebase.database.DatabaseReference
import com.google.firebase.messaging.FirebaseMessagingService

private lateinit var database: DatabaseReference

class NotificationsService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}