/*
 * Copyright Samsonov Eugene(c) 2018.
 */

package ru.shadowsparky.messenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.DateUtils
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

open class MessagesAdapter(
        val data: MessagesResponse,
        val callback: (Int) -> Unit,
        val touch_callback: (Int, String, String, Int) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.MainViewHolder>() {
    @Inject lateinit var log: Logger
    @Inject lateinit var dateUtils: DateUtils
    private var TMPDate = ""

    init {
        App.component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MainViewHolder(v)
    }

    override fun getItemCount(): Int = data.response.items!!.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        if ((position == itemCount - 1) and (position != data.response.count!! - 1)) {
            callback(position + 1)
        }
        val profiles = data.response.profiles!!
        val item = data.response.items!![position]
        holder.user_data.text = "null"
        holder.message_data.text = item.last_message!!.text
        holder.time.text = dateUtils.fromUnixToTimeString(item.last_message.date!!)
        profiles.toObservable()
                .filter { it.id == item.conversation!!.peer!!.id }
                .subscribeBy(
                        onNext = {
                            holder.user_data.text = "${it.first_name} ${it.last_name}"
                            holder.card.setOnClickListener { _ ->
                                touch_callback(
                                        item.conversation!!.peer!!.id!!,
                                        holder.user_data.text.toString(),
                                        it.photo_100!!,
                                        it.online!!
                                )
                            }
                            picasso.load(it.photo_100).circle().into(holder.image)
                        },
                        onError = { log.print("Во время изменения Holder произошла критическая ошибка... $it") }
                )
//        log.print("CURSOR IS: $position. Last cursor: $itemCount")
//        log.print("TMP DATE: $TMPDate")
//        log.print("CURRENT DATE: ${dateUtils.fromUnixToDateString(item.last_message.date)}")
//        if (TMPDate != dateUtils.fromUnixToDateString(item.last_message.date)) {
//            TMPDate = dateUtils.fromUnixToDateString(item.last_message.date)
//            holder.date_card.visibility = VISIBLE
//            holder.date_text.text = TMPDate
//            log.print("ELEMENT VISIBLE")
//        } else {
//            holder.date_card.visibility = GONE
//            log.print("ELEMENT GONE")
//        }
//        log.print("________________________________")
    }

    fun addData(newData: MessagesResponse) {
        val TMP_MAX = itemCount

        data.response.profiles!!.addAll(newData.response.profiles!!)
        data.response.items!!.addAll(newData.response.items!!)
        // fixme: заменить notifyDataSetChanged на notifyItemRangeInserted.
//        notifyDataSetChanged()
        notifyItemRangeInserted(TMP_MAX, TMP_MAX + newData.response.items.size)
//        log.print("ELEMENT ADD! MAX: $TMP_MAX, SIZE: ${newData.response.items.size}")
    }

    open class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.messageitem_photo)
        val user_data: TextView = itemView.findViewById(R.id.messageitem_user_data)
        val message_data: TextView = itemView.findViewById(R.id.messageitem_user_message)
        val time: TextView = itemView.findViewById(R.id.messageitem_time)
        val card: CardView = itemView.findViewById(R.id.item_card)
        val date_card: CardView = itemView.findViewById(R.id.messageitem_date_card)
        val date_text: TextView = itemView.findViewById(R.id.messageitem_date_text)
    }
}