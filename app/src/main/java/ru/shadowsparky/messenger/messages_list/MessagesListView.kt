package ru.shadowsparky.messenger.messages_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.ToastUtils

class MessagesListView : AppCompatActivity(), MessagesList.View {
    lateinit var presenter: MessagesList.Presenter
    lateinit var log: Logger
    lateinit var preferencesUtils: SharedPreferencesUtils
    lateinit var toast: ToastUtils

    override fun setLoading(result: Boolean) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_view)
        App.component.inject(this)
        presenter = MessagesListPresenter(this, log, preferencesUtils)
        presenter.onActivityOpen()
    }
}
