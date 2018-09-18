package ru.shadowsparky.messenger.messages_list

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_friends_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.adapters.MessagesAdapter
import ru.shadowsparky.messenger.auth.AuthView
import ru.shadowsparky.messenger.messages_view.MessagesView
import ru.shadowsparky.messenger.messages_view.MessagesView.Companion.USER_ID
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

    override fun navigateToHistory(id: Int) {
        val intent = Intent(this, MessagesView::class.java)
        intent.putExtra(USER_ID, id)
        log.print("$id")
        startActivity(intent)
    }

    override fun setAdapter(response: MessagesResponse, callback: (Int) -> Unit, touch_callback: (Int) -> Unit) {
        if (adapter == null) {
            adapter = MessagesAdapter(response, callback, touch_callback)
            messages_list.setHasFixedSize(true)
            messages_list.layoutManager = GridLayoutManager(this, 1)
            messages_list.adapter = adapter
        } else {
            adapter!!.addData(response)
        }
    }

    override fun showError() =
        toast.error(this, "При соединении произошла ошибка")

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.messages_list_menu, menu)
        val item = menu.findItem(R.id.exit_from_account)
        val fixedTitle = SpannableString(item.title)
        fixedTitle.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.black)), 0, fixedTitle.length, 0)
        item.title = fixedTitle
        item.setOnMenuItemClickListener {
            preferencesUtils.removeAll()
            startActivity(Intent(this, AuthView::class.java))
            finish()
            return@setOnMenuItemClickListener true
        }
        return super.onCreateOptionsMenu(menu)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_view)
        App.component.inject(this)
        setSupportActionBar(toolbar)
        presenter = MessagesListPresenter(this, log, preferencesUtils)
        presenter.onActivityOpen()
    }
}
