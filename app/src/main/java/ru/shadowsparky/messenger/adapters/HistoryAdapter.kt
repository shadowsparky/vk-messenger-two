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
import javax.inject.Inject

class HistoryAdapter(
        val data: HistoryResponse,
        val scroll_callback: (Int) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.MainViewHolder>() {
    @Inject lateinit var log: Logger

    init {
        App.component.inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryAdapter.MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.message_history_item, parent, false)
        return HistoryAdapter.MainViewHolder(v)
    }

    fun addData(response: HistoryResponse) {
        data.response!!.items!!.addAll(response.response!!.items!!)
        data.response.conversations!!.addAll(response.response.conversations!!)
        data.response.profiles!!.addAll(response.response.profiles!!)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data.response!!.items!!.size

    override fun onBindViewHolder(holder: HistoryAdapter.MainViewHolder, position: Int) {
        if (position == itemCount - 1) {
            scroll_callback(position + 1)
        }
        log.print("Current cursor: $position. Last Cursor is $itemCount")
        holder.text.text = data.response!!.items!![position].text
    }

    open class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.message_text)
    }
}