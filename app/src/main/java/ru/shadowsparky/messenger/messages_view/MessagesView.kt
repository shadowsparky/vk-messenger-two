/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_messages_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.adapters.HistoryAdapter
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.*
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEFAULT_SPAN_VALUE
import ru.shadowsparky.messenger.utils.Constansts.Companion.ONLINE_STATUS
import ru.shadowsparky.messenger.utils.Constansts.Companion.ONLINE_STATUS_NOT_FOUND
import ru.shadowsparky.messenger.utils.Constansts.Companion.STATUS_OFFLINE
import ru.shadowsparky.messenger.utils.Constansts.Companion.URL
import ru.shadowsparky.messenger.utils.Constansts.Companion.URL_NOT_FOUND
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_DATA
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_ID
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_ID_NOT_FOUND
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_NOT_FOUND
import javax.inject.Inject

class MessagesView : AppCompatActivity(), Messages.View {
    @Inject lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject lateinit var log: Logger
    @Inject lateinit var validator: Validator
    @Inject lateinit var toast: ToastUtils
    private lateinit var presenter: MessagesPresenter
    private var adapter: HistoryAdapter? = null
    private var user_id: Int = USER_ID_NOT_FOUND
    private var user_data: String = USER_NOT_FOUND
    private var url: String = URL_NOT_FOUND
    private var online_status = ONLINE_STATUS_NOT_FOUND

    init {
        App.component.inject(this)
    }

    override fun clearMessageText() {
        add_message.setText("")
    }

    override fun disposeAdapter() {
        adapter = null
    }

    override fun showError() = toast.error(this, "При соединении произошла ошибка")

    override fun setAdapter(response: HistoryResponse, scroll_callback: (Int) -> Unit) {
        if (adapter == null) {
            adapter = HistoryAdapter(response, scroll_callback, user_id)
            adapter!!.reverse()
            message_history_list.setHasFixedSize(true)
            message_history_list.layoutManager = GridLayoutManager(this, DEFAULT_SPAN_VALUE)
            message_history_list.adapter = adapter
            message_history_list.scrollToPosition(adapter!!.itemCount - 1)
        } else {
            adapter!!.addData(response)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_view)
        user_id = intent.getIntExtra(USER_ID, USER_ID_NOT_FOUND)
        user_data = intent.getStringExtra(USER_DATA)
        url = intent.getStringExtra(URL)
        online_status = intent.getIntExtra(ONLINE_STATUS, ONLINE_STATUS_NOT_FOUND)
        setSupportActionBar(toolbar)
        if ((user_id != USER_ID_NOT_FOUND) and (user_data != USER_NOT_FOUND) and
                (url != URL_NOT_FOUND) and (online_status != ONLINE_STATUS_NOT_FOUND)) {
            presenter = MessagesPresenter(user_id, this, preferencesUtils, log)
            initToolbar()
            presenter.onGetMessageHistoryRequest()
        }
        push_message.setOnClickListener {
            presenter.onSendMessage(add_message.text.toString())
        }
        val verify_callback: (Boolean) -> Unit = { push_message.isEnabled = it }
        validator.verifyText(add_message, verify_callback)
    }

    protected fun initToolbar() {
        message_history_user_data.text = user_data
        if (online_status == STATUS_OFFLINE) {
            message_history_user_online.text = "Не в сети"
        } else {
            message_history_user_online.text = "В сети"
        }
        presenter.onGetPhoto(url, message_history_user_photo)
    }
}
