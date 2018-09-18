package ru.shadowsparky.messenger.messages_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import ru.shadowsparky.messenger.R
import ru.shadowsparky.messenger.response_utils.VKApi
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils.Companion.TOKEN
import javax.inject.Inject

class MessagesView : AppCompatActivity(), Messages.View {
    companion object {
        val USER_ID = "id"
    }

    @Inject
    lateinit var retrofit: Retrofit

    @Inject
    lateinit var preferencesUtils: SharedPreferencesUtils

    @Inject
    lateinit var log: Logger

    var user_id: Int = -1

    init {
        App.component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_view)
        user_id = intent.getIntExtra(USER_ID, -1)
        test()
    }

    fun test() {
        Observable.just("null")
                .observeOn(Schedulers.io())
                .map {
                    retrofit.create(VKApi::class.java)
                        .getHistory(0, 20, user_id, preferencesUtils
                        .read(TOKEN)).blockingFirst()
                }
                .subscribeBy (
                        onNext = { log.print("GOOD: ${it.raw().request().url()}") },
                        onError = { log.print("ERROR: $it")}
                )
    }
}
