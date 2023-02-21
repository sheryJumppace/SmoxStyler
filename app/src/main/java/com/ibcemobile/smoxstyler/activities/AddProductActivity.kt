package com.ibcemobile.smoxstyler.activities

import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivityAddProductBinding
import com.ibcemobile.smoxstyler.model.ProductCategory
import com.ibcemobile.smoxstyler.utils.ImageUploadUtils
import com.ibcemobile.smoxstyler.utils.UploadImages
import com.ibcemobile.smoxstyler.utils.shortToast
import com.ibcemobile.smoxstyler.viewmodels.ProductViewModel

class AddProductActivity : BaseImagePickerActivity(), UploadImages {
    private lateinit var binding: ActivityAddProductBinding
    private var imageURL: String = String()
    var categoryId = 0
    var categoryName: String? = null
    var catList: List<ProductCategory.CategoryList>? = null
    var shipPrice = 0f
    var discPrice = 0f
    var price = 0f
    var totalPrice = 0f
    var isAvailable = 1
    lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE and
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.image.setOnClickListener {
            didOpenPhotoOption()
        }

        binding.btnDone.setOnClickListener {

            if (isValidate()) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("length", binding.etLength.text.toString())
                jsonObject.addProperty("width", binding.etWidth.text.toString())
                jsonObject.addProperty("height", binding.etHeight.text.toString())
                jsonObject.addProperty("pounds", binding.etLPound.text.toString())
                jsonObject.addProperty("ounces", binding.etOunces.text.toString())
                productViewModel.checkProduct(this, progressHUD, jsonObject)

            }
        }

        //txtCartCount.setText("" + Preferences.getInt(this, CART_COUNT));
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
            catList = it
            val dataAdapter = ArrayAdapter(
                this@AddProductActivity,
                android.R.layout.simple_spinner_item,
                catList!!
            )
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = dataAdapter
            binding.spinnerCategory.setSelection(0)
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
                Handler(Looper.getMainLooper()).postDelayed({
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
                Handler(Looper.getMainLooper()).postDelayed({
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
                Handler(Looper.getMainLooper()).postDelayed({
                    discPrice = if (s.toString().isEmpty())
                        0f
                    else
                        s.toString().toFloat()
                    if (discPrice <= price)
                        updateTotalPrice()
                    else {
                        totalPrice = 0f
                        discPrice = 0f
                        binding.etDiscPrice.setText("")
                        Toast.makeText(
                            this@AddProductActivity,
                            "Enter discounted price less than price.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, 500)

            }
        })

        binding.isAvailable.setOnCheckedChangeListener { _, isChecked ->
            isAvailable = if (isChecked) 1 else 0
        }

        productViewModel.isValidProduct.observe(this, Observer {
            if (it != null) {
                if (!it.error) {
                    addProduct()
                }else
                    shortToast(it.message)
            }
        })
    }

    private fun updateTotalPrice() {
        totalPrice = (discPrice + shipPrice)
        binding.txtTotal.text = String.format(getString(R.string.format_price, totalPrice))
    }

    private fun addProduct() {
        progressHUD.show()
        val jsonObject = JsonObject()
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
        jsonObject.addProperty("price", price.toString())
        jsonObject.addProperty("discounted_price", discPrice.toString())
        jsonObject.addProperty(
            "shipping_price",
            shipPrice.toString()
        )
        jsonObject.addProperty("total_price", totalPrice.toString())
        jsonObject.addProperty("pounds", binding.etLPound.text.toString())
        jsonObject.addProperty("ounces", binding.etOunces.text.toString())
        jsonObject.addProperty("length", binding.etLength.text.toString())
        jsonObject.addProperty("width", binding.etWidth.text.toString())
        jsonObject.addProperty("height", binding.etHeight.text.toString())
        jsonObject.addProperty("is_active", isAvailable)
        jsonObject.addProperty(
            "model", if (binding.etModel.text.isEmpty()) "" else binding.etModel.text.toString()
        )

        productViewModel.addNewProduct(this, progressHUD, jsonObject)
    }

    override fun didSelectPhoto(uri: Uri) {
        super.didSelectPhoto(uri)
        progressHUD.show()
        binding.image.setImageURI(uri)
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
        //Log.e("server image url", imageUrl)
    }

    override fun onError() {
        progressHUD.dismiss()
        Toast.makeText(this, "Unable to upload image, please try later", Toast.LENGTH_SHORT).show()
    }

    private fun isValidate(): Boolean {
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