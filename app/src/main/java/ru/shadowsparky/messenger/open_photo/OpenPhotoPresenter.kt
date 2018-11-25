package ru.shadowsparky.messenger.open_photo

import android.widget.ImageView
import ru.shadowsparky.messenger.utils.App
import javax.inject.Inject

class OpenPhotoPresenter : OpenPhoto.Presenter {
    private var view: OpenPhoto.View? = null
    @Inject protected lateinit var model: OpenPhotoModel

    init {
        App.component.inject(this)
    }

    override fun attachView(view: OpenPhoto.View) {
        this.view = view
    }


    override fun onActivityOpen(url: String, image: ImageView) {
        model.getImage(url, image)
    }
}