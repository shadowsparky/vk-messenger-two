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
        val touch_callback: (Int) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.MainViewHolder>() {
    @Inject
    lateinit var log: Logger
    @Inject
    lateinit var dateUtils: DateUtils

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
            callback(position)
        }
        val profiles = data.response.profiles!!
        val item = data.response.items!!.get(position)
        holder.user_data.text = "null"
        holder.message_data.text = item.last_message!!.text
        holder.time.text = dateUtils.fromUnixToTimeString(item.last_message.date!!)
        holder.card.setOnClickListener {
            touch_callback(item.conversation!!.peer!!.id!!)
        }
        profiles.toObservable()
                .filter { it.id == item.conversation!!.peer!!.id }
                .subscribeBy(
                        onNext = {
                            holder.user_data.text = "${it.first_name} ${it.last_name}"
                            picasso.load(it.photo_100).circle().into(holder.image)
                        },
                        onError = { log.print("Во время изменения Holder произошла критическая ошибка... $it") }
                )
    }


    fun addData(newData: MessagesResponse) {
        data.response.profiles!!.addAll(newData.response.profiles!!)
        data.response.items!!.addAll(newData.response.items!!)
        notifyDataSetChanged()
    }

    open class MainViewHolder : RecyclerView.ViewHolder {
        val image: ImageView
        val user_data: TextView
        val message_data: TextView
        val time: TextView
        val card: CardView

        constructor(itemView: View) : super(itemView) {
            image = itemView.findViewById(R.id.messageitem_photo)
            user_data = itemView.findViewById(R.id.messageitem_user_data)
            message_data = itemView.findViewById(R.id.messageitem_user_message)
            time = itemView.findViewById(R.id.messageitem_time)
            card = itemView.findViewById(R.id.item_card)
        }
    }
}