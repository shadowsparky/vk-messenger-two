/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.response_utils.pojos.VKGroup
import ru.shadowsparky.messenger.response_utils.pojos.VKItems
import ru.shadowsparky.messenger.response_utils.pojos.VKProfile
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.EMPTY_STRING
import ru.shadowsparky.messenger.utils.Constansts.Companion.PHOTO_NOT_FOUND
import ru.shadowsparky.messenger.utils.Constansts.Companion.STATUS_HIDE
import ru.shadowsparky.messenger.utils.Constansts.Companion.VK_PEER_CHAT
import ru.shadowsparky.messenger.utils.Constansts.Companion.VK_PEER_GROUP
import ru.shadowsparky.messenger.utils.DateUtils
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject
import kotlin.math.abs

class MessagesAdapter(
    private val data: MessagesResponse,
    private val mActionListener: ActionListener
) : RecyclerView.Adapter<MessagesAdapter.MainViewHolder>() {
    // protected a не private ПОТОМУ ЧТО Я ТАК ЗАХОТЕЛ. ВЫ НЕ ИМЕЕТЕ ПРАВА МЕНЯ СУДИТЬ, ВЫ НИЧЕГО НЕ ЗНАЕТЕ
    @Inject protected lateinit var log: Logger
    @Inject protected lateinit var dateUtils: DateUtils
    private val profiles = HashMap<Int, VKProfile>()
    private val groups = HashMap<Int, VKGroup>()
    private val TAG = javaClass.name

    init {
        App.component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        addProfiles(data)
        return MainViewHolder(v)
    }

    override fun getItemCount(): Int = data.response!!.items.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = data.response!!.items[position]
        if ((position == itemCount - 1) and (position != data.response.count - 1)) {
            mActionListener.onScroll(position + 1)
        }
        holder.user_data.text = EMPTY_STRING
        when {
            item.conversation.peer.type == VK_PEER_CHAT -> chatDialog(item, holder.user_data, holder.card, holder.image)
            profiles[item.conversation.peer.id] != null -> userDialog(profiles[item.conversation.peer.id]!!, holder.user_data, holder.card, holder.image)
            groups[abs(item.conversation.peer.id!!)] != null -> groupDialog(groups[abs(item.conversation.peer.id)]!!, holder.user_data, holder.card, holder.image)
        }
        if ((groups[abs(item.conversation.peer.id!!)] == null) and (item.conversation.peer.type != VK_PEER_CHAT)) {
            setText(item, holder)
        } else {
            holder.message_data.text = item.last_message.text!!
        }
        setDate(item, holder)
        setReading(item, holder, position)
    }

    private fun setReading(item: VKItems, holder: MainViewHolder, position: Int) {
        if (item.conversation.in_read != item.conversation.out_read) {
            holder.user_data.setTypeface(null, Typeface.BOLD)
            log.print(item.last_message.peer_id.toString()+holder.user_data.text, false, TAG);
            holder.message_data.setTypeface(null, Typeface.BOLD)
            log.print("BOLD: $position", false, TAG)
        } else {

            holder.user_data.setTypeface(null, Typeface.NORMAL)
            log.print(item.last_message.peer_id.toString()+holder.user_data.text, false, TAG)
//            item.last_message.from_id;
            holder.message_data.setTypeface(null, Typeface.NORMAL)
            log.print("normal: $position", false, TAG)
        }
    }

    private fun setText(item: VKItems, holder: MainViewHolder) {
        if (item.last_message.out == 1) {
            holder.message_data.text = "Вы: ${item.last_message.text}"
        } else if (profiles[item.conversation.peer.id] != null) {
            holder.message_data.text = "${profiles[item.conversation.peer.id]!!.first_name}: ${item.last_message.text}"
        }
    }

    private fun setDate(item: VKItems, holder: MainViewHolder) {
        val todayDate = dateUtils.fromUnixToStrictDate(System.currentTimeMillis()/1000)
        val messageDate = dateUtils.fromUnixToStrictDate(item.last_message.date!!)
        if (todayDate > messageDate) {
            holder.time.text = dateUtils.fromUnixToDateAndTime(item.last_message.date)
        } else {
            holder.time.text = dateUtils.fromUnixToTimeString(item.last_message.date)
        }
    }

    private fun userDialog(item: VKProfile, user_data: TextView, card: CardView, image: ImageView) {
        user_data.text = "${item.first_name} ${item.last_name}"
        val time = if (item.last_seen != null) {
            item.last_seen.time
        } else {
            STATUS_HIDE
        }
        card.setOnClickListener {
            mActionListener.onCardClicked(
                item.id,
                user_data.text.toString(),
                item.photo_100,
                item.online,
                time
            )
        }
        picasso.load(item.photo_100).circle().into(image)
    }

    private fun chatDialog(item: VKItems, user_data: TextView, card: CardView, image: ImageView) {
        val conversation = item.conversation
        var photo = ""
        user_data.text = conversation .chat_settings.title
        photo = if (conversation.chat_settings.photo == null)
            PHOTO_NOT_FOUND
        else
            conversation.chat_settings.photo.photo_100
        card.setOnClickListener {
            mActionListener.onCardClicked(
                conversation.peer.id!!,
                user_data.text.toString(),
                photo,
                STATUS_HIDE,
                STATUS_HIDE
            )
        }
        picasso.load(photo).circle().into(image)
    }

    private fun groupDialog(item: VKGroup, user_data: TextView, card: CardView, image: ImageView) {
        user_data.text = item.name
        val photo = item.photo_100 ?: PHOTO_NOT_FOUND
        card.setOnClickListener {
            mActionListener.onCardClicked(
                -item.id,
                user_data.text.toString(),
                photo,
                STATUS_HIDE,
                STATUS_HIDE
            )
        }
        picasso.load(photo).circle().into(image)
    }

    fun addData(newData: MessagesResponse) {
//        val TMP_MAX = itemCount
        data.response!!.profiles.addAll(newData.response!!.profiles)
        data.response.items.addAll(newData.response.items)
        addProfiles(newData)
        notifyDataSetChanged()
//        notifyItemRangeInserted(TMP_MAX, TMP_MAX + newData.response.items.size)
    }

    private fun addProfiles(newData: MessagesResponse) {
        if (newData.response!!.profiles != null)
            for (item in newData.response!!.profiles)
                profiles[item.id] = item
        if (newData.response.groups != null)
            for (item in newData.response.groups)
                groups[item.id] = item
    }

    interface ActionListener {
        fun onCardClicked(peer_id: Int, user_data: String, photo: String, user_status: Int, time: Int)
        fun onScroll(position: Int = 0)
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.messageitem_photo)
        val user_data: TextView = itemView.findViewById(R.id.messageitem_user_data)
        val message_data: TextView = itemView.findViewById(R.id.messageitem_user_message)
        val time: TextView = itemView.findViewById(R.id.messageitem_time)
        val card: CardView = itemView.findViewById(R.id.item_card)
    }
}