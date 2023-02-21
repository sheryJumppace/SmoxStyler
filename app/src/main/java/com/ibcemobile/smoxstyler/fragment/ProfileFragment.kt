package com.ibcemobile.smoxstyler.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.activities.*
import com.ibcemobile.smoxstyler.databinding.ProfileFragmentBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.manager.Constants.downloadUrl
import com.ibcemobile.smoxstyler.utils.*
import com.ibcemobile.smoxstyler.viewmodels.UserViewModel
import org.json.JSONObject

class ProfileFragment : BaseImagePickerFragment(), UploadImages {
    private lateinit var binding: ProfileFragmentBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel= ViewModelProvider(this).get(UserViewModel::class.java)


        binding.etAboutMe.setOnClickListener {
            val intent = Intent(requireActivity(), EditBioActivity::class.java)
            intent.putExtra("bio", binding.etAboutMe.text.toString())
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
        }

        binding.editImage.setOnClickListener {
            didOpenPhotoOption()

        }
        binding.editAddress.setOnClickListener {

            val intent = Intent(requireActivity(), UpdateAddressActivity::class.java)
            intent.putExtra(KEY_ADDRESS, binding.etAddress.text.toString())
            var profileResut=userViewModel.barberProfile.value?.result

            intent.putExtra(KEY_ZIP, profileResut?.zip_code)
            intent.putExtra(KEY_ADD_1, profileResut?.address_one)
            intent.putExtra(KEY_ADD_2, profileResut?.address_two)
            intent.putExtra(KEY_CITY, profileResut?.city)
            intent.putExtra(KEY_STATE, profileResut?.state)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
        }
        binding.editPassword.setOnClickListener {
            val intent = Intent(requireActivity(), ChangePasswordActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
        }

        binding.editBusiness.setOnClickListener {
            if (TextUtils.isEmpty(binding.etBusiness.text.toString())) {
                binding.etBusiness.error = "Business Name"
                binding.etBusiness.requestFocus()
            } else {
                updateBusinessName(binding.etBusiness.text.toString())
            }
        }


        binding.editNumber.setOnClickListener {
            doPhoneNumberVerification()
        }

        userViewModel.barberProfile.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                binding.txtName.text = it.result.firstName + " " + it.result.lastName
                binding.etEmail.text = app.currentUser.email
                binding.etAddress.text = app.currentUser.address
                binding.etAboutMe.text = it.result.bio
                binding.etNumber.text = it.result.phone
                binding.etBusiness.setText(it.result.businessName)
                Glide.with(requireActivity())
                    .load(downloadUrl(it.result.image!!))
                    .placeholder(R.drawable.small_placeholder)
                    .into(binding.ProfilePic)
            }
        })

        userViewModel.isUpdateProfile.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                userViewModel.getBarberProfile(requireContext(),progressHUD)
            }
        })

        userViewModel.isDeleteAccount.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                if (!it.error)
                App.instance.currentPage = 0
                val sessionManager = SessionManager.getInstance(requireContext())
                sessionManager.userData = ""
                sessionManager.apiKey = ""
                sessionManager.isSubscribed = false
                sessionManager.subscription_enddate = ""
                sessionManager.Sp_publishableKey = ""
                sessionManager.Sp_privateKey = ""
                val intent = Intent(context, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                try {
                    (context as Activity).finish()
                } catch (e: Exception) {

                }

            }
        })


        binding.btnDeleteAccount.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity(), R.style.MyAlertDialogStyle)
            builder.setTitle("Delete Account!!")
            builder.setMessage("Are you sure you want to delete account?")
            builder.setPositiveButton("CONFIRM") { _, _ ->
                userViewModel.deleteAccount(requireContext(),progressHUD)
            }
            builder.setNegativeButton("CANCEL", null)
            builder.show()

        }

    }

    override fun onResume() {
        super.onResume()
        userViewModel.getBarberProfile(requireContext(),progressHUD)

    }

    private fun updateBusinessName(business: String) {
        val jsonObject=JsonObject()
        jsonObject.addProperty("name",business)
        userViewModel.updateProfileWithPost(requireContext(),progressHUD,Constants.API.update_business_name,jsonObject)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == AppCompatActivity.RESULT_OK) {
                val phone = response?.phoneNumber.toString()
                binding.etNumber.text = phone

                updatePhoneNumber(binding.etNumber.text.toString().trim())

            } else {
                if (response != null) {
                    val error = response.error
                    if (error != null) {
                        Toast.makeText(
                            requireActivity(), response.error!!.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }
    }

    override fun didSelectPhoto(uri: Uri) {
        super.didSelectPhoto(uri)
        progressHUD.show()
        val imageUploadUtils = ImageUploadUtils()
        imageUploadUtils.onUpload(
            requireActivity(),
            uri.path!!,
            this
        )


    }

    private fun updatePhoneNumber(phone: String) {
        val jsonObject=JsonObject()
        jsonObject.addProperty("phone",phone)
        userViewModel.updateProfile(requireContext(),progressHUD,Constants.API.phone,jsonObject)

    }

    private fun doPhoneNumberVerification() {
        val providers = listOf(
            AuthUI.IdpConfig.PhoneBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.FirebasePhoneAuth)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun upload(imageUrl: String) {
        val jsonObject=JsonObject()
        jsonObject.addProperty("image",imageUrl)
        userViewModel.updateProfileWithPost(requireContext(),progressHUD,Constants.API.upload_profile,jsonObject)

    }


    override fun onError() {

    }





    companion object {
        private const val RC_SIGN_IN = 123
    }
}