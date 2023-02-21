package com.ibcemobile.smoxstyler.activities

import android.content.DialogInterface
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivityForgetPasswordBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONObject
import java.util.HashMap

class ForgetPasswordActivity : BaseLoginActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            resetPassword()
        }

    }



    private fun resetPassword() {
        val email = binding.txtEmail.text.toString()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.txtEmail.error = "Please type your email"
            return
        }

        val params = HashMap<String, String>()
        params["email"] = email

        progressHUD.show()
        APIHandler(
            this,
            Request.Method.POST,
            Constants.API.forgot,
            params,
            object:APIHandler.NetworkListener{
                override fun onFail(error: String?) {

                    Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
                    progressHUD.dismiss()
                }

                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    showMessage()
                }
            })
    }

    private fun showMessage() {
        val builder = AlertDialog.Builder(this@ForgetPasswordActivity,R.style.MyAlertDialogStyle)
        builder.setMessage("Please check your email for resetting the password")
        builder.setCancelable(true)

        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })
        val alert = builder.create()
        alert.show()
    }
}