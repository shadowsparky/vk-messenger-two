/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

open class CompositeDisposableManager {
    @Inject protected lateinit var disposables: CompositeDisposable
    @Inject protected lateinit var log: Logger
    init {
        App.component.inject(this)
    }

    fun addToCollection(item: Disposable) = disposables.add(item)

    fun disposeAllRequests() {
        if ((disposables.size() > 0) and (!disposables.isDisposed)) {
            log.print("Requests disposed. Size: ${disposables.size()}")
            disposables.dispose()
        }
    }
}