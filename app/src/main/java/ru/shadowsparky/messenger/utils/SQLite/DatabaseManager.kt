package ru.shadowsparky.messenger.utils.SQLite

import com.google.gson.Gson
import ru.shadowsparky.messenger.messages_list.MessagesList
import ru.shadowsparky.messenger.messages_view.Messages
import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

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

    fun getAllMessagesList() : List<MessagesListTable> =  db.MessagesListDao().getAll()


    fun getByIdMessagesList(id: Long) : MessagesListTable = db.MessagesListDao().getByID(id)

    fun removeAllMessagesList() = Thread {
        db.MessagesListDao().removeAll()
        log.print("БД списка сообщений была очищена")
    }.start()

    fun convertJsonToObject(json: String) : MessagesResponse = Gson().fromJson(json, MessagesResponse::class.java)
}