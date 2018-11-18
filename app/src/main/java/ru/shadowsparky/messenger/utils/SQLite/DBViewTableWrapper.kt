package ru.shadowsparky.messenger.utils.SQLite

import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

class DBViewTableWrapper : DatabaseManager {
    @Inject protected lateinit var db: MessagesDB
    @Inject protected lateinit var log: Logger

    init {
        App.component.inject(this)
    }

    override fun writeToDB(data: Response, url: String) {
        if (data is HistoryResponse) {
            val element = MessagesViewTable()
            val items = data.response.items
            element.response = Gson().toJson(data)
            element.url = url
            if (items!!.size <= 0)
                return
            element.id = items[0].peer_id!!.toLong()
            for (el in getAll()) {
                if (el.response == element.response) {
                    return
                }
            }
            db.MessagesViewDao().insert(element)
            log.print("История с этим пользователем была успешно записана в базу данных")
        }
    }

    override fun getLastID(): Long = db.MessagesViewDao().getLastID()

    override fun getAllWithCallback(callback: (Response) -> Unit) = Thread {
        getAll().toObservable()
            .map { convertJsonToObject(it.response) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { callback(it as Response) }
            )
    }.start()

    override fun getAll(): List<MessagesViewTable> = db.MessagesViewDao().getAll()

    override fun getById(id: Long): MessagesViewTable = db.MessagesViewDao().getByID(id)

    fun getByUserID(user_id: Long): List<MessagesViewTable> = db.MessagesViewDao().getByUserID(user_id)

    fun removeAllByUserID(user_id: Long) = db.MessagesViewDao().removeAllByUserID(user_id)

    override fun removeAll() = db.MessagesViewDao().removeAll()

    override fun convertJsonToObject(json: String): HistoryResponse = Gson().fromJson(json, HistoryResponse::class.java)

}