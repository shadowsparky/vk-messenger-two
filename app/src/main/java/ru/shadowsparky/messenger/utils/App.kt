/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import android.app.Application
import ru.shadowsparky.messenger.dagger.AdditionalModule
import ru.shadowsparky.messenger.dagger.Component
import ru.shadowsparky.messenger.dagger.DaggerComponent

class App : Application() {
    companion object {
        @JvmStatic lateinit var component: Component
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerComponent
                .builder()
                .additionalModule(AdditionalModule(applicationContext))
                .build()
    }
}