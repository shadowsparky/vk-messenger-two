/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.messages_list

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.rxkotlin.toObservable
import ru.shadowsparky.messenger.response_utils.FailureResponseHandler
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.response_utils.responses.VKPushResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SQLite.DatabaseManager
import ru.shadowsparky.messenger.utils.SQLite.MessagesListTable
import javax.inject.Inject

class MessagesListPresenter : MessagesList.Presenter {
    @Inject protected lateinit var model: MessagesList.Model
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var errorUtils: FailureResponseHandler
    // FIXME взаимодействие с бд должно быть в модели
    @Inject protected lateinit var db: DatabaseManager
    private var view: MessagesList.View? = null

    init {
        App.component.inject(this)
    }

    override fun attachView(view: MessagesListView) {
        this.view = view
        errorUtils.attach(view)
    }

    override fun onActivityOpen() {
//        db.getAllMessagesListWithCallback(::onSuccessResponse)
//        view!!.disposeAdapter()
        model.getAllDialogs(::onSuccessResponse, ::onFailureResponse)
    }

    override fun onPushSubscribing() = model.subscribeToPush(::onSuccessResponse, ::onFailureResponse)

    override fun onFailureResponse(error: Throwable) {
        // FIXME взаимодействие с бд должно быть в модели
        db.getAllMessagesListWithCallback(::onSuccessResponse)
        view!!.disposeAdapter()
        errorUtils.onFailureResponse(error)
    }

    override fun onActivityDestroying() = model.disposeRequests()

    override fun onScrollFinished(currentOffset: Int) =
            model.getAllDialogs(::onSuccessResponse, ::onFailureResponse, currentOffset)

    override fun onItemClicked(id: Int, user_data: String, url: String, online_status: Int) =
            view!!.navigateToHistory(id, user_data, url, online_status)

    override fun onSuccessResponse(response: Response) {
        when(response) {
            is MessagesResponse ->
                view!!.run {
                    setAdapter(response, ::onScrollFinished, ::onItemClicked)
                    setLoading(false)
                }
            is VKPushResponse -> log.print("Вы подписались на пуш уведомления")
            else -> onFailureResponse(ClassCastException())
        }
    }
}