package ru.shadowsparky.messenger.utils.SQLite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MessagesListTable")
class MessagesListTable : Table {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    var url: String = ""
    var response: String = ""
}