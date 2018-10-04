/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_messages_list_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.adapters.MessagesAdapter
import ru.shadowsparky.messenger.auth.AuthView
import ru.shadowsparky.messenger.messages_view.MessagesView
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.*
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEVICE_ID
import ru.shadowsparky.messenger.utils.Constansts.Companion.FIREBASE_TOKEN
import ru.shadowsparky.messenger.utils.Constansts.Companion.ONLINE_STATUS
import ru.shadowsparky.messenger.utils.Constansts.Companion.URL
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_DATA
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_ID
import javax.inject.Inject

open class MessagesListView : AppCompatActivity(), MessagesList.View {
    @Inject protected lateinit var presenter: MessagesList.Presenter
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var toast: ToastUtils
    @Inject protected lateinit var colorize: TextColorUtils
    var adapter: MessagesAdapter? = null

    override fun setLoading(result: Boolean) {
        refresher.isRefreshing = result
    }

    override fun disposeAdapter() {
        adapter = null
    }

    override fun onResume() {
        super.onResume()
        disposeAdapter()
        presenter.onActivityOpen()
        log.print("MessagesListView activity loaded")
    }

    override fun onPause() {
        super.onPause()
        log.print("MessagesListView activity on pause...")
    }

    override fun navigateToHistory(id: Int, user_data: String, url: String, online_status: Int) {
        val intent = Intent(this, MessagesView::class.java)
        intent.putExtra(USER_ID, id)
        intent.putExtra(USER_DATA, user_data)
        intent.putExtra(URL, url)
        intent.putExtra(ONLINE_STATUS, online_status)
        startActivity(intent)
    }

    override fun setAdapter(response: MessagesResponse, callback: (Int) -> Unit, touch_callback: (Int, String, String, Int) -> Unit) {
        if (adapter == null) {
            adapter = MessagesAdapter(response, callback, touch_callback)
            messages_list.setHasFixedSize(true)
            messages_list.layoutManager = GridLayoutManager(this, 1)
            messages_list.adapter = adapter
        } else {
            adapter!!.addData(response)
        }
    }

    override fun showError(code: Int) {
        when(code) {
            Constansts.CONNECTION_ERROR_CODE ->
                toast.error(this, "При соединении произошла ошибка. Проверьте свое интернет соединение")
            else ->
                toast.error(this, "Произошла неизвестная ошибка")
        }
        setLoading(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.messages_list_menu, menu)
        val item = menu.findItem(R.id.exit_from_account)
        item.title = colorize.getBlackText(item.title.toString())
        item.setOnMenuItemClickListener {
            preferencesUtils.removeAll()
            startActivity(Intent(this, AuthView::class.java))
            finish()
            return@setOnMenuItemClickListener true
        }
        val sub = menu.findItem(R.id.subscribe_to_push)
        sub.title = colorize.getBlackText(sub.title.toString())
        sub.setOnMenuItemClickListener {
            presenter.onPushSubscribing()
            return@setOnMenuItemClickListener true
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component.inject(this)
        setContentView(R.layout.activity_messages_list_view)
        setSupportActionBar(toolbar)
        presenter.attachView(this)
        refresher.setOnRefreshListener {
            disposeAdapter()
            presenter.onActivityOpen()
        }
        if (preferencesUtils.read(DEVICE_ID) == "")
            preferencesUtils.write(DEVICE_ID, Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID))
        log.print("FIREBASE TOKEN: ${preferencesUtils.read(FIREBASE_TOKEN)}")
        log.print("DEVICE ID: ${preferencesUtils.read(DEVICE_ID)}")
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeRequests()
        log.print("MessagesListView activity destroyed")
    }
}
