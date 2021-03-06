/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.dagger

import dagger.Component
import ru.shadowsparky.messenger.utils.Startup
import ru.shadowsparky.messenger.adapters.HistoryAdapter
import ru.shadowsparky.messenger.adapters.MessagesAdapter
import ru.shadowsparky.messenger.auth.AuthModel
import ru.shadowsparky.messenger.auth.AuthPresenter
import ru.shadowsparky.messenger.auth.AuthView
import ru.shadowsparky.messenger.dialogs.AttachmentDialog
import ru.shadowsparky.messenger.dialogs.AuthDialog
import ru.shadowsparky.messenger.messages_list.MessagesListModel
import ru.shadowsparky.messenger.messages_list.MessagesListPresenter
import ru.shadowsparky.messenger.messages_list.MessagesListView
import ru.shadowsparky.messenger.messages_view.MessagesModel
import ru.shadowsparky.messenger.messages_view.MessagesPresenter
import ru.shadowsparky.messenger.messages_view.MessagesView
import ru.shadowsparky.messenger.notifications.FirebaseMessage
import ru.shadowsparky.messenger.notifications.FirebaseRefresher
import ru.shadowsparky.messenger.open_photo.OpenPhotoModel
import ru.shadowsparky.messenger.open_photo.OpenPhotoPresenter
import ru.shadowsparky.messenger.open_photo.OpenPhotoView
import ru.shadowsparky.messenger.response_utils.requester.FailureResponseHandler
import ru.shadowsparky.messenger.utils.CompositeDisposableManager
import ru.shadowsparky.messenger.response_utils.requester.RequestBuilder
import ru.shadowsparky.messenger.response_utils.requester.Requester
import ru.shadowsparky.messenger.services.SynchronizingService
import ru.shadowsparky.messenger.utils.SQLite.DBListTableWrapper
import ru.shadowsparky.messenger.utils.SQLite.DBViewTableWrapper
import javax.inject.Singleton

@Singleton
@Component(modules = [AdditionalModule::class, RequestModule::class, DateModule::class, DatabaseModule::class,
    MessagesListModule::class, MessagesViewModule::class, RxModule::class, AuthModule::class, OpenPhotoModule::class
])
interface Component {
    fun inject(target: AuthView)
    fun inject(target: AuthPresenter)
    fun inject(target: AuthModel)
    fun inject(target: AuthDialog)
    fun inject(target: MessagesListModel)
    fun inject(target: MessagesListPresenter)
    fun inject(target: MessagesListView)
    fun inject(target: MessagesAdapter)
    fun inject(target: MessagesView)
    fun inject(target: MessagesPresenter)
    fun inject(target: MessagesModel)
    fun inject(target: HistoryAdapter)
    fun inject(target: FirebaseRefresher)
    fun inject(target: FirebaseMessage)
    fun inject(target: RequestBuilder)
    fun inject(target: CompositeDisposableManager)
    fun inject(target: FailureResponseHandler)
    fun inject(target: DBListTableWrapper)
    fun inject(target: DBViewTableWrapper)
    fun inject(target: Startup)
    fun inject(target: OpenPhotoPresenter)
    fun inject(target: OpenPhotoModel)
    fun inject(target: OpenPhotoView)
    fun inject(target: SynchronizingService)
    fun inject(target: Requester)
    fun inject(target: AttachmentDialog)
}