/*
 * Copyright Samsonov Eugene (c) 2018
 */

package ru.shadowsparky.messenger.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.WorkSource
import android.widget.ImageView
import android.widget.TextView
import com.github.chrisbanes.photoview.PhotoView
import com.hendraanggrian.pikasso.picasso
import kotlinx.android.synthetic.main.activity_post_shower.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.custom_views.ForwardView
import ru.shadowsparky.messenger.open_photo.OpenPhotoView
import ru.shadowsparky.messenger.response_utils.pojos.VKAttachmentsWall
import ru.shadowsparky.messenger.response_utils.pojos.VKGroup
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts
import ru.shadowsparky.messenger.utils.ImageWorker
import javax.inject.Inject

class AttachmentDialog : Dialog {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    private val data: VKAttachmentsWall
//    private var context: Context
    @Inject protected lateinit var imageWorker: ImageWorker

    constructor (context: Context, data: VKAttachmentsWall) : super(context) {
        this.data = data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_shower)
        App.component.inject(this)
        injectWallAttachments(data)
    }

    private fun injectWallAttachments(data: VKAttachmentsWall?) {
        if (data != null) {
            if (data.text != null) {
                val text = TextView(context)
                text.text = data.text
                post_content.addView(text)
            }
            if (data.attachments != null) {
                for (item in data.attachments) {
                    val image = PhotoView(context)
                    image.scaleType = ImageView.ScaleType.FIT_CENTER
                    image.adjustViewBounds = true
                    if (item.photo != null)
                        picasso.load(item.photo.sizes[item.photo.sizes.size - 1].url).into(image)
                    image.setOnClickListener {
                        val view = Intent(context, OpenPhotoView::class.java)
                        val url = imageWorker.getOptimalImage(imageWorker.getHashmapCard(item.photo.sizes))
                        view.putExtra(Constansts.URL, url)
                        context.startActivity(view)
                    }
                    post_content.addView(image)
                }
            }
            if (data.copy_history != null) {
                for (item in data.copy_history)
                    injectWallAttachments(item)
            }
        }
    }
}