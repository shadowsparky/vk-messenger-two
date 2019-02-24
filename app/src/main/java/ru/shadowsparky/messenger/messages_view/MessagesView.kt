/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_messages_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.adapters.HistoryAdapter
import ru.shadowsparky.messenger.open_photo.OpenPhotoView
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.*
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEAD
import ru.shadowsparky.messenger.utils.Constansts.Companion.LAST_SEEN_FIELD
import ru.shadowsparky.messenger.utils.Constansts.Companion.OFFLINE
import ru.shadowsparky.messenger.utils.Constansts.Companion.ONLINE
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

class MessagesView : AppCompatActivity(), Messages.View {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var validator: Validator
    @Inject protected lateinit var presenter: Messages.Presenter
    @Inject protected lateinit var dateUtils: DateUtils
    @Inject protected lateinit var toast: ToastUtils
    private var adapter: HistoryAdapter? = null
    private var userId = USER_ID_NOT_FOUND
    private var userData = USER_NOT_FOUND
    private var url = URL_NOT_FOUND
    private var onlineStatus = STATUS_HIDE
    private var mLastSeen = STATUS_HIDE
    private var receiver: ResponseReceiver? = null
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

    override fun setStatus(status: String) {
        message_history_user_online.text = status
    }

    override fun setAdapter(response: HistoryResponse) {
        log.print("Current adapter is $adapter", false, TAG)
        if (adapter == null) {
            adapter = HistoryAdapter(response, presenter)
            adapter!!.reverse()
            message_history_list.setHasFixedSize(true)
            message_history_list.layoutManager = LinearLayoutManager(this)
            message_history_list.adapter = adapter
        } else {
            adapter!!.addData(response)
        }
        setLoading(false)
    }

    private fun initVars() {
        userId = intent.getIntExtra(USER_ID, USER_ID_NOT_FOUND)
        log.print("User ID $userId", false, TAG)
        userData = intent.getStringExtra(USER_DATA)
        url = intent.getStringExtra(URL)
        onlineStatus = intent.getIntExtra(ONLINE_STATUS, STATUS_HIDE)
        mLastSeen = intent.getIntExtra(LAST_SEEN_FIELD, STATUS_HIDE)
        log.print("$userId $userData $url $onlineStatus")
    }

    private fun initControls() {
        if ((userId != USER_ID_NOT_FOUND) and (userData != USER_NOT_FOUND) and
                (url != URL_NOT_FOUND)) {
            presenter.attachPeerID(userId)
                    .attachView(this)
            receiver = ResponseReceiver(this, presenter, log, userId)
            presenter.onGetMessageHistoryRequest()
            push_message.setOnClickListener {
                presenter.onSendMessage(add_message.text.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_view)
        setSupportActionBar(toolbarview)
        initVars()
        initControls()
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
        val todayDate = dateUtils.fromUnixToStrictDate(System.currentTimeMillis()/1000)
        val messageDate = dateUtils.fromUnixToStrictDate(mLastSeen.toLong())
        val formattedDate = if (todayDate > messageDate)
            dateUtils.fromUnixToDateAndTime(mLastSeen.toLong())
        else
            dateUtils.fromUnixToTimeString(mLastSeen.toLong())
        when (onlineStatus) {
            STATUS_HIDE -> message_history_user_online.visibility = GONE
            STATUS_OFFLINE -> {
                val status = "$OFFLINE $formattedDate"
                setStatus(status)
            }
            STATUS_ONLINE -> setStatus(ONLINE)
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_gray_24dp)
    }
//
//    fun onActionItemClicked(
//            actionMode: ActionMode,
//            menuItem: MenuItem): Boolean {
//        when (menuItem.itemId) {
//            R.id.menu_delete -> {
//                val selectedItemPositions = adapter.getSelectedItems()
//                for (i in selectedItemPositions.indices.reversed()) {
//                    adapter.removeData(selectedItemPositions[i])
//                }
//                actionMode.finish()
//                return true
//            }
//            else -> return false
//        }
//    }
//
//    fun onDestroyActionMode(actionMode: ActionMode) {
//        this.actionMode = null
//        adapter.clearSelections()
//    }

//    override fun onContextItemSelected(item: MenuItem?): Boolean {
//        when (item?.itemId) {
//            SELECT_DELETE -> {
//                val item = adapter?.getItemById(item.groupId)
//                if (item!!.out!! == 1) {
//                    if (item.date!! + UNIX_DAY > dateUtils.getCurrentTimestamp()) {
//                        log.print("OKEY", false, TAG)
//                    } else {
//                        toast.error(this, "Вы не можете удалить это сообщение. Слишком поздно")
//                    }
//                } else {
//                    toast.error(this, "Вы не можете удалить это сообщение")
//                }
//            }
//            SELECT_EDIT -> {
//                val item = adapter?.getItemById(item.groupId)
//                if (item!!.out!! == 1) {
//                    if (item.date!! + UNIX_DAY > dateUtils.getCurrentTimestamp()) {
//                        log.print("OKEY", false, TAG)
//                    } else {
//                        toast.error(this, "Вы не можете отредактировать это сообщение. Слишком поздно")
//                    }
//                } else {
//                    toast.error(this, "Вы не можете отредактировать это сообщение")
//                }
//            }
//
//        }
//        return super.onContextItemSelected(item)
//    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onActivityDestroying()
        log.print("$TAG $DEAD", false, TAG)
    }

    override fun photoTouched(image: ImageView, url: String) {
        val i = Intent(this, OpenPhotoView::class.java)
        val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this, image, this.getString(R.string.transition))
        i.putExtra(Constansts.URL, url)
        this.startActivity(i, options.toBundle())
    }
}
