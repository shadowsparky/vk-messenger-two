package ru.shadowsparky.messenger.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

class FirebaseMessage : FirebaseMessagingService() {
    @Inject
    lateinit var log: Logger
    init {
        App.component.inject(this)
    }
    override fun onMessageReceived(p0: RemoteMessage?) {
        log.print("NEW MESSAGE: ${p0.toString()}")
        super.onMessageReceived(p0)
    }
}
