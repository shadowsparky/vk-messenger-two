package ru.shadowsparky.messenger.messages_view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.shadowsparky.messenger.response_utils.pojos.VKGetByIDMessages
import ru.shadowsparky.messenger.utils.Constansts
import ru.shadowsparky.messenger.utils.DateUtils
import ru.shadowsparky.messenger.utils.Logger
import kotlin.math.abs

class ResponseReceiver(
        private val view: Messages.View,
        private val presenter: Messages.Presenter,
        private val log: Logger,
        private val userId: Int
) : BroadcastReceiver() {
    private var mUpdateFlag = false
    private val TAG = javaClass.name

    private fun receiveLongPoll(mResponse: VKGetByIDMessages) {
        mUpdateFlag = false
        if (mResponse.profiles != null) {
            for (item in mResponse.profiles) {
                if (item.id == userId) {
                    mUpdateFlag = true
                }
            }
        }
        if (mResponse.items != null) {
            log.print(mResponse.items.toString())
            for (item in mResponse.items) {
                if (item != null)
                    if (item.peer_id == userId) {
                        mUpdateFlag = true
                    }
            }
        }
        if (mResponse.groups != null) {
            for (item in mResponse.groups) {
                if (abs(item.id) == abs(userId)) {
                    mUpdateFlag = true
                }
            }
        }
        if (mUpdateFlag) {
            log.print("MessageHistoryRequest Long Poll Handled")
            presenter.onGetMessageHistoryRequest()
        }
    }

    private fun receiveUserReadMessage(peer_id: Int) {
        if (peer_id == this.userId) {
            presenter.onGetMessageHistoryRequest()
            log.print("Receive user read message")
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == Constansts.BROADCAST_RECEIVER_CODE) {
            val mResponse = intent.getSerializableExtra(Constansts.RESPONSE)
            when (mResponse) {
                is VKGetByIDMessages -> receiveLongPoll(mResponse)
                is Int -> receiveUserReadMessage(mResponse)
            }
            val onlineStatusChanged = intent.getIntExtra(Constansts.USER_LONG_POLL_STATUS_CHANGED, Constansts.STATUS_HIDE)
            val userId = intent.getIntExtra(Constansts.USER_ID, -1)
            if (onlineStatusChanged == Constansts.STATUS_ONLINE) {
                if (userId == this.userId) {
                    view.setStatus("${Constansts.ONLINE}")
                }
            } else if (onlineStatusChanged == Constansts.STATUS_OFFLINE) {
                if (userId == this.userId) {
                    val dateUtils = DateUtils()
                    val mLastSeen = intent.getIntExtra(Constansts.LAST_SEEN_FIELD, -1)
                    val todayDate = dateUtils.fromUnixToStrictDate(System.currentTimeMillis()/1000)
                    val messageDate = dateUtils.fromUnixToStrictDate(mLastSeen.toLong())
                    var formattedDate = if (todayDate > messageDate) {
                        dateUtils.fromUnixToDateAndTime(mLastSeen.toLong())
                    } else {
                        dateUtils.fromUnixToTimeString(mLastSeen.toLong())
                    }
                    view.setStatus("${Constansts.OFFLINE} $formattedDate")
                }
            }
        }
    }
}