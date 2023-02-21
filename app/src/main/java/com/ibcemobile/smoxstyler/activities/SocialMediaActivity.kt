package com.ibcemobile.smoxstyler.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.ibcemobile.smoxstyler.R

import com.ibcemobile.smoxstyler.databinding.ActivitySocialMediaBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONObject
import java.util.HashMap


class SocialMediaActivity : BaseActivity() {

    private lateinit var binding: ActivitySocialMediaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySocialMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.llFacebook.setOnClickListener {
            showDialog("Your facebook link",1)
        }
        binding.llEmail.setOnClickListener {
            showDialog("Your email",2)

        }
        binding.llWhatsapp.setOnClickListener {
            showDialog("Your whatsapp number",3)
        }

        binding.txtFacebookHint.setOnClickListener {
            binding.rlHint.visibility= View.VISIBLE
        }
        binding.txtClose.setOnClickListener {
            binding.rlHint.visibility= View.GONE
        }

    }
    private fun showDialog(mess: String,type:Int) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.add_social_midia_dailog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)
        val etInput = dialog.findViewById<EditText>(R.id.etInput)
        val txtDecline = dialog.findViewById<TextView>(R.id.txtDecline)
        val txtApprove = dialog.findViewById<TextView>(R.id.txtApprove)
        txtMessage.text = mess
        txtApprove.setOnClickListener {

            if (etInput.text.toString().isEmpty()){
                Toast.makeText(this,"Field is empty!",Toast.LENGTH_LONG).show()
            }else{
                dialog.dismiss()

                updateData(etInput.text.toString(),type)
            }

        }
        txtDecline.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun updateData(input: String,type:Int) {
        var url =""
        val params = HashMap<String, String>()
        when (type) {
            1 -> {
                params["facebook"]=input
                url=Constants.API.fb_Link
            }
            2 -> {
                params["email"]=input
                url=Constants.API.email_link

            }
            3 -> {
                params["whatsapp"]=input
                url=Constants.API.whatsapp_link

            }
        }
        progressHUD.show()
        APIHandler(
            applicationContext,
            Request.Method.PUT,
            url,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext,
                        result.getString("message"), Toast.LENGTH_LONG
                    ).show()
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