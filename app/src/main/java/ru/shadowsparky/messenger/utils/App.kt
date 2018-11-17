/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.utils

import android.app.Application
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import ru.shadowsparky.messenger.dagger.AdditionalModule
import ru.shadowsparky.messenger.dagger.Component
import ru.shadowsparky.messenger.dagger.DaggerComponent
import ru.shadowsparky.messenger.dagger.DatabaseModule


class App : Application() {
    companion object {
        @JvmStatic lateinit var component: Component
    }

    override fun onCreate() {
        super.onCreate()
        daggerSetting()
        picassoSetting()
    }

    private fun daggerSetting() {
        component = DaggerComponent
                .builder()
                .additionalModule(AdditionalModule(applicationContext))
                .databaseModule(DatabaseModule(applicationContext))
                .build()
    }

    private fun picassoSetting() {
        val builder = Picasso.Builder(this)
        builder.downloader(OkHttp3Downloader(this, Integer.MAX_VALUE.toLong()))
        val built = builder.build()
        built.setIndicatorsEnabled(true)
        built.isLoggingEnabled = true
        Picasso.setSingletonInstance(built)
    }
}