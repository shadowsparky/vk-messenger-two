/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.Gravity.LEFT
import android.view.Gravity.RIGHT
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.custom_views.ForwardView
import ru.shadowsparky.messenger.dialogs.AttachmentDialog
import ru.shadowsparky.messenger.response_utils.Attachments
import ru.shadowsparky.messenger.response_utils.pojos.*
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.EMPTY_STRING
import ru.shadowsparky.messenger.utils.Constansts.Companion.PHOTO
import ru.shadowsparky.messenger.utils.Constansts.Companion.RECORD_OPEN
import ru.shadowsparky.messenger.utils.Constansts.Companion.STICKER
import ru.shadowsparky.messenger.utils.Constansts.Companion.WALL
import ru.shadowsparky.messenger.utils.DateUtils
import ru.shadowsparky.messenger.utils.ImageWorker
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject
import kotlin.math.abs

@Suppress("ProtectedInFinal")
class HistoryAdapter(
        private val data: HistoryResponse,
        private val mActionListener: HistoryAdapter.ActionListener
) : RecyclerView.Adapter<HistoryAdapter.MainViewHolder>() {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var dateUtils: DateUtils
    @Inject protected lateinit var imageWorker: ImageWorker
    @Inject lateinit var selectedItems: HashMap<Int, VKMessage> protected set
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
        notifyItemRangeInserted(0, response.response.items.size)
    }

    override fun getItemCount(): Int = data.response.items!!.size

    override fun onBindViewHolder(holder: HistoryAdapter.MainViewHolder, position: Int) {
        if ((position == 0) and (itemCount < data.response.count!!)) {
            mActionListener.onScroll(itemCount)
            log.print("Message history loading request... position: $itemCount", false, TAG)
        }
        val item = data.response.items!![position]
        val conversation = data.response.conversations!![0]
        configureReading(item, conversation, holder)
        configureCard(holder.card, item, holder)
        configureDate(item, holder)
        holder.layout.setOnLongClickListener {
//            configureSelecting(item, position, holder.layout)
            return@setOnLongClickListener true
        }
        val url = getUrl(profiles, groups, item)
        if (url != EMPTY_STRING)
            picasso.load(url)
                    .circle()
                    .into(holder.image)
        holder.text.text = item.text
        holder.attachments.removeAllViews()
        includeAttachments(item, holder.attachments)
    }

    private fun configureSelecting(item: VKMessage, key: Int, layout: LinearLayout) {
        item.isSelected = !item.isSelected
        if (item.isSelected) {
            selectedItems[key] = item
            layout.setBackgroundColor(Color.parseColor("#292929"))
        } else {
            selectedItems.remove(key)
            layout.setBackgroundColor(Color.parseColor("#383838"))
        }
        mActionListener.onItemSelected(selectedItems)
    }

    fun getItemById(id: Int) : VKMessage? = data.response.items?.get(id)

    private fun getUrl(profiles: HashMap<Int, VKProfile>, groups: HashMap<Int, VKGroup>, item: VKMessage) : String {
        return when {
            profiles[item.from_id] != null -> profiles[item.from_id]!!.photo_100
            groups[abs(item.from_id!!)] != null -> groups[abs(item.from_id)]!!.photo_100
            else -> EMPTY_STRING
        }
    }

    private fun configureDate(item: VKMessage, holder: HistoryAdapter.MainViewHolder) {
        val todayDate = dateUtils.fromUnixToStrictDate(System.currentTimeMillis()/1000)
        val messageDate = dateUtils.fromUnixToStrictDate(item.date!!)
        if (todayDate > messageDate) {
            holder.time.text = dateUtils.fromUnixToDateAndTime(item.date)
        } else {
            holder.time.text = dateUtils.fromUnixToTimeString(item.date)
        }
    }

    private fun configureReading(item: VKMessage, conversation: VKConversation, holder: HistoryAdapter.MainViewHolder) {
        if (conversation.out_read < item.id!!) {
            holder.text.setTypeface(null, Typeface.BOLD)
        } else {
            holder.text.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun addProfiles(newData: HistoryResponse) {
        if (newData.response.profiles != null)
            for (item in newData.response.profiles)
                profiles[item.id] = item
        if (newData.response.groups != null)
            for (item in newData.response.groups)
                groups[item.id] = item
    }

    private fun includeAttachment(attachments: LinearLayout, info: Attachments) {
        try {
            val url = when (info) {
                is VKAttachmentsPhoto -> imageWorker.getOptimalImage(imageWorker.getHashmapCard(info.sizes))
                is VKAttachmentsSticker -> info.images[info.images.size - 1].url
                else -> EMPTY_STRING
            }
            if (url == EMPTY_STRING)
                throw RuntimeException()
            val image = ImageView(context)
            image.transitionName = context!!.getString(R.string.transition)
            picasso.load(url)
                    .resize(500, 500)
                    .centerCrop()
                    .into(image)
            image.setOnClickListener { mActionListener.onPhotoClicked(image, url!!) }
            attachments.addView(image)
        } catch (e: Exception) {
            log.printError("Ignored exception in History Adapter $e", false)
        }
    }

    private fun includePhoto(info: VKAttachments, attachments: LinearLayout) {
        includeAttachment(attachments, info.photo)
    }

    private fun includeReplyMessage(info: VKMessage, attachments: LinearLayout) {
        val current_item = info.reply_message
        if (current_item != null) {
            val layout = injector(attachments, current_item)
            includeAttachments(current_item, layout)
        }
    }

    private fun injector(attachments: LinearLayout, info: VKMessage) : LinearLayout {
        val ex = ForwardView(context!!, attachments)
        when {
            profiles[info.from_id] != null -> {
                val data = profiles[info.from_id]
                ex.setHeader("${data!!.first_name} ${data.last_name}")
                picasso.load(data.photo_100).circle().into(ex.image)
            }
            groups[abs(info.from_id!!)] != null -> {
                val data = groups[abs(info.from_id!!)]
                ex.setHeader("${data!!.name}")
                picasso.load(data.photo_100).circle().into(ex.image)
            }
        }
        ex.setText(info.text!!)
        return ex.attachments
    }

    private fun includeForwardMessage(info: VKMessage, attachments: LinearLayout) {
        val current_items = info.fwd_messages
        if (current_items != null) {
            for (item in current_items) {
                val layout = injector(attachments, item)
                includeAttachments(item, layout)
            }
        }
    }

    private fun includeAttachments(item: VKMessage, attachments: LinearLayout) {
        includeReplyMessage(item, attachments)
        includeForwardMessage(item, attachments)
        if (item.attachments != null) {
            for (attachment in item.attachments) {
                when(attachment.type) {
                    PHOTO -> includePhoto(attachment, attachments)
                    STICKER -> includeSticker(attachment, attachments)
                    WALL -> includeWall(attachment, attachments)
                }
            }
        }
    }

    private fun includeWall(info: VKAttachments, attachments: LinearLayout) {
        val button = MaterialButton(context, null, R.style.Widget_MaterialComponents_Button_OutlinedButton)
        button.text = RECORD_OPEN
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
            params.gravity = LEFT
            params.rightMargin = 60
        } else {
            holder.image.visibility = GONE
            params.gravity = RIGHT
            params.leftMargin = 60
        }
        card.layoutParams = params
    }

    interface ActionListener {
        fun onScroll(position: Int)
        fun onPhotoClicked(image: ImageView, url: String)
        fun onItemSelected(map: HashMap<Int, VKMessage>)
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val layout: LinearLayout = itemView.findViewById(R.id.message_history_layout)
        val text: TextView = itemView.findViewById(R.id.message_text)
        val card: CardView = itemView.findViewById(R.id.message_history_card)
        val time: TextView = itemView.findViewById(R.id.message_history_time)
        val attachments: LinearLayout = itemView.findViewById(R.id.message_history_attachments)
        val image: ImageView = itemView.findViewById(R.id.message_history_user_card)
    }
}