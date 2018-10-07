package ru.shadowsparky.messenger.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class FirebaseMessage : FirebaseMessagingService() {
    @Inject lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject lateinit var log: Logger

    init {
        App.component.inject(this)
    }

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)
        p0?.let {
            preferencesUtils.write(Constansts.FIREBASE_TOKEN, it)
            log.print("NEW FIREBASE TOKEN HANDLED: $it")
        }
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        log.print("NEW MESSAGE: ${p0.toString()}")
        super.onMessageReceived(p0)
    }
}
