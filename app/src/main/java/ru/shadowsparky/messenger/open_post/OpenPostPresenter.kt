package ru.shadowsparky.messenger.open_post

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.chrisbanes.photoview.PhotoView
import ru.shadowsparky.messenger.response_utils.pojos.VKAttachmentsWall
import ru.shadowsparky.messenger.utils.App
import javax.inject.Inject

@Deprecated("Не используется и скоро будет удалено. Заменен на dialogs.AttachmentDialog}")
class OpenPostPresenter : OpenPost.Presenter {
    private var view: OpenPost.View? = null
    private var context: Context? = null
    @Inject protected lateinit var model: OpenPostModel

    init {
        App.component.inject(this)
    }

    override fun attachView(view: OpenPost.View, context: Context) {
        this.view = view
        this.context = context
    }

    override fun onActivityLoading(data: VKAttachmentsWall, layout: LinearLayout) {
        if (data.text != null) {
            val text = TextView(context)
            text.text = data.text
            layout.addView(text)
        }
        if (data.attachments != null) {
            for (item in data.attachments) {
                val image = PhotoView(context)
                image.scaleType = ImageView.ScaleType.FIT_CENTER
                image.adjustViewBounds = true
                if (item.photo != null)
                    model.getImageRequest(image, item.photo.sizes[item.photo.sizes.size - 1].url)
                layout.addView(image)
            }
        }
        if (data.copy_history != null) {
            for (item in data.copy_history)
                onActivityLoading(item, layout)
        }
    }
}