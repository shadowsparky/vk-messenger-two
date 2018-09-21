/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.auth

import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils.Companion.TOKEN
import javax.inject.Inject

class AuthPresenter(val view : Auth.View) : Auth.Presenter {
    @Inject
    lateinit var log: Logger
    var test: Logger? = null
    @Inject
    lateinit var preferences: SharedPreferencesUtils

    lateinit var model: Auth.Model

    init {
        App.component.inject(this)
        model = AuthModel()
    }

    override fun onAuthentication() {
        view.setLoading(true)
        val token = preferences.read(TOKEN)
        if (token.isNotBlank()) {
            view.navigateToMessagesList()
            log.print("TOKEN READED ${preferences.read(TOKEN)}")
        }
        view.setLoading(false)
    }
}