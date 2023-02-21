package com.ibcemobile.smoxstyler.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.AddressAdapter
import com.ibcemobile.smoxstyler.databinding.ActivityAddAddressBinding
import com.ibcemobile.smoxstyler.fragment.AddressBottomSheetFragment
import com.ibcemobile.smoxstyler.responses.AddressResponse
import com.ibcemobile.smoxstyler.utils.ADDRESS
import com.ibcemobile.smoxstyler.utils.APPOINTMENT_ID
import com.ibcemobile.smoxstyler.viewmodels.AddressViewModel
import java.util.*


class AddAddressActivity : BaseActivity(), AddressAdapter.MyItemClickListener,
    AddressBottomSheetFragment.AddressClickedListener {
    lateinit var binding: ActivityAddAddressBinding
    private lateinit var addressViewModel: AddressViewModel
    lateinit var addressAdapter: AddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        addressViewModel = ViewModelProvider(this).get(AddressViewModel::class.java)

        binding.addAddress.setOnClickListener {
            val modalBottomSheet = AddressBottomSheetFragment(this, null)
            modalBottomSheet.show(supportFragmentManager, AddressBottomSheetFragment.TAG)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvAddress.layoutManager = layoutManager
        addressAdapter = AddressAdapter(this)
        binding.rvAddress.adapter = addressAdapter

        addressViewModel.addressData.observe(this, Observer {
            if (it != null) {
                binding.txtNotFound.visibility = View.GONE
                addressAdapter.setData(it)
                binding.rvAddress.adapter = addressAdapter

                if (it.isEmpty()) {
                    binding.txtNotFound.visibility = View.VISIBLE
                }
            } else {
                binding.txtNotFound.visibility = View.VISIBLE
            }
        })

        addressViewModel.isDeleted.observe(this, Observer {
            if (it != null) {
                if (!it) {
                    addressViewModel.getAllAddress(this, progressHUD)
                }
            }
        })

    }

    override fun editClick(addressData: AddressResponse.AddressData) {
        val modalBottomSheet = AddressBottomSheetFragment(this, addressData)
        modalBottomSheet.show(supportFragmentManager, AddressBottomSheetFragment.TAG)
    }

    private fun showConfirmDeleteDialog(addressData: AddressResponse.AddressData) {
        val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        builder.setTitle("DELETE ADDRESS")
        builder.setMessage("Are you sure you want to delete address?")
        builder.setPositiveButton("DELETE") { _, _ ->
            val jsonObject = JsonObject()
            jsonObject.addProperty("address_id", addressData.id)
            addressViewModel.deleteAddress(this, progressHUD, jsonObject)
        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }

    override fun deleteClick(addressData: AddressResponse.AddressData) {
        showConfirmDeleteDialog(addressData)

    }

    override fun selectClick(addressData: AddressResponse.AddressData) {
        addressViewModel.isUpdate.observe(this, Observer {
            if (it != null) {
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra(ADDRESS, Gson().toJson(addressData))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })
        val jsonObject = getJsonData(addressData)
        addressViewModel.updateAddress(this, progressHUD, jsonObject)
    }

    private fun getJsonData(addressData: AddressResponse.AddressData): JsonObject {

        val jsonObject = JsonObject()
        jsonObject.addProperty("address_id", addressData.id)
        jsonObject.addProperty("first_name", addressData.first_name)
        jsonObject.addProperty("last_name", addressData.last_name)
        jsonObject.addProperty("address_one", addressData.address_one)
        jsonObject.addProperty("address_two", addressData.address_two)
        jsonObject.addProperty("city", addressData.city)
        jsonObject.addProperty("state", addressData.state)
        jsonObject.addProperty("country", addressData.country)
        jsonObject.addProperty("zipcode", addressData.zipcode)
        jsonObject.addProperty("latitude", addressData.latitude)
        jsonObject.addProperty("longitude", addressData.longitude)
        jsonObject.addProperty("phone", addressData.phone)
        jsonObject.addProperty("make_default", "1")

        return jsonObject
    }

    override fun onResume() {
        super.onResume()
        addressViewModel.getAllAddress(this, progressHUD)

    }

    override fun saveAddress() {
        addressViewModel.getAllAddress(this, progressHUD)
    }

}