package com.ibcemobile.smoxstyler.activities

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivityEditProductBinding
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.ProductCategory
import com.ibcemobile.smoxstyler.responses.ProductResponse
import com.ibcemobile.smoxstyler.utils.ImageUploadUtils
import com.ibcemobile.smoxstyler.utils.UploadImages
import com.ibcemobile.smoxstyler.utils.shortToast
import com.ibcemobile.smoxstyler.viewmodels.ProductViewModel
import java.io.File

class EditProductActivity : BaseImagePickerActivity(), UploadImages {
    private lateinit var binding: ActivityEditProductBinding
    private var product: ProductResponse.ProductItem? = null
    lateinit var productViewModel: ProductViewModel
    var catList: List<ProductCategory.CategoryList>? = null
    var catPos = 0
    var categoryId = 0
    var categoryName = ""
    var shipPrice = 0f
    var discPrice = 0f
    var price = 0f
    var totalPrice = 0f
    var isAvailable = 1
    private var imageURL: String = String()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        product = intent.getParcelableExtra("product")!!

        product?.apply {
            if (main_img.isNotEmpty()) {
                val url = Constants.downloadUrlOfProduct(main_img)

                Glide.with(this@EditProductActivity)
                    .load(url)
                    .apply(
                        RequestOptions().transform(RoundedCorners(7)).fitCenter()
                            .placeholder(R.drawable.ic_select_image)
                    )
                    .into(binding.image)
            }
            imageURL=main_img
            binding.etProduct.setText(product_name)
            binding.etDescription.setText(description)
            binding.etFeatures.setText(feature)
            binding.etPrice.setText(price.toInt().toString())
            binding.etShippingPrice.setText(shipping_price.toInt().toString())
            binding.etDiscPrice.setText(discountedPrice.toInt().toString())
            binding.etModel.setText(model)
            binding.etStock.setText(stock.toString())
            val totalPrice=discountedPrice.toInt()+shipping_price.toInt()
            binding.txtTotal.text = totalPrice.toString()
            categoryId=category_id
            binding.isAvailable.isChecked = is_active != 0

            binding.etLPound.setText(pounds.toString())
            binding.etOunces.setText(ounces.toString())
            binding.etLength.setText(length.toString())
            binding.etHeight.setText(height.toString())
            binding.etWidth.setText(width.toString())


        }

        binding.btnDone.setOnClickListener {
            if (validation()) {
                val jsonObject=JsonObject()
                jsonObject.addProperty("length",binding.etLength.text.toString())
                jsonObject.addProperty("width",binding.etWidth.text.toString())
                jsonObject.addProperty("height",binding.etHeight.text.toString())
                jsonObject.addProperty("pounds",binding.etLPound.text.toString())
                jsonObject.addProperty("ounces",binding.etOunces.text.toString())

                productViewModel.checkProduct(this,progressHUD,jsonObject)
            }
        }
        binding.image.setOnClickListener {
            didOpenPhotoOption()
        }

        binding.spinnerCategory.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                pos: Int,
                l: Long
            ) {
                categoryId = catList?.get(pos)?.id!!
                categoryName = catList?.get(pos)?.category_name!!
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        productViewModel.productCategory.observe(this) {
            catList=it
            val dataAdapter = ArrayAdapter(
                this@EditProductActivity,
                android.R.layout.simple_spinner_item,
                catList!!
            )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = dataAdapter
            val item= catList?.find { it.id==categoryId }
            val index=it.indexOf(item)

            binding.spinnerCategory.setSelection(index)
        }

        initEditView()
        productViewModel.getCategoryList(this, progressHUD)
    }

    private fun initEditView() {
        binding.etPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Handler(mainLooper).postDelayed({
                    price = if (s.toString().isEmpty())
                        0f
                    else
                        s.toString().toFloat()
                    updateTotalPrice()
                }, 500)
            }
        })
        binding.etShippingPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Handler(mainLooper).postDelayed({
                    shipPrice = if (s.toString().isEmpty())
                        0f
                    else
                        s.toString().toFloat()
                    updateTotalPrice()
                }, 500)
            }
        })
        binding.etDiscPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Handler(mainLooper).postDelayed({
                    discPrice = if (s.toString().isEmpty())
                        0f
                    else
                        s.toString().toFloat()
                    updateTotalPrice()
                }, 500)
            }
        })

        binding.isAvailable.setOnCheckedChangeListener { buttonView, isChecked ->
            isAvailable = if (isChecked) 1 else 0
        }

        productViewModel.isValidProduct.observe(this, Observer {
            if (it!=null){
                Log.e("TAG", "initEditView: "+it.message )
                Toast.makeText(this, it.result,Toast.LENGTH_LONG).show()
                if(!it.error){
                    updateProduct()
                }else
                    shortToast(it.result)
            }
        })
    }

    private fun updateTotalPrice() {
        totalPrice = ((price + shipPrice) - discPrice)
        binding.txtTotal.text = String.format(getString(R.string.format_price, totalPrice))
    }

    override fun didSelectPhoto(uri: Uri) {
        super.didSelectPhoto(uri)
        Glide.with(this@EditProductActivity)
            .load(File(uri.path))
            .apply(
                RequestOptions().transform(RoundedCorners(7)).fitCenter()
                    .placeholder(R.drawable.ic_select_image)
            )
            .into(binding.image)
        progressHUD.show()
        val imageUploadUtils = ImageUploadUtils()
        imageUploadUtils.onUpload(
            this,
            uri.path!!,
            this
        )
    }

    override fun upload(imageUrl: String) {
        progressHUD.dismiss()
        imageURL = imageUrl
        Log.e("server image url", imageUrl)
    }

    override fun onError() {
        progressHUD.dismiss()
        Toast.makeText(this, "Unable to upload image, please try later", Toast.LENGTH_SHORT).show()
    }

    private fun updateProduct() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("product_id", product?.id)
        jsonObject.addProperty("user_id", app.currentUser.id)
        jsonObject.addProperty("category_id", categoryId)
        jsonObject.addProperty("category_name", categoryName)
        jsonObject.addProperty("product_name", binding.etProduct.text.toString())
        jsonObject.addProperty("main_img", imageURL)
        jsonObject.addProperty(
            "stock", (if (binding.etStock.text.isEmpty()) 0 else binding.etStock.text.toString()
                .toInt()).toString()
        )
        jsonObject.addProperty("description", binding.etDescription.text.toString())
        jsonObject.addProperty("feature", binding.etFeatures.text.toString())
        jsonObject.addProperty("price", binding.etPrice.text.toString())
        jsonObject.addProperty("discounted_price", binding.etDiscPrice.text.toString())
        jsonObject.addProperty(
            "shipping_price",
            binding.etShippingPrice.text.toString()
        )
        jsonObject.addProperty("pounds", binding.etLPound.text.toString())
        jsonObject.addProperty("ounces", binding.etOunces.text.toString())
        jsonObject.addProperty("length", binding.etLength.text.toString())
        jsonObject.addProperty("width", binding.etWidth.text.toString())
        jsonObject.addProperty("height", binding.etHeight.text.toString())
        jsonObject.addProperty("total_price", binding.txtTotal.text.toString())
        jsonObject.addProperty("is_active", isAvailable)
        jsonObject.addProperty(
            "model", (if (binding.etModel.text.isEmpty()) "" else binding.etModel.text.toString())
        )

        productViewModel.updateProduct(this, progressHUD, jsonObject)

    }

    private fun validation()//////Check Validation
            : Boolean {
        when {
            TextUtils.isEmpty(imageURL) -> {
                Toast.makeText(this, "Select Product Image", Toast.LENGTH_LONG).show()
                return false
            }
            TextUtils.isEmpty(binding.etProduct.text.toString()) -> {
                binding.etProduct.error = "Product Name"
                binding.etProduct.requestFocus()
                return false
            }
            TextUtils.isEmpty(binding.etDescription.text.toString()) -> {
                binding.etDescription.error = "Description"
                binding.etDescription.requestFocus()
                return false
            }
            TextUtils.isEmpty(binding.etFeatures.text.toString()) -> {
                binding.etFeatures.error = "Features"
                binding.etFeatures.requestFocus()
                return false
            }
            TextUtils.isEmpty(binding.etPrice.text.toString()) -> {
                binding.etPrice.error = "Price"
                binding.etPrice.requestFocus()
                return false
            }
            TextUtils.isEmpty(binding.etStock.text.toString()) -> {
                binding.etStock.error = "Stock"
                binding.etStock.requestFocus()
                return false
            }

            TextUtils.isEmpty(binding.etLength.text.toString()) -> {
                binding.etLength.error = "Length"
                binding.etLength.requestFocus()
                return false
            }

            TextUtils.isEmpty(binding.etWidth.text.toString()) -> {
                binding.etWidth.error = "Width"
                binding.etWidth.requestFocus()
                return false
            }

            TextUtils.isEmpty(binding.etHeight.text.toString()) -> {
                binding.etHeight.error = "Height"
                binding.etHeight.requestFocus()
                return false
            }

            TextUtils.isEmpty(binding.etLPound.text.toString()) -> {
                binding.etLPound.error = "Pounds"
                binding.etLPound.requestFocus()
                return false
            }

            TextUtils.isEmpty(binding.etOunces.text.toString()) -> {
                binding.etOunces.error = "Ounces"
                binding.etOunces.requestFocus()
                return false
            }

            else -> {
                return true
            }
        }
    }



}