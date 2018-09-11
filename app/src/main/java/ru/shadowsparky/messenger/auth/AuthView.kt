package ru.shadowsparky.messenger.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import es.dmoral.toasty.Toasty
import ru.shadowsparky.messenger.R
import kotlinx.android.synthetic.main.activity_auth.*
import ru.shadowsparky.messenger.MessagesListView
import ru.shadowsparky.messenger.dialogs.AuthDialog
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils.Companion.TOKEN
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
            val callback: (String) -> Unit = {
                navigateToMessagesList(it)
            }
            AuthDialog(this, callback).show()
        }
        presenter.onAuthentication()
    }

    override fun setLoading(result: Boolean) {
    }

    override fun navigateToMessagesList(token: String) {
        val i = Intent(this, MessagesListView::class.java)
        i.putExtra(TOKEN, token)
        startActivity(i)
    }

}
