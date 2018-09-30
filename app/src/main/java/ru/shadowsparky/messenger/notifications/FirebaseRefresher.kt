package ru.shadowsparky.messenger.notifications

import android.provider.Settings
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Constansts.Companion.FIREBASE_TOKEN
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import android.content.Context
import javax.inject.Inject

class FirebaseRefresher : FirebaseInstanceIdService() {
    @Inject lateinit var preferencesUtils: SharedPreferencesUtils
    @Inject lateinit var log: Logger
    @Inject lateinit var context: Context

    init {
        App.component.inject(this)
    }

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        FirebaseInstanceId.getInstance().token?.let {
            preferencesUtils.write(FIREBASE_TOKEN, it)
            log.print("NEW FIREBASE TOKEN HANDLED: $it")
        }
    }
}