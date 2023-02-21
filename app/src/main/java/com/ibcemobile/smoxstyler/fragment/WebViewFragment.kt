package com.ibcemobile.smoxstyler.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.ibcemobile.smoxstyler.R

import com.ibcemobile.smoxstyler.databinding.WebviewFragmentBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONObject

class WebViewFragment : BaseFragment() {

    private var binding: WebviewFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = WebviewFragmentBinding.inflate(inflater, container, false)
        // val v = inflater.inflate(R.layout.fragment_home, container, false)
        val url = requireArguments().getString("url")

        progressHUD.show()

        setUpWebView(url!!)
        return binding!!.root
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(url:String){



        // Get the web view settings instance
        val settings = binding!!.webView.settings

        // Enable java script in web view
        settings.javaScriptEnabled = true

        // Enable and setup web view cache
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_DEFAULT

        // Enable zooming in web view
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = true

        binding!!.webView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))

        binding!!.webView.settings.pluginState = WebSettings.PluginState.ON
        binding!!.webView.webChromeClient = WebChromeClient()
        binding!!.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                Log.e("URL", url)
                progressHUD.show()
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.e("URL", url)
                progressHUD.dismiss()
            }
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                when {
                    url!!.contains("https://smoxtrimsetters.com/?code") -> {
                        val stripeCodeUri : Uri = Uri.parse(url)
                        val stripeCode = stripeCodeUri.getQueryParameters("code")[0]
                        connectStripeAccount(stripeCode)
                    }
                    url.contains("https://connect.stripe.com/connect/default/oauth/test?code") -> {
                        val stripeCodeUri : Uri = Uri.parse(url)
                        val stripeCode = stripeCodeUri.getQueryParameters("code")[0]
                        connectStripeAccount(stripeCode)
                    }
                    url.contains("contactus@smoxtrimsetters.com") -> {
                        val stripeCodeUri : Uri = Uri.parse("mailto:$url")
                        startActivity(Intent(Intent.ACTION_SENDTO, stripeCodeUri))
                    }
                    else -> {
                        view?.loadUrl(url)
                    }
                }

                return true
            }
        }
        binding!!.webView.loadUrl(url)

    }
    companion object {

        fun newInstance(): WebViewFragment {
            return WebViewFragment()
        }
    }

    private fun connectStripeAccount(code: String){
        val params = HashMap<String, String>()
        params.put("code", code)

        progressHUD.show()

        APIHandler(requireActivity(),
            Request.Method.POST,
            Constants.API.stripe_connect_account,
            params,
            object  : APIHandler.NetworkListener{
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    //{"error":false,"message":"Congrats! You've registered a payment method, Verifying any of your documents will take us 1 - 3 business days."}
                    val message = result.getString("message")

                    app.currentUser.connectAccount = false //Now onwards user change there key of account so
                    sessionManager.userData = app.currentUser.getJsonString()

                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    requireActivity().finish()
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                }
            }
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}