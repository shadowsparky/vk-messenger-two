/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.auth

import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils.Companion.TOKEN
import javax.inject.Inject

class AuthPresenter : Auth.Presenter {
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var preferences: SharedPreferencesUtils
    @Inject protected lateinit var model: Auth.Model
    private var view : Auth.View? = null

    init {
        App.component.inject(this)
    }

    override fun attachView(view: Auth.View) {
        this.view = view
    }

    override fun onAuthentication() {
        view!!.setLoading(true)
        val token = preferences.read(TOKEN)
        if (token.isNotBlank()) {
            view!!.navigateToMessagesList()
            log.print("TOKEN READED ${preferences.read(TOKEN)}")
        }
        view!!.setLoading(false)
    }
}