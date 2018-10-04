package ru.shadowsparky.messenger.dialogs

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import ru.shadowsparky.messenger.R
import kotlinx.android.synthetic.main.auth_dialog.*
import ru.shadowsparky.messenger.auth.Auth
import ru.shadowsparky.messenger.utils.App
import ru.shadowsparky.messenger.utils.Logger
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils
import ru.shadowsparky.messenger.utils.SharedPreferencesUtils.Companion.TOKEN
import ru.shadowsparky.messenger.utils.ToastUtils
import javax.inject.Inject

open class AuthDialog(context: Context, val callback: () -> Unit) : Dialog(context) {
    @Inject
    lateinit var log: Logger
    @Inject
    lateinit var preferences: SharedPreferencesUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_dialog)
        App.component.inject(this)
        this.setCanceledOnTouchOutside(false)
        configureBrowser()
    }

    protected fun configureBrowser() {
        AuthBrowser.settings.javaScriptEnabled = true
        AuthBrowser.settings.setAppCacheEnabled(true)
//        AuthBrowser.loadUrl("https://m.vk.com")
        AuthBrowser.loadUrl("https://oauth.vk.com/authorize?client_id=6690029&scope=friends,messages,offline&redirect_uri=https://vk.com/blank.html&display=popup&v=5.85&response_type=token")
        AuthBrowser.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                log.print("PAGE NOT LOADED. ERROR: $request")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                log.print("PAGE LOADED. URL: $url")
                if (isAuthEnded(url!!)) {
                    preferences.remove(TOKEN)
                    val token = getToken(url)
                    if (preferences.write(TOKEN, token)) {
                        hide()
                        callback()
                    } else {
                        log.print("SHARED PREFERENCES ERROR! WRITE DON'T WORKING")
                    }
                }
            }
        }
    }

    protected fun getToken(url: String) : String {
        val firstParse = url.split('=')
        val twoParse = firstParse[1].split('&')
        return twoParse[0]
    }

    protected fun isAuthEnded(url: String) : Boolean = url.contains("access_token", true)

    protected fun checkError(url: String) : Boolean = url.contains("error", true)

    override fun onBackPressed() {
        super.onBackPressed()
        if (AuthBrowser.canGoBack()){
            AuthBrowser.goBack();
        } else {
            hide()
        }
    }
}