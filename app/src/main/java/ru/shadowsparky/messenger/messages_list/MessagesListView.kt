package ru.shadowsparky.messenger.messages_list

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_friends_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.adapters.MessagesAdapter
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.ToastUtils
import javax.inject.Inject

class MessagesListView : AppCompatActivity(), MessagesList.View {
    lateinit var presenter: MessagesList.Presenter
    @Inject lateinit var log: Logger
    @Inject lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject lateinit var toast: ToastUtils
    var adapter: MessagesAdapter? = null

    override fun setLoading(result: Boolean) {

    }

    override fun setAdapter(response: MessagesResponse, callback: (Int) -> Unit) {
        if (adapter == null) {
            adapter = MessagesAdapter(response, callback)
            messages_list.setHasFixedSize(true)
            messages_list.layoutManager = GridLayoutManager(this, 1)
            messages_list.adapter = adapter
        } else {
            adapter!!.addData(response)
        }
    }

    override fun showError() =
        toast.error(this, "При соединении произошла ошибка")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_view)
        App.component.inject(this)
        presenter = MessagesListPresenter(this, log, preferencesUtils)
        presenter.onActivityOpen()
    }
}
