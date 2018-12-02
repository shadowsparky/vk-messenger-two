package ru.shadowsparky.messenger.open_photo

import android.widget.ImageView
import com.github.chrisbanes.photoview.PhotoViewAttacher
import com.hendraanggrian.pikasso.picasso
import com.squareup.picasso.Picasso
import ru.shadowsparky.messenger.utils.App
import javax.inject.Inject

class OpenPhotoModel : OpenPhoto.Model {
    private var photoViewAttacher: PhotoViewAttacher? = null
    init {
        App.component.inject(this)
    }

    override fun getImage(url: String, image: ImageView) {
        picasso.load(url).into(image)
        photoViewAttacher = PhotoViewAttacher(image)
        photoViewAttacher!!.update()
    }
}