package com.ibcemobile.smoxstyler.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ActivityProductDetailBinding
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.responses.ProductResponse
import com.ibcemobile.smoxstyler.utils.shortToast
import com.ibcemobile.smoxstyler.viewmodels.CartViewModel

class ProductDetailActivity : BaseActivity() {
    lateinit var binding: ActivityProductDetailBinding
    lateinit var cartViewModel: CartViewModel
    var quantity = 1
    lateinit var clickRunnable: Runnable
    val handler = Handler(Looper.getMainLooper())
    lateinit var product: ProductResponse.ProductItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        product = intent.getParcelableExtra(Constants.API.PRODUCT)!!
        product.apply {
            binding.product = this
        }
        clickListeners()
        updateFinalPrice(quantity * product.price)
    }

    private fun clickListeners() {
        binding.txtAbout.setOnClickListener {
            binding.txtFeatures.background =
                ContextCompat.getDrawable(this, R.drawable.round_unselect)
            binding.txtFeatures.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
            binding.txtAbout.background =
                ContextCompat.getDrawable(this, R.drawable.round_corner)
            binding.txtAbout.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.llAboutView.visibility = View.VISIBLE
            binding.llFeaturesView.visibility = View.GONE
        }

        binding.txtFeatures.setOnClickListener {
            binding.txtFeatures.background =
                ContextCompat.getDrawable(this, R.drawable.round_corner)
            binding.txtFeatures.setTextColor(
                ContextCompat.getColor(
                    this,
                    R.color.black
                )
            )
            binding.txtAbout.background =
                ContextCompat.getDrawable(this, R.drawable.round_unselect)
            binding.txtAbout.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.llAboutView.visibility = View.GONE
            binding.llFeaturesView.visibility = View.VISIBLE
        }

        binding.imgRemove.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateFinalPrice(quantity * product.price)
                handler.removeCallbacks(clickRunnable)
                handler.postDelayed(clickRunnable, 1000)
            }else
                shortToast("quantity can't be less than 1")
        }

        binding.imgAdd.setOnClickListener {
            if (product.stock > quantity) {
                quantity++
                updateFinalPrice(quantity * product.price)
                handler.removeCallbacks(clickRunnable)
                handler.postDelayed(clickRunnable, 1000)
            } else
                shortToast("Product stock full")
        }

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnAddToCart.setOnClickListener {
            if (product.is_cart_added==0){
               updateCart()
            }else{
                Toast.makeText(this, "Product already added", Toast.LENGTH_SHORT).show()
            }
        }
        clickRunnable = Runnable {
            if (product.is_cart_added == 1)
                updateCart()
        }

        cartViewModel.isCartUpdate.observe(this, Observer {
            if (!it) {
                product.is_cart_added = 1
                binding.btnAddToCart.text=getString(R.string.addedCart)
            }
        })
    }

    private fun updateCart() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("product_id", product.id)
        jsonObject.addProperty("barber_id", product.user_id)
        jsonObject.addProperty("quantity",  quantity)
        jsonObject.addProperty("user_id", app.currentUser.id.toString())
        cartViewModel.addToCart(this, progressHUD, jsonObject)
    }

    private fun updateFinalPrice(price: Double) {
        binding.txtFinalPrice.text = getString(R.string.format_price, price)
        binding.txtQuantity.text = getString(R.string.format_quantity, quantity)
    }
}