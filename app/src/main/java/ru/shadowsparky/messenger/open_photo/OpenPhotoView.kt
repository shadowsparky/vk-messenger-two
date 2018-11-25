package ru.shadowsparky.messenger.open_photo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_open_photo_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.EMPTY_STRING
import ru.shadowsparky.messenger.utils.Constansts.Companion.URL
import javax.inject.Inject

class OpenPhotoView : AppCompatActivity(), OpenPhoto.View {
    @Inject protected lateinit var presenter: OpenPhotoPresenter
    private var url: String = EMPTY_STRING
    init {
        App.component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_photo_view)
        url = intent.getStringExtra(URL)
        presenter.attachView(this)
        if (url != EMPTY_STRING)
            presenter.onActivityOpen(url, open_photo_image)
    }
}
