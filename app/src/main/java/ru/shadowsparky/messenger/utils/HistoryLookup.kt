package ru.shadowsparky.messenger.utils

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import ru.shadowsparky.messenger.adapters.HistoryAdapter

@Deprecated("Нигде в проекте не используется и вряд ли когда нибудь будет")
class HistoryLookup(private val rv: RecyclerView) : ItemDetailsLookup<Long>(){

    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = rv.findChildViewUnder(event.x, event.y)
        if(view != null) {
//            return (rv.getChildViewHolder(view) as HistoryAdapter.MainViewHolder)
//                    .getItemDetails()
        }
        return null
    }
}