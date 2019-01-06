/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

open class CompositeDisposableManager {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var disposables: CompositeDisposable
    @Inject protected lateinit var log: Logger
    init {
        App.component.inject(this)
    }

    fun addRequest(item: Disposable) = disposables.add(item)

    fun disposeAllRequests() {
        if ((disposables.size() > 0) and (!disposables.isDisposed)) {
            log.print("Requests disposed. Size: ${disposables.size()}")
            disposables.dispose()
        }
    }
}