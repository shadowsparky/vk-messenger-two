/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_messages_list_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.adapters.MessagesAdapter
import ru.shadowsparky.messenger.auth.AuthView
import ru.shadowsparky.messenger.messages_view.MessagesView
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.services.SynchronizingService
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.BROADCAST_RECEIVER_CODE
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEAD
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEVICE_ID
import ru.shadowsparky.messenger.utils.Constansts.Companion.FIREBASE_TOKEN
import ru.shadowsparky.messenger.utils.Constansts.Companion.LAST_SEEN_FIELD
import ru.shadowsparky.messenger.utils.Constansts.Companion.ONLINE_STATUS
import ru.shadowsparky.messenger.utils.Constansts.Companion.URL
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_DATA
import ru.shadowsparky.messenger.utils.Constansts.Companion.USER_ID
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SQLite.DBListTableWrapper
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.TextColorUtils
import javax.inject.Inject

open class MessagesListView : AppCompatActivity(), MessagesList.View {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var presenter: MessagesList.Presenter
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var colorize: TextColorUtils
    @Inject protected lateinit var db: DBListTableWrapper
    private val TAG = javaClass.name
    private var adapter: MessagesAdapter? = null
    private lateinit var receiver: ResponseReceiver

    override fun setLoading(result: Boolean) {
        refresher.isRefreshing = result
    }

    override fun disposeAdapter() {
        adapter = null
    }

    override fun onResume() {
        super.onResume()
        disposeAdapter()
        presenter.onScroll()
        log.print("MessagesListView activity loaded", false, TAG)
        registerReceiver(receiver, IntentFilter(BROADCAST_RECEIVER_CODE))
    }

    override fun showContent(flag: Boolean) {
        if (flag) {
            messages_list.visibility = VISIBLE
            messages_not_found.visibility = GONE
        } else {
            messages_list.visibility = GONE
            messages_not_found.visibility = VISIBLE
        }
    }

    override fun startService() {
        val service = Intent(this, SynchronizingService::class.java)
        startService(service)
    }

    override fun stopService() {
        val service = Intent(this, SynchronizingService::class.java)
        stopService(service)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
        log.print("MessagesListView activity on pause...", false, TAG)
    }

    override fun navigateToHistory(id: Int, user_data: String, url: String, online_status: Int, last_seen: Int) {
        val intent = Intent(this, MessagesView::class.java)
        intent.putExtra(USER_ID, id)
        intent.putExtra(USER_DATA, user_data)
        intent.putExtra(URL, url)
        intent.putExtra(LAST_SEEN_FIELD, last_seen)
        intent.putExtra(ONLINE_STATUS, online_status)
        log.print("USER_ID $id", false, TAG)
        startActivity(intent)
    }

    override fun setAdapter(response: MessagesResponse) {
        if (adapter == null) {
            adapter = MessagesAdapter(response, presenter)
            if (response.response!!.count > 0) {
                showContent(true)
                messages_list.setHasFixedSize(true)
                messages_list.layoutManager = GridLayoutManager(this, 1)
                messages_list.adapter = adapter
            } else {
                showContent(false)
            }
        } else {
            adapter!!.addData(response)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.messages_list_menu, menu)
        val item = menu.findItem(R.id.exit_from_account)
        item.title = colorize.getWhiteText(item.title.toString())
        item.setOnMenuItemClickListener {
            preferencesUtils.removeAll()
            startActivity(Intent(this, AuthView::class.java))
            finish()
            return@setOnMenuItemClickListener true
        }
        val sub = menu.findItem(R.id.subscribe_to_push)
        sub.title = colorize.getWhiteText(sub.title.toString())
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
        startService()
        receiver = ResponseReceiver(presenter, log)
        refresher.setOnRefreshListener{ presenter.onScroll() }
        if (preferencesUtils.read(DEVICE_ID) == "")
            preferencesUtils.write(DEVICE_ID, Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID))
        log.print("FIREBASE TOKEN: ${preferencesUtils.read(FIREBASE_TOKEN)}", false, TAG)
        log.print("DEVICE ID: ${preferencesUtils.read(DEVICE_ID)}", false, TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onActivityDestroying()
        stopService()
        log.print("$TAG $DEAD", false, TAG)
        log.print("MessagesListView activity destroyed", false, TAG)
    }
}
