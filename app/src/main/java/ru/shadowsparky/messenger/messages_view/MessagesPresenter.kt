/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_view

import android.content.Intent
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.open_photo.OpenPhotoView
import ru.shadowsparky.messenger.response_utils.FailureResponseHandler
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.SendMessageResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts
import javax.inject.Inject

class MessagesPresenter : Messages.Presenter {
    @Inject protected lateinit var model: Messages.Model
    @Inject protected lateinit var errorUtils: FailureResponseHandler
    private var view: MessagesView? = null
    private var peerId: Int? = null
    private var loadingError = false

    init {
        App.component.inject(this)
    }

    override fun attachPeerID(peerId: Int) : MessagesPresenter {
        this.peerId = peerId
        return this
    }

    override fun attachView(view: MessagesView) {
        this.view = view
        errorUtils.attach(view)
    }

    override fun onPhotoTouched(image: ImageView, url: String) {
        val i = Intent(view!!, OpenPhotoView::class.java)
        val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(view!!, image, view!!.getString(R.string.transition))
        i.putExtra(Constansts.URL, url)
        view!!.startActivity(i, options.toBundle())
    }

    override fun onGetMessageHistoryRequest() {
        view!!.setLoading(true)
        view!!.disposeAdapter()
        model.getMessageHistory(peerId!!, ::onSuccessResponse, ::onFailureResponse)
    }

    override fun onScrollFinished(position: Int) {
        view!!.setLoading(true)
        model.getMessageHistory(peerId!!,::onSuccessResponse, ::onFailureResponse, position)
    }

    override fun onSendMessage(message: String) {
        view!!.setLoading(true)
        model.sendMessage(peerId!!, message, ::onSuccessResponse, ::onFailureResponse)
    }

    override fun onFailureResponse(error: Throwable) {
        val callback: (response: Response) -> Unit = {
            loadingError = true
            view!!.setAdapter(it as HistoryResponse, ::onScrollFinished, ::onPhotoTouched)
        }
        if (!loadingError) {
            view!!.setLoading(true)
            model.getCachedHistory(callback, peerId!!.toLong())
            view!!.disposeAdapter()
        }
        view!!.setLoading(false)
        errorUtils.onFailureResponse(error)
    }

    override fun onGetPhoto(url: String, image: ImageView) = model.getPhoto(url, image)

    override fun onActivityDestroying() = model.disposeRequests()

    override fun onSuccessResponse(response: Response) {
        when (response) {
            is HistoryResponse -> view!!.setAdapter(response, ::onScrollFinished, ::onPhotoTouched)
            is SendMessageResponse -> { view!!.clearMessageText() }
            else -> onFailureResponse(ClassCastException())
        }
        loadingError = false
    }
}