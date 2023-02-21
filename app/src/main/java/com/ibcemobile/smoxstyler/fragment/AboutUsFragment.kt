package com.ibcemobile.smoxstyler.fragment

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.ibcemobile.smoxstyler.databinding.AboutUsFragmentBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONObject

class AboutUsFragment : BaseFragment() {

    private lateinit var binding: AboutUsFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AboutUsFragmentBinding.inflate(inflater, container, false)
        // val v = inflater.inflate(R.layout.fragment_home, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()

    }

    private fun getData() {
        val params = HashMap<String, String>()

        progressHUD.show()
        APIHandler(
            requireActivity(),
            Request.Method.GET,
            Constants.API.about_us,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    Log.e("Open days Data:- ", result.toString())

                    binding.txtData.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(result.getString("result"), Html.FROM_HTML_MODE_COMPACT)
                    } else {
                        Html.fromHtml(result.getString("result"))
                    }
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


}