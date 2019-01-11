/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

open class Constansts {
    companion object {
        const val UNSPECTED_ERROR = "Произошла неизвестная ошибка"
        const val CAPTCHA_ERROR = 0xD
        const val DEFAULT_TIMEOUT = 0x1E.toLong()
        const val DEFAULT_SLEEP_TIME_ON_ERROR = 0x7D0.toLong()
        const val DEFAULT_SERVER_SPLITTER = '/'
        const val USER_ID = "id"
        const val EMPTY_STRING = ""
        const val VK_PEER_CHAT = "chat"
        const val VK_PEER_GROUP = "group"
        const val VK_API_VERSION = 5.87
        const val USER_ID_NOT_FOUND = -1
        const val DEFAULT_SPAN_VALUE = 1
        const val USER_DATA = "user_data"
        const val USER_NOT_FOUND = "user_not_found"
        const val URL = "url"
        const val URL_NOT_FOUND = "url not found"
        const val ONLINE_STATUS = "online status"
        const val STATUS_OFFLINE = 0
        const val STATUS_ONLINE = 1
        const val STATUS_HIDE = -1
        const val FIREBASE_TOKEN = "firebase_token"
        const val DEVICE_ID = "device_id"
        const val BROADCAST_RECEIVER_CODE = "brc"
        const val LONG_POLL_VERSION = 3
        const val RESPONSE = "response"
        const val PHOTO = "photo"
        const val STICKER = "sticker"
        const val WALL = "wall"
        const val LAST_SEEN_FIELD = "last_seen"
        const val RECORD_OPEN = "Открыть запись"
        const val PHOTO_NOT_FOUND = "https://vk.com/images/camera_100.png?ava=1"
        const val USER_LONG_POLL_STATUS_CHANGED = "long poll status changed"
        const val DEAD = " is dead. Rest in Peace"
        const val ONLINE = "В сети"
        const val OFFLINE = "Был(а) в сети"
    }
}