package com.ibcemobile.smoxstyler.activities

import android.os.Bundle
import com.ibcemobile.smoxstyler.databinding.ActivityFacebookAuthBinding

class FacebookAuthActivity : BaseActivity() {
    private var binding: ActivityFacebookAuthBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacebookAuthBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        if (intent.getStringExtra("fromFb")!!.isNotEmpty()) {
            binding!!.btnSignUp.setOnClickListener {
                facebookSignUp(intent.getStringExtra("fromFb")!!)
            }
        }
    }

    private fun facebookSignUp(socialId: String) {


    }
}