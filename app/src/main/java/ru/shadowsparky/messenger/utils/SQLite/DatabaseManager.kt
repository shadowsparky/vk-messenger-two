package ru.shadowsparky.messenger.utils.SQLite

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse

interface DatabaseManager {
    fun writeToDB(data: Response, url: String)
    fun getLastID() : Long
    fun getAllWithCallback(callback: (Response) -> Unit)
    fun getAll() : List<MessagesListTable>
    fun getById(id: Long): MessagesListTable
    fun removeAll()
    fun convertJsonToObject(json: String): MessagesResponse
}