package ru.shadowsparky.messenger.messages_view

class MessagesPresenter(
        val view: Messages.View
) : Messages.Presenter {
    val model = MessagesModel()

}