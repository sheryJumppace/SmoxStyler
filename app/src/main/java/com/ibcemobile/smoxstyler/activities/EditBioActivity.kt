package com.ibcemobile.smoxstyler.activities

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Toast
import com.android.volley.Request
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivityEditBioBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONObject
import java.util.*

class EditBioActivity : BaseActivity() {
    companion object {
        private const val MAX_NUM = 250
    }

    private lateinit var binding: ActivityEditBioBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener{
            onBackPressed()
        }

        binding.btnSubmit.setOnClickListener {
            updateBio()

        }
        initUI()

    }

    private fun initUI() {
        binding.txtBio.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_NUM))
        binding.txtBio.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                setCounterLabel()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
        binding.txtBio.setText(app.currentUser.bio)
        setCounterLabel()
    }

    private fun setCounterLabel() {
        binding.txtCounter.text =
            String.format("%d Left", MAX_NUM - binding.txtBio.text.toString().length)
    }

    private fun updateBio() {
        val bio = binding.txtBio.text.toString()
        if (bio.isEmpty()) {
            binding.txtBio.error = resources.getString(R.string.err_short_bio)
            return
        }
        val params = HashMap<String, String>()
        params["bio"] = bio
        progressHUD.show()
        APIHandler(
            applicationContext,
            Request.Method.PUT,
            Constants.API.user_bio,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val barber = app.currentUser
                    barber.bio = bio
                    sessionManager.userData = barber.getJsonString()
                    finish()
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}