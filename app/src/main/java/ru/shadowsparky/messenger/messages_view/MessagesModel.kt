/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.widget.ImageView
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import ru.shadowsparky.messenger.response_utils.requester.Requester
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.SQLite.DBViewTableWrapper
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesModel : Messages.Model {
    @Inject protected lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject protected lateinit var db: DBViewTableWrapper
    @Inject protected lateinit var requester: Requester

    init {
        App.component.inject(this)
    }

    override fun attachCallbacks(successCallback: (Response) -> Unit, failureCallback: (Throwable) -> Unit) =
            requester.attachCallbacks(successCallback, failureCallback)
    override fun getMessageHistory(peerId: Int, offset: Int) = requester.getMessageHistory(peerId, offset)
    override fun getCachedHistory(callback: (Response) -> Unit, user_id: Long) =
            db.getAllByIDWithCallback(callback, user_id)
    override fun sendMessage(peerId: Int, message: String) = requester.sendMessage(peerId, message)
    override fun getPhoto(url: String, image: ImageView) = picasso.load(url).circle().into(image)
    override fun disposeRequests() = requester.disposeRequests()
}