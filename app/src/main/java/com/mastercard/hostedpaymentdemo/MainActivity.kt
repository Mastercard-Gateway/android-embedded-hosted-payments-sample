package com.mastercard.hostedpaymentdemo

import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    companion object {
        const val REDIRECT_SCHEME = "simplify"
        const val ENDPOINT_URL = "YOUR_SERVER_HERE"
        const val AMOUNT = 1000L
        const val CURRENCY_CODE = "AUD"
    }


    val webView: WebView by lazy { findViewById<WebView>(R.id.webView) }
    val checkComplete: ImageView by lazy { findViewById<ImageView>(R.id.imageView) }
    val completeText: TextView by lazy { findViewById<TextView>(R.id.text) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView.webChromeClient = WebChromeClient()
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = buildWebViewClient()
        webView.loadUrl("$ENDPOINT_URL/hostedPayment.php?amount=$AMOUNT")

    }

    internal fun buildWebViewClient(): WebViewClient {
        return object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                webViewUrlChanges(Uri.parse(url))
                return true
            }
        }
    }

    private fun webViewUrlChanges(uri: Uri) {
        if (REDIRECT_SCHEME.equals(uri.scheme, ignoreCase = true)) {
            //Parse card token out of path
            val cardToken = uri.getQueryParameters("token")?.last()
            processPayment(cardToken)

        }
    }

    private fun processPayment(cardToken: String?) {
        PostCardToken().execute(cardToken)
    }


    inner class PostCardToken : AsyncTask<String, Unit, String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = URL("$ENDPOINT_URL/charge.php")
            val params = "amount=$AMOUNT&simplifyToken=${params[0]}&currency=$CURRENCY_CODE"
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded")
            conn.readTimeout = 15000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true

            conn.outputStream.use { os ->
                os.bufferedWriter().use {writer ->
                    writer.write(params)
                }
            }

            val output = conn.inputStream.use { ins ->
                ins.bufferedReader().use { br ->
                    br.lineSequence().joinToString(separator = "") { it }
                }
            }

            return output
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            Log.d("Payment Response", result)

            webView.visibility = View.GONE
            checkComplete.visibility = View.VISIBLE
            completeText.visibility = View.VISIBLE
        }
    }
}
