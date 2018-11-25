package ru.shadowsparky.messenger.open_photo

import android.widget.ImageView

interface OpenPhoto {
    interface View {

    }

    interface Presenter {
        fun attachView(view: View)
        fun onActivityOpen(url: String, image: ImageView)
    }

    interface Model {
        fun getImage(url: String, image: ImageView)
    }
}