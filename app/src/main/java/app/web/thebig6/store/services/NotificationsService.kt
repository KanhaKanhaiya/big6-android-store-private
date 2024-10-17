package app.web.thebig6.store.services

import com.google.firebase.messaging.FirebaseMessagingService

class NotificationsService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}