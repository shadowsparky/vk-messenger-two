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

// TODO: Сделать так, чтобы при загрузке приложения загружалась копия из БД, а если результат запроса будет без ошибок, то он должен будет перезаписать копию бд
class DatabaseManager {
    @Inject protected lateinit var db: MessagesDB
    @Inject protected lateinit var log: Logger

    fun getLastIDMessagesList() : Long = db.MessagesListDao().getLastID()

    init {
        App.component.inject(this)
    }

    fun writeToDB(data: Response, url: String) {
        when(data) {
            is MessagesResponse -> {
                val element = MessagesListTable()
                element.response = Gson().toJson(data)
                element.url = url
                for (el in getAllMessagesList()) {
                    if (el.response == element.response) {
                        return
                    }
                }
                db.MessagesListDao().insert(element)
                log.print("Элемент списка сообщений был успешно записан в базу данных")
            }
        }
    }

    fun getAllMessagesListWithCallback(callback: (Response) -> Unit) = Thread {
        getAllMessagesList().toObservable()
            .map { convertJsonToObject(it.response) }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                    onNext = { callback(it as Response) }
            )
    }.start()

    fun getAllMessagesList() : List<MessagesListTable> =  db.MessagesListDao().getAll()


    fun getByIdMessagesList(id: Long) : MessagesListTable = db.MessagesListDao().getByID(id)

    fun removeAllMessagesList() = Thread {
        db.MessagesListDao().removeAll()
        log.print("БД списка сообщений была очищена")
    }.start()

    fun convertJsonToObject(json: String) : MessagesResponse = Gson().fromJson(json, MessagesResponse::class.java)
}