package ru.shadowsparky.messenger.utils

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.shadowsparky.messenger.auth.AuthView
import ru.shadowsparky.messenger.messages_list.MessagesListView
import ru.shadowsparky.messenger.utils.Constansts.Companion.EMPTY_STRING
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils.Companion.TOKEN
import javax.inject.Inject

class Startup: AppCompatActivity() {
    @Inject protected lateinit var mPref: SharedPreferencesUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.component.inject(this)
        val activity = if (mPref.read(TOKEN) != EMPTY_STRING) {
            Intent(this, MessagesListView::class.java)
        } else {
            Intent(this, AuthView::class.java)
        }
        startActivity(activity)
        finish()
    }
}