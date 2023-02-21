package com.ibcemobile.smoxstyler.activities

import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.ibcemobile.smoxstyler.databinding.ActivityChangePasswordBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.kaopiz.kprogresshud.KProgressHUD
import org.json.JSONObject
import java.util.HashMap

class ChangePasswordActivity : BaseActivity() {
    private lateinit var txtCurrentPassword: EditText
    private lateinit var txtNewPassword: EditText
    private lateinit var txtConfirmPassword: EditText
    private lateinit var  binding : ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        txtCurrentPassword = binding.oldPassword
        txtNewPassword = binding.newPassword
        txtConfirmPassword = binding.repeatPassword

        binding.btnSubmit.setOnClickListener{
            updatePassword()
        }


        binding.imgBack.setOnClickListener{
            onBackPressed()
        }




    }
    private fun updatePassword() {
        val oldPassword =  txtCurrentPassword.text.toString()
        val newPassword =  txtNewPassword.text.toString()
        val confirmPassword =  txtConfirmPassword.text.toString()

        if(TextUtils.isEmpty(oldPassword)){
            txtCurrentPassword.error = "Please type your old password"
            return
        }

        if(TextUtils.isEmpty(newPassword)){
            txtCurrentPassword.error = "Please type your new password"
            return
        }

        if(TextUtils.isEmpty(confirmPassword)){
            txtCurrentPassword.error = "Please type your retype password"
            return
        }

        if (oldPassword.length < 6) {
            txtCurrentPassword.error = "The password is too short,  it must at least 6 characters"
            return
        }

        if (newPassword.length < 6) {
            txtNewPassword.error = "The password is too short,  it must at least 6 characters"
            return
        }

        if (confirmPassword.length < 6) {
            txtConfirmPassword.error = "The password is too short,  it must at least 6 characters"
            return
        }

        if(newPassword != confirmPassword){
            txtNewPassword.error = "Password confirmation doesn't match Password."
            txtConfirmPassword.setText("")
            return
        }

        val params = HashMap<String, String>()
        params["old_password"] = oldPassword
        params["password"] = newPassword
        val progressHUD = KProgressHUD(this@ChangePasswordActivity)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        progressHUD.show()

        APIHandler(
            this@ChangePasswordActivity,
            Request.Method.PUT,
            Constants.API.password,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val status : Boolean = result.getBoolean("error")
                    val message : String = result.getString("message")
                    if(!status){
                        finish()
                    }
                    Toast.makeText(this@ChangePasswordActivity, message, Toast.LENGTH_LONG).show()
                }
                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

}