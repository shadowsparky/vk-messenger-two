package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.Message
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import javax.inject.Inject

class MessagesListModel() : MessagesList.Model {
    @Inject
    lateinit var preferences: SharedPreferencesUtils

    init {
        App.component.inject(this)
    }

    override fun getAllDialogs(callback: (ArrayList<Message>?) -> Unit) {
        
    }

}