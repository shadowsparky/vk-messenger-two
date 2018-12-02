package ru.shadowsparky.messenger.open_post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_post_shower.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.response_utils.pojos.VKAttachmentsWall
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.WALL_DATA
import javax.inject.Inject

class OpenPostView : AppCompatActivity(), OpenPost.View {
    @Inject protected lateinit var presenter: OpenPostPresenter
    private var data: VKAttachmentsWall? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_shower)
        App.component.inject(this)
        presenter.attachView(this, this)
        data = intent.getSerializableExtra(WALL_DATA) as VKAttachmentsWall?
        if (data == null)
            finish()
        presenter.onActivityLoading(data!!, post_content)
    }
}
