package ru.shadowsparky.messenger.open_photo

import android.widget.ImageView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.hendraanggrian.pikasso.picasso
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ru.shadowsparky.messenger.utils.App
import javax.inject.Inject

class OpenPhotoModel : OpenPhoto.Model {
    private var photoViewAttacher: PhotoViewAttacher? = null
    init {
        App.component.inject(this)
    }

    override fun getImage(url: String, imageView: ImageView) {
        picasso.load(url).into(imageView)
        photoViewAttacher = PhotoViewAttacher(imageView)
        photoViewAttacher!!.update()
    }
}