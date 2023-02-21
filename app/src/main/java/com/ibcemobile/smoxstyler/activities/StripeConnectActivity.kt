package com.ibcemobile.smoxstyler.activities

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import com.android.volley.Request
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivityStripeConnectBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONObject
import java.util.HashMap

class StripeConnectActivity : BaseActivity() {
    private lateinit var binding: ActivityStripeConnectBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStripeConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (app.currentUser.stripe_public_key.isNotEmpty()) {
            binding.txtPublicKey.setText(app.currentUser.stripe_public_key)
        }
        if (app.currentUser.stripe_secret_key.isNotEmpty()) {
            binding.txtPrivateKey.setText(app.currentUser.stripe_secret_key)
        }
        binding.btnSave.setOnClickListener {
            saveStripeInfo()
        }
    }

    private fun saveStripeInfo() {
        val publicKey = binding.txtPublicKey.text.toString()
        if (publicKey.isEmpty()) {
            binding.txtPublicKey.requestFocus()
            Toast.makeText(
                this,
                getString(R.string.text_stripe_public_key), Toast.LENGTH_LONG
            ).show()
            return
        }
        val privateKey = binding.txtPrivateKey.text.toString()
        if (privateKey.isEmpty()) {
            binding.txtPrivateKey.requestFocus()
            Toast.makeText(
                this,
                getString(R.string.text_stripe_public_key), Toast.LENGTH_LONG
            ).show()
            return
        }

        val params = HashMap<String, String>()

        params["stripe_public_key"] = publicKey
        params["stripe_secret_key"] = privateKey

        progressHUD.show()
        APIHandler(
            applicationContext,
            Request.Method.POST,
            Constants.API.save_key,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()

                    app.currentUser.connectAccount = false
                    //Now onwards user change there key of account so
                    app.currentUser.stripe_public_key = binding.txtPublicKey.text.toString()
                    app.currentUser.stripe_secret_key = binding.txtPrivateKey.text.toString()

                    sessionManager.userData = app.currentUser.getJsonString()
                    sessionManager.Sp_publishableKey = binding.txtPublicKey.text.toString()
                    sessionManager.Sp_privateKey = binding.txtPrivateKey.text.toString()

                    val error = result.getBoolean("error")
                    val title = if (error) "Failed" else "Success"
                    val message = result.getString("message")
                    showAlertDialog(title, message, DialogInterface.OnClickListener { _, _ ->
                        if (!error) finish()
                    }, getString(R.string.ok), null, null)
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