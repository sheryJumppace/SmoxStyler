package com.ibcemobile.smoxstyler.fragment

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.AddressBottomSheetBinding
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.responses.AddressResponse
import com.ibcemobile.smoxstyler.utils.KEY_ADDRESS
import com.ibcemobile.smoxstyler.utils.shortToast
import com.ibcemobile.smoxstyler.viewmodels.AddressViewModel
import com.kaopiz.kprogresshud.KProgressHUD
import java.io.IOException
import java.util.*


class AddressBottomSheetFragment(
    private val addressClickedListener: AddressClickedListener,
    private val addressData: AddressResponse.AddressData?
) :
    BottomSheetDialogFragment() {
    private lateinit var binding: AddressBottomSheetBinding
    lateinit var addressViewModel: AddressViewModel
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 1
    private var MAKE_DEFAULT: Int = 0
    private var addressId: String? = null
    private var addressLatitude: Double? = 0.0
    private var addressLongitude: Double? = 0.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = AddressBottomSheetBinding.inflate(inflater, container, false)
        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)

        val progressHUD = KProgressHUD(requireActivity())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)

        if (!Places.isInitialized()) {
            Places.initialize(
                App.instance,
                Constants.PLACE_KEY_1 + Constants.PLACE_KEY_2 + Constants.PLACE_KEY_3 + Constants.PLACE_KEY_4,
                Locale.US
            )
        }

        binding.etCity.setOnClickListener {
            if (Places.isInitialized()) {
                val fields: List<Place.Field> = listOf(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.ADDRESS_COMPONENTS,
                    Place.Field.LAT_LNG
                )
                val autoSearchIntent = Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN,
                    fields
                ).build(requireActivity())
                startActivityForResult(autoSearchIntent, AUTOCOMPLETE_REQUEST_CODE)
            }
        }
        if (addressData != null) {
            addressId = addressData.id
            binding.etFirstName.setText(addressData.first_name)
            binding.etLastName.setText(addressData.last_name)
            binding.etAddress1.setText(addressData.address_one)
            binding.etAddress2.setText(addressData.address_two)
            binding.etCity.setText(addressData.city)
            binding.etState.setText(addressData.state)
            binding.etCountry.setText(addressData.country)
            binding.etZipCode.setText(addressData.zipcode)
            binding.etNumber.setText(addressData.phone)
            addressLatitude = addressData.latitude.toDouble()
            addressLongitude = addressData.longitude.toDouble()
            MAKE_DEFAULT = addressData.make_default.toInt()
            binding.switchButton.isChecked= MAKE_DEFAULT==1
            binding.saveAddress.text = "Update Address"
            binding.saveAddress.setOnClickListener {
                if (isValiData())
                    saveAddress(progressHUD, true)
            }
        } else {
            binding.saveAddress.setOnClickListener {
                if (isValiData())
                    saveAddress(progressHUD, false)
            }
        }

        binding.switchButton.setOnCheckedChangeListener { _, isChecked ->

            MAKE_DEFAULT = if (isChecked) {
                1
            } else {
                0
            }
        }

        addressViewModel.isUpdate.observe(viewLifecycleOwner, Observer { isUpdate ->
            if (isUpdate != null) {
                if (!isUpdate) {
                    dismiss()
                    addressClickedListener.saveAddress()
                }
            }
        })

        return binding.root
    }

    private fun isValiData(): Boolean {
        if (binding.etFirstName.text.toString().isEmpty()){
            shortToast(getString(R.string.error_fname))
            return false
        }
        if (binding.etLastName.text.toString().isEmpty()){
            shortToast(getString(R.string.error_lname))
            return false
        }
       /* if (binding.etAddress1.text.toString().isEmpty()){
            shortToast(getString(R.string.error_add1))
            return false
        }*/
        if (binding.etAddress2.text.toString().isEmpty()){
            shortToast(getString(R.string.error_add2))
            return false
        }
        if (binding.etCity.text.toString().isEmpty()){
            shortToast(getString(R.string.error_city))
            return false
        }
        if (binding.etState.text.toString().isEmpty()){
            shortToast(getString(R.string.error_state))
            return false
        }
        if (binding.etCountry.text.toString().isEmpty()){
            shortToast(getString(R.string.error_country))
            return false
        }
        if (binding.etZipCode.text.toString().isEmpty()){
            shortToast(getString(R.string.error_zip))
            return false
        }
        if (binding.etNumber.text.toString().isEmpty()){
            shortToast(getString(R.string.error_phone))
            return false
        }
        if (binding.etNumber.text.toString().length<6){
            shortToast(getString(R.string.error_phoneValid))
            return false
        }
        if (!Patterns.PHONE.matcher(binding.etNumber.text.toString()).matches()){
            shortToast(getString(R.string.error_phoneValid))
            return false
        }

        return true
    }


    private fun saveAddress(progressHUD: KProgressHUD, isUpdate: Boolean) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("first_name", binding.etFirstName.text.toString())
        jsonObject.addProperty("last_name", binding.etLastName.text.toString())
        jsonObject.addProperty("address_one", binding.etAddress1.text.toString())
        jsonObject.addProperty("address_two", binding.etAddress2.text.toString())
        jsonObject.addProperty("city", binding.etCity.text.toString())
        jsonObject.addProperty("state", binding.etState.text.toString())
        jsonObject.addProperty("country", binding.etCountry.text.toString())
        jsonObject.addProperty("zipcode", binding.etZipCode.text.toString())
        jsonObject.addProperty("latitude", addressLatitude)
        jsonObject.addProperty("longitude", addressLongitude)
        jsonObject.addProperty("phone", binding.etNumber.text.toString())
        jsonObject.addProperty("make_default", MAKE_DEFAULT)
        if (isUpdate) {
            jsonObject.addProperty("address_id", addressId)
            addressViewModel.updateAddress(requireActivity(), progressHUD, jsonObject)
        } else {
            addressViewModel.addNewAddress(requireActivity(), progressHUD, jsonObject)
        }
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                //binding.etAddress1.setText(address.getAddressLine(0))
                binding.etCity.setText(address.locality)
                binding.etState.setText(address.adminArea)
                binding.etZipCode.setText(address.postalCode)
                binding.etCountry.setText(address.countryName)

                //result.append(address.locality).append("\n")
                //result.append(address.countryName)
            }
        } catch (e: IOException) {
            Log.e("tag", e.localizedMessage!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)

                addressLatitude = place.latLng?.latitude
                addressLongitude = place.latLng?.longitude

                getAddress(addressLatitude!!, addressLongitude!!)

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i(KEY_ADDRESS, status.statusMessage!!)
            }
        }


    }

    interface AddressClickedListener {
        fun saveAddress()
    }

    companion object {
        const val TAG = "AddressBottomSheet"
    }
}