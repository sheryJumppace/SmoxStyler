package com.ibcemobile.smoxstyler.activities

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivityUpdateAddressBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.utils.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 *
 * Work on Smox
 *
 * Add Cancel Order Button in Order detail
 * Fix All friday Issues
 * Add Shipping UI for Shipping
 * Change Date Format in All Order
 * Add Confirm message when Delete Cart ,Delete Address,Cancel Order
 * Show and Hide Order cancel button
 *
 */

class UpdateAddressActivity : BaseActivity() {
    private lateinit var binding: ActivityUpdateAddressBinding
    private val AUTOCOMPLETE_REQUEST_CODE: Int = 1
    private val AUTOCOMPLETE_REQUEST_CODE2: Int = 2
    private var addressLatitude: Double? = 0.0
    private var addressLongitude: Double? = 0.0
    var isSellProduct = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {

            if (binding.txtAddress.text.isNotEmpty()) {
                addressLatitude?.let { it1 ->
                    addressLongitude?.let { it2 ->
                        updateAddress(
                            binding.txtAddress.text.toString(),
                            it1,
                            it2
                        )
                    }
                }
            } else
                shortToast("Please enter valid address")
        }

        binding.defaultSwitch.setOnCheckedChangeListener { button, checked ->
            isSellProduct = checked
            if (isSellProduct) {
                binding.pickupAddressLayout.visibility = View.VISIBLE
            } else
                binding.pickupAddressLayout.visibility = View.GONE
        }

        if (intent.hasExtra(KEY_ADDRESS)) {
            binding.txtAddress.text = intent.getStringExtra(KEY_ADDRESS)
            if (intent.getStringExtra(KEY_ZIP)!=null && intent.getStringExtra(KEY_ZIP)?.isNotEmpty()!!) {

                binding.etAddress1.setText(intent.getStringExtra(KEY_ADD_1))
                binding.etAddress2.setText(intent.getStringExtra(KEY_ADD_2))
                binding.etCity.setText(intent.getStringExtra(KEY_CITY))
                binding.etState.setText(intent.getStringExtra(KEY_STATE))
                binding.txtZipCode.setText(intent.getStringExtra(KEY_ZIP))

                binding.defaultSwitch.isChecked = true

            }
        }
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                Constants.PLACE_KEY_1 + Constants.PLACE_KEY_2 + Constants.PLACE_KEY_3 + Constants.PLACE_KEY_4,
                Locale.US
            )
        }

        binding.txtAddress.setOnClickListener {
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
                ).build(this@UpdateAddressActivity)
                startActivityForResult(autoSearchIntent, AUTOCOMPLETE_REQUEST_CODE)
            }
        }

        binding.etCity.setOnClickListener {
            startPlaceApi()
        }
    }

    private fun startPlaceApi() {
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
            ).build(this)
            startActivityForResult(autoSearchIntent, AUTOCOMPLETE_REQUEST_CODE2)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                addressLatitude = place.latLng?.latitude
                addressLongitude = place.latLng?.longitude
                binding.txtAddress.text = Editable.Factory.getInstance().newEditable(place.address)
            } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE2) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                setPickupAddress(place.latLng?.latitude, place.latLng?.longitude)

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                val status = Autocomplete.getStatusFromIntent(data!!)
                Log.i(KEY_ADDRESS, status.statusMessage!!)
            }
        }
    }

    private fun setPickupAddress(latitude: Double?, longitude: Double?) {
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses: List<Address> = geocoder.getFromLocation(
                latitude!!,
                longitude!!,
                1
            )
            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]

                binding.etCity.setText(address.locality?.toString())
                binding.etState.setText(address.adminArea?.toString())
                binding.txtZipCode.setText(address.postalCode?.toString())

            }
        } catch (e: IOException) {
            Log.e("tag", e.localizedMessage)
        }
    }

    private fun updateAddress(address: String, latitude: Double, longitude: Double) {
        progressHUD.show()
        val params = HashMap<String, String>()
        params["address"] = address
        params["lat"] = latitude.toString()
        params["lng"] = longitude.toString()

        if (isSellProduct) {
            if (validData()) {
                params["address_one"] = binding.etAddress1.text.toString().trim()
                params["address_two"] = binding.etAddress2.text.toString().trim()
                params["city"] = binding.etCity.text.toString().trim()
                params["state"] = binding.etState.text.toString().trim()
                params["zip_code"] = binding.txtZipCode.text.toString().trim()
            } else {
                progressHUD.dismiss()
                return
            }
        }

        APIHandler(
            this@UpdateAddressActivity,
            Request.Method.PUT,
            Constants.API.barber_address,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val barber = app.currentUser
                    barber.apply {
                        this.latitude = latitude
                        this.longitude = longitude
                        this.address = address
                    }
                    sessionManager.userData = barber.getJsonString()
                    if (isSellProduct){
                        sessionManager.sellProducts=true
                    }
                    finish()
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        this@UpdateAddressActivity,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun validData(): Boolean {
        when {
            /*address1.get().toString().isEmpty() -> {
                error.value = InputValidatorMessage(Constants.API.ADD1,
                    context.getString(R.string.error_add1))
                return false
            }*/
            binding.etAddress1.text.toString().isEmpty() -> {
                shortToast(getString(R.string.error_add1))
                return false
            }
            binding.etAddress2.text.toString().isEmpty() -> {
                shortToast(getString(R.string.error_add2))
                return false
            }
            binding.etCity.text.toString().isEmpty() -> {
                shortToast(getString(R.string.error_city))
                return false
            }
            binding.etState.text.toString().isEmpty() -> {
                shortToast(getString(R.string.error_state))
                return false
            }
            binding.txtZipCode.text.toString().isEmpty() -> {
                shortToast(getString(R.string.error_zip))
                return false
            }
            else -> return true
        }
    }

}