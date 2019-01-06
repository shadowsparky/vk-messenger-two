package ru.shadowsparky.messenger.open_photo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.chrisbanes.photoview.PhotoViewAttacher
import kotlinx.android.synthetic.main.activity_open_photo_view.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.DEAD
import ru.shadowsparky.messenger.utils.Constansts.Companion.EMPTY_STRING
import ru.shadowsparky.messenger.utils.Constansts.Companion.URL
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

class OpenPhotoView : AppCompatActivity(), OpenPhoto.View {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var presenter: OpenPhotoPresenter
    @Inject protected lateinit var log: Logger
    private val TAG = javaClass.name
    private var url: String = EMPTY_STRING
    init {
        App.component.inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        log.print("$TAG $DEAD", false, TAG)
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
