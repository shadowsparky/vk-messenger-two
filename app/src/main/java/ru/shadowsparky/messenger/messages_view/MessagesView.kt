/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.os.Bundle
import android.view.View.GONE
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_messages_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.adapters.HistoryAdapter
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.App
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
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.Validator
import javax.inject.Inject

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

    init {
        App.component.inject(this)
    }

    override fun clearMessageText() {
        add_message.setText("")
    }

    override fun disposeAdapter() {
        adapter = null
    }

    override fun setAdapter(response: HistoryResponse, scroll_callback: (Int) -> Unit,
                            photo_touch_callback: (ImageView, String) -> Unit) {
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
    }

    override fun onStart() {
        super.onStart()
        userId = intent.getIntExtra(USER_ID, USER_ID_NOT_FOUND)
        userData = intent.getStringExtra(USER_DATA)
        url = intent.getStringExtra(URL)
        onlineStatus = intent.getIntExtra(ONLINE_STATUS, STATUS_HIDE)
        log.print("$userId $userData $url $onlineStatus")
        if ((userId != USER_ID_NOT_FOUND) and (userData != USER_NOT_FOUND) and
                (url != URL_NOT_FOUND)) {
            presenter.attachPeerID(userId)
                    .attachView(this)
            push_message.setOnClickListener {
                presenter.onSendMessage(add_message.text.toString())
            }
        }
        initToolbar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_view)
        setSupportActionBar(toolbar)
        val verifyCallback: (Boolean) -> Unit = { push_message.isEnabled = it }
        validator.verifyText(add_message, verifyCallback)
    }

    override fun onResume() {
        super.onResume()
        disposeAdapter()
        presenter.onGetMessageHistoryRequest()
        log.print("MessagesView activity loaded")
    }

    override fun onPause() {
        super.onPause()
        log.print("MessagesView activity paused")
    }

    private fun initToolbar() {
        message_history_user_data.text = userData
        when (onlineStatus) {
            STATUS_OFFLINE -> message_history_user_online.text = "Не в сети"
            STATUS_HIDE -> message_history_user_online.visibility = GONE
            STATUS_ONLINE -> message_history_user_online.text = "В сети"
        }
        presenter.onGetPhoto(url, message_history_user_photo)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onActivityDestroying()
        log.print("MessagesView activity destroyed")
    }
}
