package ru.shadowsparky.messenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import io.reactivex.Observable
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.response_utils.responses.MessagesResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import javax.inject.Inject

open class MessagesAdapter(
        val data: MessagesResponse,
        val callback: (Int) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.MainViewHolder>() {
    @Inject
    lateinit var log: Logger

    init {
        App.component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return MainViewHolder(v)
    }

    override fun getItemCount(): Int = data.response.items!!.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        if ((position == itemCount - 1) and (position != data.response.count)) {
            callback(position)
            log.print("Scrolling callback working. Position: $position")
        }
        log.print("Current cursor in: $position")
        val profiles = data.response.profiles!!
        val item = data.response.items!!.get(position)
        var check = false
        for (i in profiles) {
            if (!check) {
                if (item.conversation!!.peer!!.id == i.id) {
                    holder.user_data.text = "${i.first_name} ${i.last_name}"
                    picasso.load(i.photo_100).circle().into(holder.image)
                    holder.image.visibility = VISIBLE
                    check = true
                } else {
                    holder.user_data.text = "Группа (не обрабатывается)"
                    holder.image.visibility = GONE
                }
            }
        }
        holder.message_data.text = item.last_message!!.text
    }

    fun addData(newData: MessagesResponse) {
        data.response.profiles!!.addAll(newData.response.profiles!!)
//        data.response.groups!!.addAll(newData.response.groups!!)
        data.response.items!!.addAll(newData.response.items!!)
        notifyDataSetChanged()
    }

    open class MainViewHolder : RecyclerView.ViewHolder {
        val image: ImageView
        val user_data: TextView
        val message_data: TextView

        constructor(itemView: View) : super(itemView) {
            image = itemView.findViewById(R.id.messageitem_photo)
            user_data = itemView.findViewById(R.id.messageitem_user_data)
            message_data = itemView.findViewById(R.id.messageitem_user_message)
        }
    }
}