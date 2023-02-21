package com.ibcemobile.smoxstyler.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.ibcemobile.smoxstyler.databinding.ContactUsFragmentBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONObject

class ContactUsFragment : BaseFragment() {

    private lateinit var binding: ContactUsFragmentBinding

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ContactUsFragmentBinding.inflate(inflater, container, false)
        // val v = inflater.inflate(R.layout.fragment_home, container, false)


        binding.btnDone.setOnClickListener {
            when {
                TextUtils.isEmpty(binding.etEmail.text.toString()) -> {
                    binding.etEmail.error="Field is empty!"
                    binding.etEmail.requestFocus()
                }
                TextUtils.isEmpty(binding.etDescription.text.toString()) -> {
                    binding.etDescription.error="Field is empty!"
                    binding.etDescription.requestFocus()
                }
                else -> {
                    sendEmailToServer()
                }
            }
        }



        return binding.root
    }

    private fun sendEmailToServer() {

        val params = HashMap<String, String>()
        params["email"] = binding.etEmail.text.toString()
        params["description"] = binding.etDescription.text.toString()

        APIHandler(
            requireActivity(),
            Request.Method.POST,
            Constants.API.contactus,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val status: Boolean = result.getBoolean("error")
                    val message: String = result.getString("message")
                    Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
                    if (!status)
                        requireActivity().onBackPressed()
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