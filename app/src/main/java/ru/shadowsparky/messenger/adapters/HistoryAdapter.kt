/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.response_utils.pojos.VKAttachments
import ru.shadowsparky.messenger.response_utils.pojos.VKMessage
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.DateUtils
import ru.shadowsparky.messenger.utils.Logger
import java.util.*
import javax.inject.Inject

class HistoryAdapter(
        val data: HistoryResponse,
        val scroll_callback: (Int) -> Unit,
        val user_id: Int
) : RecyclerView.Adapter<HistoryAdapter.MainViewHolder>() {
    @Inject lateinit var log: Logger
    @Inject lateinit var dateUtils: DateUtils
    private var context: Context? = null

    init {
        App.component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_history_item, parent, false)
        context = parent.context
        return HistoryAdapter.MainViewHolder(v)
    }

    fun reverse() {
        data.response!!.items!!.reverse()
        data.response.conversations!!.reverse()
        data.response.profiles!!.reverse()
    }

    fun addData(response: HistoryResponse) {
        reverse()
        data.response!!.items!!.addAll(response.response!!.items!!)
        data.response.conversations!!.addAll(response.response.conversations!!)
        data.response.profiles!!.addAll(response.response.profiles!!)
        reverse()
        notifyItemRangeInserted(0, response.response.items!!.size)
    }

    override fun getItemCount(): Int = data.response!!.items!!.size

    override fun onBindViewHolder(holder: HistoryAdapter.MainViewHolder, position: Int) {
        if ((position == 0) and (itemCount < data.response.count!!)) {
            scroll_callback(itemCount)
            log.print("Message history loading request... position: $itemCount")
        }
        val item = data.response.items!![position]
        configureCard(holder.card, item)
        holder.text.text = item.text
        dateUtils.fromUnixToDateAndTimeCalendar(item.date!!)
        val todayDate = dateUtils.fromUnixToStrictDate(System.currentTimeMillis()/1000)
        val messageDate = dateUtils.fromUnixToStrictDate(item.date)
        if (todayDate > messageDate) {
            holder.time.text = dateUtils.fromUnixToDateAndTime(item.date)
        } else {
            holder.time.text = dateUtils.fromUnixToTimeString(item.date)
        }
        includeAttachments(item, holder)
    }

    private fun includePhoto(attachment: VKAttachments, holder: HistoryAdapter.MainViewHolder) {
        if (attachment.photo != null) {
            val url = attachment.photo.sizes[attachment.photo.sizes.size - 1].url
            val image = ImageView(context)
            holder.attachment_container.addView(image)
            picasso.load(url).into(image)
            holder.attachment_container.visibility = VISIBLE
        }
    }

    private fun includeAttachments(item: VKMessage, holder: HistoryAdapter.MainViewHolder) {
        if (item.text == "")
            holder.text.visibility = GONE
        for (attachment in item.attachments) {
            if (attachment.sticker != null) {
                includeSticker(attachment, holder)
            }

            if (attachment.photo != null) {
                includePhoto(attachment, holder)
            }
        }
    }

    private fun includeSticker(attachment: VKAttachments, holder: HistoryAdapter.MainViewHolder) {
        if (attachment.sticker != null) {
            val url = attachment.sticker.images[attachment.sticker.images.size - 1].url
            val image = ImageView(context)
            holder.attachment_container.addView(image)
            picasso.load(url).into(image)
            holder.attachment_container.visibility = VISIBLE
        }
    }

    protected fun configureCard(card: CardView, item: VKMessage) {
        val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        params.setMargins(8,8,8,8)
        if (item.out == 0) {
            params.gravity = Gravity.LEFT
            params.rightMargin = 60
        } else {
            params.gravity = Gravity.RIGHT
            params.leftMargin = 60
        }
        card.layoutParams = params
    }

    open class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.message_text)
        val card: CardView = itemView.findViewById(R.id.message_history_card)
        val time: TextView = itemView.findViewById(R.id.message_history_time)
        val attachment_container: LinearLayout = itemView.findViewById(R.id.message_history_attachment)
    }
}