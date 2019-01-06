package ru.shadowsparky.messenger.open_photo

import android.widget.ImageView
import ru.shadowsparky.messenger.utils.App
import javax.inject.Inject

class OpenPhotoPresenter : OpenPhoto.Presenter {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
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