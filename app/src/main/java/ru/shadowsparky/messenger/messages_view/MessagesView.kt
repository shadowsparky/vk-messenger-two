/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_messages_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.adapters.HistoryAdapter
import ru.shadowsparky.messenger.response_utils.pojos.VKMessages
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.*
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEFAULT_SPAN_VALUE
import ru.shadowsparky.messenger.utils.Constansts.Companion.ONLINE_STATUS
import ru.shadowsparky.messenger.utils.Constansts.Companion.STATUS_HIDE
import ru.shadowsparky.messenger.utils.Constansts.Companion.STATUS_OFFLINE
import ru.shadowsparky.messenger.utils.Constansts.Companion.STATUS_ONLINE
import ru.shadowsparky.messenger.utils.Constansts.Companion.URL
import ru.shadowsparky.messenger.utils.Constansts.Companion.URL_NOT_FOUND
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_DATA
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_ID
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_ID_NOT_FOUND
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_NOT_FOUND
import javax.inject.Inject
import kotlin.math.abs

class MessagesView : AppCompatActivity(), Messages.View {
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var validator: Validator
    @Inject protected lateinit var presenter: Messages.Presenter
    private var adapter: HistoryAdapter? = null
    private var userId = USER_ID_NOT_FOUND
    private var userData = USER_NOT_FOUND
    private var url = URL_NOT_FOUND
    private var onlineStatus = STATUS_HIDE
    private var receiver: MessagesView.ResponseReceiver? = null
    private val TAG = javaClass.name

    init {
        App.component.inject(this)
    }

    override fun setLoading(result: Boolean) {
        if (result)
            view_loading.visibility = View.VISIBLE
        else
            view_loading.visibility = View.GONE
    }

    override fun clearMessageText() {
        add_message.setText("")
    }

    override fun disposeAdapter() {
        adapter = null
    }

    override fun setAdapter(response: HistoryResponse, scroll_callback: (Int) -> Unit,
        photo_touch_callback: (ImageView, String) -> Unit) {
        log.print("Current adapter is $adapter", false, TAG)
        if (adapter == null) {
            adapter = HistoryAdapter(response, scroll_callback, photo_touch_callback, userId)
            adapter!!.reverse()
            message_history_list.setHasFixedSize(true)
            message_history_list.layoutManager = GridLayoutManager(this, DEFAULT_SPAN_VALUE)
            message_history_list.adapter = adapter
            message_history_list.scrollToPosition(adapter!!.itemCount - 1)
        } else {
            adapter!!.addData(response)
        }
        setLoading(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_view)
        setSupportActionBar(toolbarview)
        userId = intent.getIntExtra(USER_ID, USER_ID_NOT_FOUND)
        userData = intent.getStringExtra(USER_DATA)
        url = intent.getStringExtra(URL)
        onlineStatus = intent.getIntExtra(ONLINE_STATUS, STATUS_HIDE)
        log.print("$userId $userData $url $onlineStatus")
        if ((userId != USER_ID_NOT_FOUND) and (userData != USER_NOT_FOUND) and
                (url != URL_NOT_FOUND)) {
            presenter.attachPeerID(userId)
                    .attachView(this)
            receiver = ResponseReceiver(presenter, log, userId)
            presenter.onGetMessageHistoryRequest()
            push_message.setOnClickListener {
                presenter.onSendMessage(add_message.text.toString())
            }
        }
        initToolbar()
        val verifyCallback: (Boolean) -> Unit = { push_message.isEnabled = it }
        validator.verifyText(add_message, verifyCallback)
    }

    override fun onResume() {
        super.onResume()
        log.print("MessagesView activity loaded", false, TAG)
        registerReceiver(receiver, IntentFilter(Constansts.BROADCAST_RECEIVER_CODE))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
        log.print("MessagesView activity paused", false, TAG)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToolbar() {
        message_history_user_data.text = userData
        when (onlineStatus) {
            STATUS_HIDE -> message_history_user_online.visibility = GONE
            else -> message_history_user_online.text = DateUtils().fromUnixToDateAndTime(onlineStatus.toLong())
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_gray_24dp)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onActivityDestroying()
        log.print("MessagesView activity destroyed", false, TAG)
    }

    class ResponseReceiver(
            val presenter: Messages.Presenter,
            val log: Logger,
            val userId: Int
    ) : BroadcastReceiver() {
        private var mUpdateFlag = false

        private fun receiveLongPoll(mResponse: VKMessages) {
            mUpdateFlag = false
            if (mResponse.profiles != null) {
                for (item in mResponse.profiles) {
                    if (item.id == userId) {
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

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent!!.action == Constansts.BROADCAST_RECEIVER_CODE) {
                val mResponse = intent.getSerializableExtra(Constansts.RESPONSE)
                when (mResponse) {
                    is VKMessages -> receiveLongPoll(mResponse)
                }
            }
        }
    }
}
