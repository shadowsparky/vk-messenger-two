/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.pikasso.picasso
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.response_utils.pojos.VKAttachments
import ru.shadowsparky.messenger.response_utils.pojos.VKMessage
import ru.shadowsparky.messenger.response_utils.pojos.VKPhotoSize
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.DateUtils
import ru.shadowsparky.messenger.utils.Logger
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


class HistoryAdapter(
        val data: HistoryResponse,
        val scroll_callback: (Int) -> Unit,
        val touch_photo_callback: (ImageView, String) -> Unit,
        val user_id: Int
) : RecyclerView.Adapter<HistoryAdapter.MainViewHolder>() {
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var dateUtils: DateUtils
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
        data.response.items!!.reverse()
        data.response.conversations!!.reverse()
        data.response.profiles!!.reverse()
    }

    fun addData(response: HistoryResponse) {
        reverse()
        data.response.items!!.addAll(response.response.items!!)
        data.response.conversations!!.addAll(response.response.conversations!!)
        data.response.profiles!!.addAll(response.response.profiles!!)
        reverse()
        log.print("ADD DATA: ${data.response.items.size} ${response.response.items.size}")
        notifyItemRangeInserted(0, response.response.items.size)
    }

    override fun getItemCount(): Int = data.response.items!!.size

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

    private fun getHashmapCard(list: ArrayList<VKPhotoSize>) : HashMap<String, String> {
        val hashmap = HashMap<String, String>()
        for (element in list)
            hashmap[element.type] = element.url
        return hashmap
    }

    private fun getOptimalImage(map: HashMap<String, String>) : String? = when {
        map["w"] != null -> map["w"]!!
        map["z"] != null -> map["z"]!!
        map["y"] != null -> map["y"]!!
        map["x"] != null -> map["x"]!!
        map["m"] != null -> map["m"]!!
        map["s"] != null -> map["s"]!!
        else -> null
    }

    private fun includeAttachment(attachments: LinearLayout, info: VKAttachments, callback: (ImageView, String) -> Unit = { _, _ -> }) {
        try {
            val layout = LinearLayout(context)
            val url = getOptimalImage(getHashmapCard(info.photo.sizes))
            layout.orientation = LinearLayout.VERTICAL
            val image = ImageView(context)
            image.transitionName = context!!.getString(R.string.transition)
            picasso.load(url).into(image)
            image.setOnClickListener { callback(image, url!!) }
            layout.addView(image)
            attachments.addView(layout)
        } catch (e: Exception) {
            log.printError("Ignored exception in History Adapter $e", false)
        }
    }

    private fun includePhoto(info: VKAttachments, attachments: LinearLayout) {
        if (info.photo != null)
            includeAttachment(attachments, info, touch_photo_callback)
    }

    private fun includeAttachments(item: VKMessage, holder: HistoryAdapter.MainViewHolder) {
        if (item.attachments != null) {
            holder.attachments.removeAllViews()
            for (attachment in item.attachments) {
                when(attachment.type) {
                    "photo" -> includePhoto(attachment, holder.attachments)
                    "sticker" -> includeSticker(attachment, holder.attachments)
                }
            }
        }
    }

    private fun includeSticker(info: VKAttachments, attachments: LinearLayout) {
        includeAttachment(attachments, info)
    }

    private fun configureCard(card: CardView, item: VKMessage) {
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
        val attachments: LinearLayout = itemView.findViewById(R.id.message_history_attachments)
    }
}