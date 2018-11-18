package ru.shadowsparky.messenger.utils.SQLite

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastID(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllWithCallback(callback: (Response) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(): List<MessagesViewTable> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getById(id: Long): MessagesViewTable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeAll() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun convertJsonToObject(json: String): HistoryResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}