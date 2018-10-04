/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.dagger

import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.messages_view.MessagesModel

@Module
class MessagesViewModule {
    @Provides
    fun provideMessagesModel() : MessagesModel = MessagesModel()
}