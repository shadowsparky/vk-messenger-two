package ru.shadowsparky.messenger.dagger

import dagger.Component
import ru.shadowsparky.messenger.auth.AuthModel
import ru.shadowsparky.messenger.auth.AuthPresenter
import ru.shadowsparky.messenger.auth.AuthView
import ru.shadowsparky.messenger.dialogs.AuthDialog
import javax.inject.Singleton

@Singleton
@Component(modules = [AdditionalModule::class])
interface Component {
    fun inject(target: AuthView)
    fun inject(target: AuthPresenter)
    fun inject(target: AuthModel)
    fun inject(target: AuthDialog)
}