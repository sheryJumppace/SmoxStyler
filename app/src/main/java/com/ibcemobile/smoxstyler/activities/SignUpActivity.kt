package com.ibcemobile.smoxstyler.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivitySignUpBinding
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.type.UserType

class SignUpActivity : BaseLoginActivity(), View.OnClickListener {
        private var isPhoneVerified = false
//    private var isPhoneVerified = true
    var userType: String = UserType.Barber.toString()
    lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnFacebook.setOnClickListener(this)
        binding.btnGoogle.setOnClickListener(this)
        binding.btnSignUp.setOnClickListener(this)
        binding.txtPhone.setOnClickListener(this)
        binding.txtLogin.setOnClickListener(this)

//        binding.txtPhone.setText("+923463885110")
        binding.txtPhone.setOnClickListener {
            doPhoneNumberVerification()
        }
        binding.imageView4.setOnClickListener {
            binding.eyeOff.visibility = View.VISIBLE
            binding.imageView4.visibility = View.GONE
            binding.txtPassword.transformationMethod = PasswordTransformationMethod()

        }
        binding.eyeOff.setOnClickListener {
            binding.imageView4.visibility = View.VISIBLE
            binding.eyeOff.visibility = View.GONE
            binding.txtPassword.transformationMethod = null
        }


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.txtLogin -> {
                binding.txtLogin.isEnabled = false
                login()
            }
            R.id.btnSignUp -> {
                signUp()
            }
            R.id.btnFacebook -> {
                binding.btnFacebook.isEnabled = false
                loginWithFacebook(UserType.Barber, true)
            }
            R.id.btnGoogle -> {
                binding.btnGoogle.isEnabled = false
                loginWithGoogle(UserType.Barber, true)
            }

        }
    }

    @SuppressLint("RestrictedApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Companion.RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == RESULT_OK) {
                val phone = response!!.user.phoneNumber.toString()
                binding.txtPhone.setText(phone)
                isPhoneVerified = true
                binding.btnVerify.visibility = View.VISIBLE
            } else {
                if (response != null) {
                    val error = response.error
                    if (error != null) {
                        Toast.makeText(
                            applicationContext,
                            response.error!!.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    binding.btnVerify.visibility = View.GONE

                }
            }
        }
    }

    private fun login() {
        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
    }

    private fun terms() {
        val intent = Intent(this@SignUpActivity, ForgetPasswordActivity::class.java)
        intent.putExtra("url", Constants.KUrl.terms)
        intent.putExtra("title", resources.getString(R.string.title_terms))
        startActivity(intent)
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
    }

    private fun signUp() {
        if (!validation()) {
            return
        }
        val firstName = binding.txtFirstName.text.toString()
        val lastName = binding.txtLastName.text.toString()
        val email = binding.txtEmail.text.toString()
        val phone = binding.txtPhone.text.toString()
        val password = binding.txtPassword.text.toString()
        signUpWithEmail(firstName, lastName, email, phone, password, UserType.Barber)
    }

    private fun validation(): Boolean {
        if (binding.txtFirstName.text.toString().isEmpty()) {
            binding.txtFirstName.error = "Please type your first name"
            binding.txtFirstName.requestFocus()
            return false
        }
        if (binding.txtLastName.text.toString().isEmpty()) {
            binding.txtLastName.error = "Please type your last name"
            binding.txtLastName.requestFocus()

            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.txtEmail.text.toString()).matches()) {
            binding.txtEmail.error = "Please type your email"
            binding.txtEmail.requestFocus()

            return false
        }
        if (binding.txtPassword.text.toString().length < 6) {
            binding.txtPassword.error = "The password is too short, it must at least 6 characters"
            binding.txtPassword.requestFocus()
            return false
        }
        if (!isPhoneVerified) {
            binding.txtPhone.error = "Please verify your phone number"
            return false
        }

        return true
    }

    private fun doPhoneNumberVerification() {
        val providers = listOf(
            AuthUI.IdpConfig.PhoneBuilder().build()
        )
        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .setTheme(R.style.FirebasePhoneAuth).build(), Companion.RC_SIGN_IN
        )
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}