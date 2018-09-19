package ru.shadowsparky.messenger.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.response_utils.responses.HistoryResponse
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import java.util.*
import javax.inject.Inject

class HistoryAdapter(
        val data: HistoryResponse,
        val scroll_callback: (Int) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.MainViewHolder>() {
    @Inject lateinit var log: Logger

    var current_cursor = 0
        private set

    init {
        App.component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_history_item, parent, false)
        return HistoryAdapter.MainViewHolder(v)
    }

    fun reverse() {
        data.response!!.items!!.reverse()
        data.response.conversations!!.reverse()
        data.response.profiles!!.reverse()
        notifyDataSetChanged()
    }

    fun addData(response: HistoryResponse) {
        reverse()
        data.response!!.items!!.addAll(response.response!!.items!!)
        data.response.conversations!!.addAll(response.response.conversations!!)
        data.response.profiles!!.addAll(response.response.profiles!!)
        reverse()
    }

    override fun getItemCount(): Int = data.response!!.items!!.size

    override fun onBindViewHolder(holder: HistoryAdapter.MainViewHolder, position: Int) {
        if (position == 0) {
            scroll_callback(current_cursor + 1)
            current_cursor += 20
            log.print("LOG CALLBACK WORKED: $current_cursor")
        }
        log.print("Current cursor: $position. Last Cursor is $itemCount")
        holder.text.text = data.response!!.items!![position].text
    }

    open class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.message_text)
    }
}