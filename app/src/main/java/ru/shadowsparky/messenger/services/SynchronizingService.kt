package ru.shadowsparky.messenger.services

import android.app.IntentService
import android.content.Intent
import ru.shadowsparky.messenger.utils.Constansts.Companion.BROADCAST_RECEIVER_CODE

class SynchronizingService(val name: String) : IntentService(name) {

    override fun onHandleIntent(intent: Intent?) {
        val broadcast = Intent()
        broadcast.action = BROADCAST_RECEIVER_CODE
        sendBroadcast(broadcast)
    }
}
