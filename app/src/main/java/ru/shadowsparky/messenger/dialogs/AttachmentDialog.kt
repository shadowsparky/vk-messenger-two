/*
 * Copyright Samsonov Eugene (c) 2018
 */

package ru.shadowsparky.messenger.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.github.chrisbanes.photoview.PhotoView
import com.hendraanggrian.pikasso.picasso
import kotlinx.android.synthetic.main.activity_post_shower.*
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.response_utils.pojos.VKAttachmentsWall

class AttachmentDialog : Dialog {
    private var data: VKAttachmentsWall? = null

    constructor (context: Context, data: VKAttachmentsWall) : super(context) {
        this.data = data
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_shower)
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