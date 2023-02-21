package com.ibcemobile.smoxstyler.activities

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.view.Window
import android.widget.TextView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.databinding.ActivitySignInBinding
import com.ibcemobile.smoxstyler.model.type.UserType
import com.ibcemobile.smoxstyler.utils.showReminder

class SignInActivity : BaseLoginActivity(), View.OnClickListener {
    lateinit var binding: ActivitySignInBinding
    var userType:String = UserType.Barber.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
        binding.btnFacebook.setOnClickListener(this)
        binding.btnGoogle.setOnClickListener(this)
        binding.btnForgot.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.registerBtn.setOnClickListener(this)

        binding.imageView4.setOnClickListener {
            binding.eyeOff.visibility=View.VISIBLE
            binding.imageView4.visibility=View.GONE
            binding.txtPassword.transformationMethod = PasswordTransformationMethod()

        }
        binding.eyeOff.setOnClickListener {
            binding.imageView4.visibility=View.VISIBLE
            binding.eyeOff.visibility=View.GONE
            binding.txtPassword.transformationMethod = null
        }

        binding.remember.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                when {
                    binding.txtEmail.text.toString().isEmpty() -> {
                        binding.txtEmail.error = "Please type your email"
                        binding.txtEmail.requestFocus()
                    }
                    binding.txtPassword.text.toString().isEmpty() -> {
                        binding.txtPassword.error = "Please type your password"
                        binding.txtPassword.requestFocus()
                    }
                    binding.txtPassword.text.toString().length < 6 -> {
                        binding.txtPassword.error = "The password is too short, it must at least 6 characters"
                        binding.txtPassword.requestFocus()
                    }
                    else -> {
                        SessionManager.getInstance(this).setValue("email",binding.txtEmail.text.toString())
                        SessionManager.getInstance(this).setValue("pass",binding.txtPassword.text.toString())
                    }
                }
                // show toast , check box is checked

            } else {
                // show toast , check box is not checked
                SessionManager.getInstance(this).setValue("email","")
                SessionManager.getInstance(this).setValue("pass","")
            }
        }

        if (SessionManager.getInstance(this).getValue("email")?.isNotEmpty()!!){
            binding.txtEmail.setText(SessionManager.getInstance(this).getValue("email"))
            binding.txtPassword.setText(SessionManager.getInstance(this).getValue("pass"))
        }else{
            showReminder(this)
        }





    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btnLogin -> {
                    login()
                }
                R.id.registerBtn -> {
                    signUp()
                }
                R.id.btnForgot -> {
                    forgot()
                }
                R.id.btnFacebook -> {
                    loginWithFacebook(UserType.Barber, false)
                }
                R.id.btnGoogle -> {
                    binding.btnGoogle.isEnabled = false
                    loginWithGoogle(UserType.Barber, false)
                }
            }
        }
    }

    private fun forgot() {
        val intent = Intent(this@SignInActivity, ForgetPasswordActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
    }

    private fun signUp() {
        val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)

    }
    private fun login() {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        keepMeLogin(email, password)
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.txtEmail.text.toString()).matches()) {
            binding.txtEmail.error = "Please type your email"
            return
        }
        when {
            binding.txtEmail.text.toString().isEmpty() -> {
                binding.txtEmail.error = "Please type your email"
                binding.txtEmail.requestFocus()
            }

            binding.txtPassword.text.toString().isEmpty() -> {
                binding.txtPassword.error = "Please type your password"
                binding.txtPassword.requestFocus()

            }
            binding.txtPassword.text.toString().length < 6 -> {
                binding.txtPassword.error = "The password is too short, it must at least 6 characters"
                binding.txtPassword.requestFocus()

            }
            else ->{
                loginWithEmail(email, password,userType)
            }
        }
    }




}