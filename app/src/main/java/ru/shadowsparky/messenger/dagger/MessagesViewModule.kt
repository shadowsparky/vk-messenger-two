/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.dagger

import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.messages_view.Messages
import ru.shadowsparky.messenger.messages_view.MessagesModel
import ru.shadowsparky.messenger.messages_view.MessagesPresenter

@Module
class MessagesViewModule {
    @Provides
    fun provideMessagesModel() : Messages.Model = MessagesModel()
    @Provides
    fun provideMessagesPresenter() : Messages.Presenter = MessagesPresenter()
}