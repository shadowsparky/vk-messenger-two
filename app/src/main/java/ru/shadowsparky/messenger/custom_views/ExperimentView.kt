/*
 * Copyright Samsonov Eugene (c) 2018
 */

package ru.shadowsparky.messenger.custom_views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ru.shadowsparky.messenger.R

@Deprecated("For Fun")
class ExperimentView(context: Context, parent: LinearLayout) : LinearLayout(context) {
    private val view: View
    val image: ImageView
    val attachments: LinearLayout
    val header: TextView
    val text: TextView


    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.fwd_layout, parent, false)
        image = view.findViewById(R.id.fwd_image)
        header = view.findViewById(R.id.fwd_header)
        text = view.findViewById(R.id.fwd_text)
        attachments = view.findViewById(R.id.fwd_attachments)
        parent.addView(view)
    }

    fun setText(text: String) { this.text.text = text }
    fun setHeader(text: String) { header.text = text }
}