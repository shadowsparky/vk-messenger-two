package ru.shadowsparky.messenger.messages_list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.shadowsparky.messenger.utils.Constansts
import ru.shadowsparky.messenger.utils.Logger

class ResponseReceiver(val presenter: MessagesList.Presenter, val log: Logger) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == Constansts.BROADCAST_RECEIVER_CODE) {
            val result = intent.getBooleanExtra("test", false)
            if (result)
                presenter.onScroll()
        }
    }
}