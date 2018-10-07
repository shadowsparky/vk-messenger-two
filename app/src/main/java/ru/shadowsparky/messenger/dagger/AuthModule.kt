/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.dagger

import dagger.Module
import dagger.Provides
import ru.shadowsparky.messenger.auth.Auth
import ru.shadowsparky.messenger.auth.AuthModel
import ru.shadowsparky.messenger.auth.AuthPresenter

@Module
class AuthModule {
    @Provides
    fun provideAuthPresenter() : Auth.Presenter = AuthPresenter()

    @Provides
    fun provideAuthModel() : Auth.Model = AuthModel()
}