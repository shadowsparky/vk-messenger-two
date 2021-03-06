/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.auth

interface Auth {

    interface View {
        fun setLoading(result: Boolean)
        fun navigateToMessagesList()
    }

    interface Presenter {
        fun attachView(view: View)
        fun onAuthentication()
    }

    interface Model
}