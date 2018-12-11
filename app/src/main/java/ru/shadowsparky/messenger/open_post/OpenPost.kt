package ru.shadowsparky.messenger.open_post

import android.content.Context
import android.widget.LinearLayout
import com.github.chrisbanes.photoview.PhotoView
import ru.shadowsparky.messenger.response_utils.pojos.VKAttachmentsWall

@Deprecated("Не используется и скоро будет удалено. Заменен на dialogs.AttachmentDialog}")
interface OpenPost {
    interface View {

    }

    interface Presenter {
        fun attachView(view: View, context: Context)
        fun onActivityLoading(data: VKAttachmentsWall, layout: LinearLayout)
    }

    interface Model {
        fun getImageRequest(photo: PhotoView, url: String)
    }
}