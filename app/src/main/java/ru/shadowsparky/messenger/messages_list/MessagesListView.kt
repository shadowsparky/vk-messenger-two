package ru.shadowsparky.messenger.messages_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.shadowsparky.messenger.R

class MessagesListView : AppCompatActivity(), MessagesList.View {
    override fun setLoading(result: Boolean) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_view)
    }
}
