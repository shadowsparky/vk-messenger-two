package ru.shadowsparky.messenger.utils.SQLite

import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.FIRST_ELEMENT
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

class DBViewTableWrapper : DatabaseManager {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var db: MessagesDB
    @Inject protected lateinit var log: Logger
    private val TAG = this.javaClass.name

    init {
        App.component.inject(this)
    }

    override fun writeToDB(data: Response, url: String) {
        if (data is HistoryResponse) {
            val element = MessagesViewTable()
            element.response = Gson().toJson(data)
            element.url = url
            element.user_id = getPeerID(data)
            for (el in getAll()) {
                if (el.response == element.response) {
                    return
                }
            }
            db.MessagesViewDao().insert(element)
            log.print("История с пользователем ${getPeerID(data)} была успешно записана в базу данных", true, TAG)
        }
    }

    fun getPeerID(data: HistoryResponse) : Long {
        val items = data.response.items
        if (items!!.size <= 0)
            return -1
        return items[0].peer_id!!.toLong()
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

    fun getAllByIDWithCallback(callback: (Response) -> Unit, user_id: Long) = Thread {
        getByUserID(user_id).toObservable()
            .map { convertJsonToObject(it.response) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { callback(it as Response) }
            )
        log.print("Запрос переписки с пользователем $user_id был выполнен", true, TAG)
    }.start()

    override fun getAll(): List<MessagesViewTable> = db.MessagesViewDao().getAll()

    override fun getById(id: Long): MessagesViewTable = db.MessagesViewDao().getByID(id)

    fun getByUserID(user_id: Long): List<MessagesViewTable> = db.MessagesViewDao().getByUserID(user_id)

    fun removeAllByUserID(user_id: Long)  {
        db.MessagesViewDao().removeAllByUserID(user_id)
        log.print("Переписка с пользователем $user_id была очищена", true, TAG)
    }

    override fun removeAll() = Thread {
        db.MessagesViewDao().removeAll()
        log.print("БД истории сообщений со всеми пользователями была очищена", true, TAG)
    }.start()

    override fun convertJsonToObject(json: String): HistoryResponse = Gson().fromJson(json, HistoryResponse::class.java)

}