package ru.shadowsparky.messenger.messages_list

import ru.shadowsparky.messenger.response_utils.Message

class MessagesListPresenter(val view: MessagesList.View) : MessagesList.Presenter {
    val model = MessagesListModel()

    override fun onActivityOpen() {
        view.setLoading(true)
        val callback: (ArrayList<Message>?) -> Unit = {
            if (it != null) {

            } else {

            }
            view.setLoading(false)
        }
        model.getAllDialogs(callback)
    }

}