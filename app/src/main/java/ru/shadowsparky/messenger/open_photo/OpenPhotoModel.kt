package ru.shadowsparky.messenger.open_photo

import android.widget.ImageView
import com.hendraanggrian.pikasso.picasso
import com.squareup.picasso.Picasso
import ru.shadowsparky.messenger.utils.App
import javax.inject.Inject

class OpenPhotoModel : OpenPhoto.Model {

    init {
        App.component.inject(this)
    }

    override fun getImage(url: String, image: ImageView) {
        picasso.load(url).into(image)
    }
}