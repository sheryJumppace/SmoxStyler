package com.ibcemobile.smoxstyler.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.CartAdapter
import com.ibcemobile.smoxstyler.databinding.ActivityCartBinding
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.responses.AddressResponse
import com.ibcemobile.smoxstyler.responses.CartData
import com.ibcemobile.smoxstyler.utils.ADDRESS
import com.ibcemobile.smoxstyler.utils.BARBER_ID
import com.ibcemobile.smoxstyler.utils.showDialogOrderPlace
import com.ibcemobile.smoxstyler.utils.showSnackbar
import com.ibcemobile.smoxstyler.viewmodels.CartViewModel
import com.ibcemobile.smoxstyler.viewmodels.OrderViewModel

/**
 * Work on SMox
 * Fix shipping issue
 * fix shipping price issue
 *
 * Work on Dating App
 * Create UI gender picker and Also working
 * Create UI Design About me
 * Add Location Picker UI
 * Add Passion Picker UI and Working
 * Working on Sun Sign picker
 * Add Animation on all transition
 *
 */
class CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    lateinit var cartViewModel: CartViewModel
    lateinit var orderViewModel: OrderViewModel
    lateinit var cartAdapter: CartAdapter
    lateinit var cartFreshData: CartData.CartItems
    private var runnable: Runnable? = null
    private var newHandler: Handler? = null
    private var cartIdList = ArrayList<Int>()
    private var discPrice: String? = null
    private var barBerId: String? = null
    lateinit var resultPayment: ActivityResultLauncher<Intent>

    private lateinit var addressData: AddressResponse.AddressData
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        newHandler = Handler(mainLooper)
        initObserver()

        runnable = Runnable {
            updateCartItem(cartFreshData)
        }

        binding.txtEdit.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            launcher.launch(intent)
            overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
        }



        resultPayment =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val intent = result.data
                intent?.getStringExtra(Constants.API.PAY_STATUS)
                intent?.getStringExtra(Constants.API.PAY_MESSAGE)

                val jsonObject = getJsonObject()
                val arr = JsonArray()
                for (item in orderViewModel.orderPlace.value?.checkout_id!!) {
                    arr.add(item)
                }
                jsonObject.addProperty(
                    "client_secret",
                    orderViewModel.orderPlace.value?.client_secret
                )
                jsonObject.add(
                    "checkout_id",
                    arr
                )

                orderViewModel.confirmPayment(this, progressHUD, jsonObject)

            }
    }

    private fun checkout() {
        val jsonObject = getJsonObject()
        orderViewModel.placeOrder(this, progressHUD, jsonObject)
    }

    private fun getJsonObject(): JsonObject {
        val cartData = cartViewModel.cartData.value
        val cartAmountObject = JsonObject()
        for (item in cartData!!.cart_items) {
            val jsonObject1 = JsonObject()
            jsonObject1.addProperty("qty_discounted_price", item.product.qty_discounted_price)
            jsonObject1.addProperty("qty_price", item.product.qty_price)
            jsonObject1.addProperty("qty_shipping", item.product.qty_shipping)
            jsonObject1.addProperty("quantity", item.quantity)
            jsonObject1.addProperty("cart_id", item.id)
            cartAmountObject.add(item.product_id.toString(), jsonObject1)
        }
        val arr = JsonArray()
        for (item in cartIdList) {
            arr.add(item)
        }
        val address =
            addressData.first_name + " " + addressData.last_name + " \n" + addressData.address_one + ", " + addressData.address_two + ", " +
                    addressData.city + ", " + addressData.state + ", " + addressData.country + ", " + addressData.zipcode
        val jsonObject = JsonObject()
        jsonObject.addProperty("barber_id", barBerId)
        jsonObject.addProperty("name", addressData.first_name + " " + addressData.last_name)
        jsonObject.addProperty("phone", addressData.phone)
        jsonObject.addProperty("email", app.currentUser.email)
        jsonObject.addProperty("address_id", addressData.id)
        jsonObject.addProperty("address", address)
        jsonObject.addProperty("subtotal", binding.totalPrice.text.toString().replace("$", ""))
        jsonObject.addProperty("discounted_price", discPrice)
        //jsonObject.addProperty("discount",binding.txtDiscPrice.text.toString().replace("$",""))
        jsonObject.addProperty("shipping", binding.txtShippingFee.text.toString().replace("$", ""))
        jsonObject.addProperty("total", binding.txtTotalPay.text.toString().replace("$", ""))
        jsonObject.add("cart_id", arr)
        jsonObject.addProperty("latitude", addressData.latitude)
        jsonObject.addProperty("longitude", addressData.longitude)
        jsonObject.add("single_product", cartAmountObject)

        return jsonObject
    }

    /**
     * Work on Date me App
     * Create Home Screen UI Design
     * create Notification Activity
     * Create Coins Activity
     * Create Pick Location Bottom Navigation
     * Create Profile UI Design
     * Working on Profile Details Design
     *
     * Work on SMOX
     * Fix Checkout API Issue
     * Remove Discount From Checkout API
     */

    @SuppressLint("SetTextI18n")
    private fun initObserver() {
        binding.rvCart.layoutManager = LinearLayoutManager(this@CartActivity)
        cartAdapter = CartAdapter(this)
        cartAdapter.setItemClickListener(object : CartAdapter.MyItemClickListener {
            override fun clicked(view: View, cartData: CartData.CartItems) {
                when (view.id) {
                    R.id.imgDelete -> {
                        cartData.quantity = 0
                        showConfirmCart(cartData)
                    }
                    R.id.imgRemove -> {
                        if (cartData.quantity > 1) {
                            cartData.quantity--
                            cartAdapter.notifyDataSetChanged()
                            binding.btnCheckbox.isEnabled = false
                            cartFreshData = cartData
                            newHandler!!.removeCallbacks(runnable!!)
                            newHandler!!.postDelayed(runnable!!, 1000)
                        }

                    }
                    R.id.imgAdd -> {

                        if (cartData.quantity < cartData.product.stock) {
                            cartData.quantity++
                            cartAdapter.notifyDataSetChanged()
                            binding.btnCheckbox.isEnabled = false
                            cartFreshData = cartData
                            newHandler!!.removeCallbacks(runnable!!)
                            newHandler!!.postDelayed(runnable!!, 1000)
                        }
                    }
                }

            }

        })

        orderViewModel.isOrderPlaced.observe(this, Observer {
            if (it) {
                showDialogOrderPlace(this)
            }
        })

        orderViewModel.orderPlace.observe(this, Observer {
            if (it != null) {
                Log.e("TAG", "initObserver: $it")

                val intent = Intent(
                    this,

                    ServiceCheckoutActivity::class.java
                )
                intent.putExtra(
                    Constants.API.PAYMENT_INTENT,
                    it.client_secret
                )
                resultPayment.launch(intent)


            }
        })

        cartViewModel.cartData.observe(this, Observer {
            progressHUD.dismiss()
            if (it != null) {
                cartIdList.clear()
                Log.e("TAG", "onCreate: $it")
                if (!it.cart_items.isNullOrEmpty()) {
                    for (cartItem in it.cart_items) {
                        cartIdList.add(cartItem.id)

                    }
                    // barBerId=it.cart_items[0].barber_id.toString()
                    cartAdapter.setData(it.cart_items)
                    binding.rvCart.adapter = cartAdapter

                    if (it.cart_items.size > 0) {
                        binding.txtAddress.text = it.defaultAddress()
                    }

                    addressData = it.default_address
                    binding.totalPrice.text = getString(R.string.format_price, it.discounted_price)
                    binding.txtDiscPrice.text = getString(R.string.format_price, it.discount)
                    discPrice = it.discounted_price.toString()
                    binding.txtShippingFee.text = getString(R.string.format_price, it.shipping)
                    binding.txtTotalPay.text = getString(R.string.format_price, it.total)
                    binding.btnCheckbox.isEnabled = true

                    if (it.zip_error == "") {
                        binding.btnCheckbox.setOnClickListener {
                            binding.btnCheckbox.isEnabled = false
                            checkout()
                        }
                    } else {
                        binding.btnCheckbox.isEnabled = true
                        showAlertDialog(it.zip_error)
                    }
                }else{
                    onBackPressed()
                }
            }
        })

        if (intent.getStringExtra(BARBER_ID) != null) {
            barBerId = intent.getStringExtra(BARBER_ID).toString()
            progressHUD.show()

            cartViewModel.getCartListByBarber(this, progressHUD, barBerId!!)

        }

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    val intent = it.data
                    if (intent!!.getStringExtra(ADDRESS) != null) {
                        addressData = Gson().fromJson(
                            intent.getStringExtra(ADDRESS),
                            AddressResponse.AddressData::class.java
                        )
                        val address =
                            addressData.first_name + " " + addressData.last_name + " \n" + addressData.address_one + ", " + addressData.city + ", " + addressData.state + ", " + addressData.zipcode
                        binding.txtAddress.text = address
                        cartViewModel.getCartListByBarber(this, progressHUD, barBerId!!)

                    }
                }
            }
    }

    private fun showConfirmCart(cartData: CartData.CartItems) {
        val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        builder.setTitle("DELETE ITEM")
        builder.setMessage("Are you sure you want to delete item?")
        builder.setPositiveButton("CONFIRM") { _, _ ->
            updateCartItem(cartData)
        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }

    private fun updateCartItem(cartData: CartData.CartItems) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("product_id", cartData.product_id)
        jsonObject.addProperty("barber_id", cartData.barber_id)
        jsonObject.addProperty("user_id", app.currentUser.id.toString())
        jsonObject.addProperty("quantity", cartData.quantity.toString())
        cartViewModel.updateCart(this, progressHUD, jsonObject, app.currentUser.id.toString())
    }
}