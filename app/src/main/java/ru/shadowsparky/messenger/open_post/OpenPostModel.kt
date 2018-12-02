package ru.shadowsparky.messenger.open_post

import com.github.chrisbanes.photoview.PhotoView
import com.hendraanggrian.pikasso.picasso

class OpenPostModel : OpenPost.Model {
    override fun getImageRequest(photo: PhotoView, url: String) {
        picasso.load(url).into(photo)
    }
}