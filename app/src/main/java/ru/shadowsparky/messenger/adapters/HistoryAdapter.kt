/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.*
import android.view.View.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.dialogs.AttachmentDialog
import ru.shadowsparky.messenger.open_post.OpenPostView
import ru.shadowsparky.messenger.response_utils.pojos.*
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.EMPTY_STRING
import ru.shadowsparky.messenger.utils.Constansts.Companion.WALL_DATA
import ru.shadowsparky.messenger.utils.DateUtils
import ru.shadowsparky.messenger.utils.Logger
import java.lang.RuntimeException
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
    private val profiles = HashMap<Int, VKProfile>()
    private val groups = HashMap<Int, VKGroup>()
    private var context: Context? = null
    private val TAG = javaClass.name

    init {
        App.component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_history_item, parent, false)
        context = parent.context
        addProfiles(data)
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
        addProfiles(response)
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
        configureCard(holder.card, item, holder)
        holder.text.text = item.text
        dateUtils.fromUnixToDateAndTimeCalendar(item.date!!)
        val todayDate = dateUtils.fromUnixToStrictDate(System.currentTimeMillis()/1000)
        val messageDate = dateUtils.fromUnixToStrictDate(item.date)
        if (todayDate > messageDate) {
            holder.time.text = dateUtils.fromUnixToDateAndTime(item.date)
        } else {
            holder.time.text = dateUtils.fromUnixToTimeString(item.date)
        }
        picasso.load(profiles[item.from_id]?.photo_100)
                .circle()
                .into(holder.image)
        includeAttachments(item, holder)
    }

    private fun addProfiles(newData: HistoryResponse) {
        for (item in newData.response.profiles!!)
            profiles[item.id] = item
//        for (item in newData.response.)
//            groups[item.id] = item
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

    private fun includeAttachment(attachments: LinearLayout, info: Any, callback: (ImageView, String) -> Unit = { _, _ -> }) {
        try {
            val layout = LinearLayout(context)
            var url = when (info) {
                is VKAttachmentsPhoto -> getOptimalImage(getHashmapCard(info.sizes))
                is VKAttachmentsSticker -> info.images[info.images.size - 1].url
                else -> EMPTY_STRING
            }
            if (url == EMPTY_STRING)
                throw RuntimeException()
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
        includeAttachment(attachments, info.photo, touch_photo_callback)
    }

    private fun includeReplyMessage(info: VKMessage, holder: HistoryAdapter.MainViewHolder) {
        val current_item = info.reply_message
        if (current_item != null) {
            val tw = TextView(context)
            tw.text = current_item.text
            tw.height = 100
            tw.width = 100
            log.print("Reply message handled ${tw.text}", false, TAG)
            holder.attachments.addView(tw)
            includeAttachments(current_item, holder)
        }
    }

    private fun includeForwardMessage(info: VKMessage, holder: HistoryAdapter.MainViewHolder) {
        val current_items = info.fwd_messages
        if (current_items != null) {
            for (item in current_items) {
                val tw = TextView(context)
                tw.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                tw.text = item.text
                tw.height = 100
                tw.width = 100
                log.print("Reply forward handled ${tw.text}", false, TAG)
                holder.attachments.addView(tw)
                includeAttachments(item, holder)
            }
        }
    }

    private fun includeAttachments(item: VKMessage, holder: HistoryAdapter.MainViewHolder) {
        includeReplyMessage(item, holder)
        includeForwardMessage(item, holder)
        if (item.attachments != null) {
            holder.attachments.removeAllViews()
            for (attachment in item.attachments) {
                when(attachment.type) {
                    "photo" -> includePhoto(attachment, holder.attachments)
                    "sticker" -> includeSticker(attachment, holder.attachments)
                    "wall" -> includeWall(attachment, holder.attachments)
                }
            }
        }
    }

    private fun includeWall(info: VKAttachments, attachments: LinearLayout) {
//        val newContext = ContextThemeWrapper(context, R.style.Widget_MaterialComponents_Button_OutlinedButton)
        val button = MaterialButton(context, null, R.style.Widget_MaterialComponents_Button_OutlinedButton)
        button.text = "Открыть запись"
        button.width = 500
        button.height = 200
        button.gravity = Gravity.CENTER
        button.textAlignment = TEXT_ALIGNMENT_CENTER
        button.setOnClickListener {
            if (info.wall != null) {
                val dialog = AttachmentDialog(context!!, info.wall)
                dialog.show()
            }
        }
        attachments.addView(button)
    }

    private fun includeSticker(info: VKAttachments, attachments: LinearLayout) {
        includeAttachment(attachments, info.sticker)
    }

    private fun configureCard(card: CardView, item: VKMessage, holder: MainViewHolder) {
        val params = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        params.setMargins(8,8,8,8)
        if (item.out == 0) {
            holder.image.visibility = VISIBLE
            params.gravity = Gravity.LEFT
            params.rightMargin = 60
        } else {
            holder.image.visibility = GONE
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
        val image: ImageView = itemView.findViewById(R.id.message_history_user_card)
    }
}