package ru.shadowsparky.messenger.utils.SQLite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MessagesViewTable")
class MessagesViewTable : Table {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    var url: String = ""
    var response: String = ""
    var user_id: Long = -1
}