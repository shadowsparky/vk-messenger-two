package ru.shadowsparky.messenger.auth

interface Auth {

    interface View {
        fun setLoading(result: Boolean)
        fun navigateToMessagesList(token: String)
    }

    interface Presenter {
        fun onAuthentication()
    }

    interface Model {

    }

}