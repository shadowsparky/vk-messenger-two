package ru.shadowsparky.messenger.utils.SQLite

import ru.shadowsparky.messenger.response_utils.Response
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse

interface DatabaseManager {
    fun writeToDB(data: Response, url: String)
    fun getLastID() : Long
    fun getAllWithCallback(callback: (Response) -> Unit)
    fun getAll() : List<Table>
    fun getById(id: Long): Table
    fun removeAll()
    fun convertJsonToObject(json: String): Response
}