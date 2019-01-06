package ru.shadowsparky.messenger.utils.SQLite

import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import ru.shadowsparky.messenger.messages_list.MessagesList
import ru.shadowsparky.messenger.messages_view.Messages
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

class DBListTableWrapper : DatabaseManager {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var db: MessagesDB
    @Inject protected lateinit var log: Logger
    private val TAG = this.javaClass.name

    override fun getLastID() : Long = db.MessagesListDao().getLastID()

    init {
        App.component.inject(this)
    }

    override fun writeToDB(data: Response, url: String) {
        if (data is MessagesResponse) {
            val element = MessagesListTable()
            element.response = Gson().toJson(data)
            element.url = url
            for (el in getAll()) {
                if (el.response == element.response) {
                    return
                }
            }
            db.MessagesListDao().insert(element)
            log.print("Элемент списка сообщений был успешно записан в базу данных", true, TAG)
        }
    }

    override fun getAllWithCallback(callback: (Response) -> Unit) = Thread {
        getAll().toObservable()
            .map { convertJsonToObject(it.response) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { callback(it as Response) }
            )
    }.start()

    override fun getAll() : List<MessagesListTable> =  db.MessagesListDao().getAll()

    override fun getById(id: Long) : MessagesListTable = db.MessagesListDao().getByID(id)

    override fun removeAll() = Thread {
        db.MessagesListDao().removeAll()
        log.print("БД списка сообщений была очищена", true, TAG)
    }.start()

    override fun convertJsonToObject(json: String) : MessagesResponse = Gson().fromJson(json, MessagesResponse::class.java)
}