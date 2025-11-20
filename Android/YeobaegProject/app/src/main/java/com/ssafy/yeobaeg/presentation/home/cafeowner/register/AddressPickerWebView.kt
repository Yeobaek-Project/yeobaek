// PostcodeJibunWebView.kt
@file:Suppress("SetJavaScriptEnabled")

package com.ssafy.yeobaeg.presentation.home.cafeowner.register

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.Keep
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PostcodeJibunWebView(
    modifier: Modifier = Modifier,
    onSelected: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val html = remember {
        context.assets.open("postcode.html").bufferedReader().use { it.readText() }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {}
                webChromeClient = WebChromeClient()

                addJavascriptInterface(
                    PostcodeJibunJsBridge(onSelected, onClose),
                    "AndroidBridge"
                )

                loadDataWithBaseURL(
                    "https://t1.daumcdn.net/", // 외부 스크립트 로드 base
                    html,
                    "text/html",
                    "utf-8",
                    null
                )
            }
        }
    )
}

@Keep
private class PostcodeJibunJsBridge(
    private val onSelectedAddress: (String) -> Unit,
    private val onCloseWebView: () -> Unit
) {
    @JavascriptInterface
    fun onSelected(jibun: String) {
        onSelectedAddress(jibun)
    }

    @JavascriptInterface
    fun onClose() {
        onCloseWebView()
    }
}