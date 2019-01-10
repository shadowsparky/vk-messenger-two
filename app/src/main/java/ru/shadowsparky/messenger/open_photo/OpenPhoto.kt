package ru.shadowsparky.messenger.open_photo

import android.widget.ImageView
import com.squareup.picasso.Target

interface OpenPhoto {
    interface View {
        fun getImageView() : ImageView
    }

    interface Presenter {
        fun attachView(view: View)
        fun onActivityOpen(url: String)
    }

    interface Model {
        fun getImage(url: String, imageView: ImageView)
    }
}