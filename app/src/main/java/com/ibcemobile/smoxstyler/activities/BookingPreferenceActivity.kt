package com.ibcemobile.smoxstyler.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.*
import com.ibcemobile.smoxstyler.data.ServiceRepository
import com.ibcemobile.smoxstyler.databinding.ActivityBookingPreferenceBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Category
import com.ibcemobile.smoxstyler.model.Service
import com.ibcemobile.smoxstyler.recyclerdraghelper.OnDragStartEndListener
import com.ibcemobile.smoxstyler.recyclerdraghelper.RecyclerDragHelperService
import com.ibcemobile.smoxstyler.utils.ImageUploadUtils
import com.ibcemobile.smoxstyler.utils.UploadImages
import com.ibcemobile.smoxstyler.viewmodels.ServiceListViewModel
import com.ibcemobile.smoxstyler.viewmodels.ServiceListViewModelFactory
import com.ibcemobile.smoxstyler.viewmodels.UserViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class BookingPreferenceActivity : BaseImagePickerActivity(), UploadImages, OnDragStartEndListener,
    CategorySelectorAdapter.CategorySelectActions {
    private lateinit var binding: ActivityBookingPreferenceBinding
    private val serviceAdapter = ServiceAdapter()
    private lateinit var viewModel: ServiceListViewModel
    private lateinit var userViewModel: UserViewModel
    var items: ArrayList<Category> = ArrayList()
    private var serviceTemp = ArrayList<Service>()
    var dragHelper: RecyclerDragHelperService? = null
    private var dragFromPosition = 0
    private var dragToPosition = 0
    private var isLogo = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBookingPreferenceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ServiceListViewModelFactory(ServiceRepository.getInstance(app.currentUser.id))
        viewModel = ViewModelProvider(this, factory).get(ServiceListViewModel::class.java)
        userViewModel=ViewModelProvider(this).get(UserViewModel::class.java)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.fabAddService.setOnClickListener {
            val intent = Intent(this@BookingPreferenceActivity, AddServiceActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
        }
        binding.btnAdd.setOnClickListener {
            if (TextUtils.isEmpty(binding.etCancellationFee.text)){
                binding.etCancellationFee.error="Add Cancellation Fee"
                binding.etCancellationFee.requestFocus()
            }
            else{
                updateCancellationFee(binding.etCancellationFee.text.toString().replace("%",""))
            }
        }

        serviceAdapter.setItemClickListener(object : ServiceAdapter.ItemClickListener {
            override fun onItemClick(view: View, serviceId: Service, position: Int) {
                if (view.id==R.id.imgTrash){
                    showConfirmDeleteService(serviceId,position)
                }else   if (view.id==R.id.imgEdit){
                    val gson = Gson()
                    val intent = Intent(this@BookingPreferenceActivity, AddServiceActivity::class.java)
                    intent.putExtra("service", gson.toJson(serviceId))
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.activity_enter,
                        R.anim.activity_exit
                    )
                }
            }
        })

        binding.txtAdd.setOnClickListener {
            isLogo = true
            didOpenPhotoOption()
        }

        binding.txtAddPhoto.setOnClickListener {
            isLogo = false
            didOpenPhotoOption()
        }
        initObserver()


    }


    private fun initObserver(){
        userViewModel.barberProfile.observe(this, Observer {
            if (it!=null){
                Glide.with(this@BookingPreferenceActivity)
                    .load(it.result.logo!!)
                    .placeholder(R.drawable.small_placeholder)
                    .into(binding.imgLogo)

                Glide.with(this@BookingPreferenceActivity)
                    .load(it.result.image!!)
                    .placeholder(R.drawable.small_placeholder)
                    .into(binding.imgProfile)

                binding.etCancellationFee.setText(it.result.cancellationFee)

            }
        })
        userViewModel.isUpdateProfile.observe(this, Observer {
            if (it!=null){
                //Log.e("TAG", "onCreateView: $it")
                userViewModel.getBarberProfile(this,progressHUD)
            }
        })

        userViewModel.getBarberProfile(this,progressHUD)
    }

    override fun didSelectPhoto(uri: Uri) {
        super.didSelectPhoto(uri)
        val imageUploadUtils = ImageUploadUtils()
        imageUploadUtils.onUpload(this,
            uri.path!!,
            this)
    }

    private fun updateCancellationFee(fee: String) {
        if (fee.isEmpty()) return
        val jsonObject= JsonObject()
        jsonObject.addProperty("fee",fee)
        userViewModel.updateProfile(this,progressHUD,Constants.API.cancellation_fee,jsonObject)

    }
    private fun deleteServiceFromServer(context: Context, serviceId: Service) {
        val params = HashMap<String, String>()
        progressHUD.show()
        APIHandler(
            context,
            Request.Method.DELETE,
            Constants.API.service + "/" + serviceId.id,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    doRequestForCategoryList()

                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        context.applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun doRequestForCategoryList() {
        progressHUD.show()
        val params = HashMap<String, String>()
        APIHandler(
            this,
            Request.Method.GET,
            Constants.API.get_category + "/" + app.currentUser.id,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    items.clear()
                    if (result.has("result")) {
                        val jsonArray = result.getJSONArray("result")

                        for (i in 0 until jsonArray.length()) {
                            val json = jsonArray.getJSONObject(i)
                            val category = Category(json)

                            items.add(category)

                        }

                        val categoryAdapter = CategorySelectorAdapter(this@BookingPreferenceActivity, this@BookingPreferenceActivity, false)
                        binding.rvCat.layoutManager =
                            LinearLayoutManager(this@BookingPreferenceActivity, LinearLayoutManager.HORIZONTAL, false)
                        binding.rvCat.setHasFixedSize(true)
                        binding.rvCat.adapter = categoryAdapter
                        categoryAdapter.doRefresh(items)


                        getService(0)




                    }
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

    private fun showConfirmDeleteService(serviceId: Service,position: Int) {
        val builder = AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
        builder.setMessage("Are you sure you want to delete the selected service?")
        builder.setPositiveButton("DELETE") { _, _ ->
            deleteServiceFromServer(this, serviceId)
        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        doRequestForCategoryList()
    }

    override fun onItemClick(pos: Int) {
        getService(pos)
    }
    override fun upload(imageUrl: String) {
        val jsonObject=JsonObject()
        if (isLogo){
            jsonObject.addProperty("image",imageUrl)
            userViewModel.updateProfileWithPost(this,progressHUD,Constants.API.upload_logo,jsonObject)
        }else{
            jsonObject.addProperty("image",imageUrl)
            userViewModel.updateProfileWithPost(this,progressHUD,Constants.API.upload_profile,jsonObject)
        }
    }


    override fun onDragStartListener(fromPosition: Int) {
        dragFromPosition = fromPosition
    }

    override fun onDragEndListener(toPosition: Int) {
        dragToPosition = toPosition
        changeItemPosition()
    }

    private fun changeItemPosition() {
        if (dragToPosition != -1) {
            val item: Service = serviceTemp.removeAt(dragFromPosition)
            serviceTemp.add(dragToPosition, item)
            if (serviceTemp.size > 0) {
                val jsonArray = JSONArray()
                for (x in 0 until serviceTemp.size) {
                    val a = serviceTemp[x]
                    jsonArray.put(x, JSONObject().put("id", a.id).put("position_id", x + 1))
                }
                val jsonObject = JSONObject().put("service_data", jsonArray)
                serviceAdapter.submitList(serviceTemp)
                serviceAdapter.notifyDataSetChanged()
                viewModel.sendReorderService(this, jsonObject)
            }
        }
    }

    private fun getService(position: Int){
        binding.rvService.layoutManager = LinearLayoutManager(this@BookingPreferenceActivity)
        binding.rvService.adapter = serviceAdapter
        serviceAdapter.submitList(items[position].services)
        serviceTemp = items[position].services
        if(items[position].services.isNullOrEmpty()){
            binding.tvNotFound.visibility=View.VISIBLE
        }else{
            binding.tvNotFound.visibility=View.GONE
        }
        serviceAdapter.notifyDataSetChanged()


        dragHelper = RecyclerDragHelperService(items[position].services, serviceAdapter)
        dragHelper!!.onDragStartEndListener = this@BookingPreferenceActivity
        val itemTouchHelper = ItemTouchHelper(dragHelper!!)
        itemTouchHelper.attachToRecyclerView(binding.rvService)
    }

    override fun onError() {

    }

}