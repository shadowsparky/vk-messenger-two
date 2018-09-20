package ru.shadowsparky.messenger.messages_view

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_messages_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.adapters.HistoryAdapter
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEFAULT_SPAN_VALUE
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_ID
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_ID_NOT_FOUND
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesView : AppCompatActivity(), Messages.View {
    @Inject lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject lateinit var log: Logger
    private lateinit var presenter: MessagesPresenter
    private var adapter: HistoryAdapter? = null
    private var user_id: Int = USER_ID_NOT_FOUND

    init {
        App.component.inject(this)
    }

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
            message_history_list.scrollToPosition(30)
        }
        log.print("LIST SCROLLED")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_view)
        user_id = intent.getIntExtra(USER_ID, USER_ID_NOT_FOUND)
        if (user_id != USER_ID_NOT_FOUND) {
            presenter = MessagesPresenter(user_id, this, preferencesUtils, log)
            presenter.onGetMessageHistoryRequest()
        }
    }
}
