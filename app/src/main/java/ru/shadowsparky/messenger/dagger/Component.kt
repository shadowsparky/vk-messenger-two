package ru.shadowsparky.messenger.dagger

import dagger.Component
import ru.shadowsparky.messenger.adapters.HistoryAdapter
import ru.shadowsparky.messenger.adapters.MessagesAdapter
import ru.shadowsparky.messenger.auth.AuthModel
import ru.shadowsparky.messenger.auth.AuthPresenter
import ru.shadowsparky.messenger.auth.AuthView
import ru.shadowsparky.messenger.dialogs.AuthDialog
import ru.shadowsparky.messenger.messages_list.MessagesListModel
import ru.shadowsparky.messenger.messages_list.MessagesListView
import ru.shadowsparky.messenger.messages_view.MessagesModel
import ru.shadowsparky.messenger.messages_view.MessagesView
import javax.inject.Singleton

@Singleton
@Component(modules = [AdditionalModule::class, RequestModule::class, DateModule::class])
interface Component {
    fun inject(target: AuthView)
    fun inject(target: AuthPresenter)
    fun inject(target: AuthModel)
    fun inject(target: AuthDialog)
    fun inject(target: MessagesListModel)
    fun inject(target: MessagesListView)
    fun inject(target: MessagesAdapter)
    fun inject(target: MessagesView)
    fun inject(target: MessagesModel)
    fun inject(target: HistoryAdapter)
}