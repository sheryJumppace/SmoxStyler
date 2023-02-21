package com.ibcemobile.smoxstyler.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.ServiceAdapter
import com.ibcemobile.smoxstyler.data.ServiceRepository
import com.ibcemobile.smoxstyler.databinding.ActivityAddServiceBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Category
import com.ibcemobile.smoxstyler.model.Service
import com.ibcemobile.smoxstyler.utils.ImageUploadUtils
import com.ibcemobile.smoxstyler.utils.UploadImages
import com.ibcemobile.smoxstyler.viewmodels.ServiceListViewModel
import com.ibcemobile.smoxstyler.viewmodels.ServiceListViewModelFactory
import org.json.JSONObject
import java.util.*

class AddServiceActivity : BaseImagePickerActivity(), UploadImages {
    private lateinit var binding: ActivityAddServiceBinding
    private lateinit var viewModel: ServiceListViewModel
    private var imageURL:String= String()
    val adapter = ServiceAdapter()
    var services: ArrayList<Service> = ArrayList<Service>()
    var items: ArrayList<Category> = ArrayList()
    val values: ArrayList<String> = ArrayList()
    private var catId:Int=0
    private var serviceID:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = ServiceListViewModelFactory(ServiceRepository.getInstance(app.currentUser.id))
        viewModel = ViewModelProvider(this, factory).get(ServiceListViewModel::class.java)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.image.setOnClickListener {
            didOpenPhotoOption()
        }
        doRequestForCategoryList()

        if (intent.hasExtra("service")){
            binding.txtTitleBar.text=getString(R.string.txt_edit_service)
            val gson = Gson()
            val service = gson.fromJson<Service>(intent.getStringExtra("service"), Service::class.java)
            catId=service.category_id.toInt()
            Glide.with(this)
                .load(service.image)
                .into(binding.image)
            imageURL=service.image
            binding.etDuration.setText(service.duration.toString())
            binding.etPrice.setText(service.price.toString())
            binding.etServiceDes.setText(service.serviceDescription)
            binding.etServiceName.setText(service.title)

            serviceID=service.id
            binding.btnDone.text="Update"
            binding.btnDone.setOnClickListener {
                addService(true)
            }

        }else{
            binding.btnDone.text="Done"
            binding.btnDone.setOnClickListener {
                addService(false)
            }
        }
    }

    override fun didSelectPhoto(uri: Uri) {
        super.didSelectPhoto(uri)
        Glide.with(this)
            .load(uri)
            .into(binding.image)
        val imageUploadUtils = ImageUploadUtils()

        imageUploadUtils.onUpload(this,
            uri.path!!,
            this)

    }

    private fun doRequestForCategoryList() {
        //progressHUD.show()
        val params = HashMap<String, String>()
        APIHandler(
            this,
            Request.Method.GET,
            Constants.API.get_category + "/" + app.currentUser.id,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    //progressHUD.dismiss()

                    items.clear()
                    if (result.has("result")) {
                        val jsonArray = result.getJSONArray("result")

                        for (i in 0 until jsonArray.length()) {
                            val json = jsonArray.getJSONObject(i)
                            val category = Category(json)
                            items.add(category)
                            values.add(category.cat_name.toString())
                        }

                        binding.powerSpinner.setItems(values)

                        binding.powerSpinner.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
                            catId=newIndex+1

                        }
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


    private fun addService(isUpdate:Boolean) {

        if(imageURL.isEmpty()){
            Toast.makeText(applicationContext, "Select Service Image", Toast.LENGTH_LONG)
                .show()
            return
        }
        if(catId.toString().isEmpty()){
            Toast.makeText(applicationContext, "Select Category", Toast.LENGTH_LONG)
                .show()
            return
        }
        if(TextUtils.isEmpty(binding.etServiceName.text)){
            Toast.makeText(applicationContext, "Service name is empty", Toast.LENGTH_LONG)
                .show()
            return
        }
        if(TextUtils.isEmpty(binding.etServiceDes.text)){
            Toast.makeText(applicationContext, "Service Description is empty", Toast.LENGTH_LONG)
                .show()
            return
        }
        if(TextUtils.isEmpty(binding.etPrice.text)){
            Toast.makeText(applicationContext, "Service Price is empty", Toast.LENGTH_LONG)
                .show()
            return
        }

        if(TextUtils.isEmpty(binding.etDuration.text)){
            Toast.makeText(applicationContext, "Service Duration is empty", Toast.LENGTH_LONG)
                .show()
            return
        }
        progressHUD.show()
        val params = HashMap<String, String>()
        params["id"] = serviceID.toString()
        params["category_id"] = catId.toString()
        params["user_id"] = app.currentUser.id.toString()
        params["title"] = binding.etServiceName.text.toString()
        params["description"] = binding.etServiceDes.text.toString()
        params["price"] = binding.etPrice.text.toString()
        params["image"] = imageURL
        params["duration"] = binding.etDuration.text.toString()
        if (isUpdate){
            APIHandler(
                this@AddServiceActivity,
                Request.Method.PUT,
                Constants.API.service,
                params,
                object : APIHandler.NetworkListener {
                    override fun onResult(result: JSONObject) {
                        progressHUD.dismiss()
                        finish()
                    }

                    override fun onFail(error: String?) {
                        progressHUD.dismiss()
                       // Log.e("Data Json Object Result", error.toString())
                    }
                }
            )
        }else{
            APIHandler(
                this@AddServiceActivity,
                Request.Method.POST,
                Constants.API.add_service,
                params,
                object : APIHandler.NetworkListener {
                    override fun onResult(result: JSONObject) {
                        //Log.e("Data Json Object Result", result.toString())
                        progressHUD.dismiss()
                        finish()
                    }

                    override fun onFail(error: String?) {
                        progressHUD.dismiss()
                        //Log.e("Data Json Object Result", error.toString())
                    }
                }
            )
        }

    }



    override fun upload(imageUrl: String) {
        imageURL=imageUrl
    }

    override fun onError() {

    }


}