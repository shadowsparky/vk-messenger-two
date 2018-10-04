/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.dagger

import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import ru.shadowsparky.messenger.utils.CompositeDisposableManager

@Module
class RxModule {
    @Provides
    fun provideCompositeDisposable() : CompositeDisposable = CompositeDisposable()
    @Provides
    fun provideCompositeDisposableManager() : CompositeDisposableManager = CompositeDisposableManager()
}