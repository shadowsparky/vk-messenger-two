/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.dagger

import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.messages_list.MessagesList
import ru.shadowsparky.messenger.messages_list.MessagesListModel
import ru.shadowsparky.messenger.messages_list.MessagesListPresenter

@Module
class MessagesListModule {
    @Provides
    fun provideModel() : MessagesList.Model = MessagesListModel()
    @Provides
    fun providePresenter() : MessagesList.Presenter = MessagesListPresenter()
}