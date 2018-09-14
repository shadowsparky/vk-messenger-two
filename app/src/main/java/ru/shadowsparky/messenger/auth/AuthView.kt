package ru.shadowsparky.messenger.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.shadowsparky.messenger.R
import kotlinx.android.synthetic.main.activity_auth.*
import ru.shadowsparky.messenger.messages_list.MessagesListView
import ru.shadowsparky.messenger.dialogs.AuthDialog
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.ToastUtils
import javax.inject.Inject

class AuthView : AppCompatActivity(), Auth.View {
    @Inject
    lateinit var log: Logger
    @Inject
    lateinit var toast: ToastUtils
    @Inject
    lateinit var context: Context
    lateinit var presenter: Auth.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        App.component.inject(this)
        presenter = AuthPresenter(this)
        AuthButton.setOnClickListener {
            val callback: () -> Unit = {
                navigateToMessagesList()
            }
            AuthDialog(this, callback).show()
        }
        presenter.onAuthentication()
    }

    override fun setLoading(result: Boolean) {
    }

    override fun navigateToMessagesList() = startActivity(Intent(this, MessagesListView::class.java))

}
