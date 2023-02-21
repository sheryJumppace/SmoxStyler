package com.ibcemobile.smoxstyler.activities

import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.OrderDetailAdapter
import com.ibcemobile.smoxstyler.adapter.TrackOrderAdapter
import com.ibcemobile.smoxstyler.adapter.loadProductImage
import com.ibcemobile.smoxstyler.databinding.ActivityTrackOrderBinding
import com.ibcemobile.smoxstyler.model.TrackModel
import com.ibcemobile.smoxstyler.responses.OrderResponse
import com.ibcemobile.smoxstyler.utils.ORDER
import com.ibcemobile.smoxstyler.viewmodels.OrderViewModel
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter
import jp.wasabeef.recyclerview.adapters.SlideInLeftAnimationAdapter
import jp.wasabeef.recyclerview.animators.FadeInAnimator
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator


class TrackOrderActivity : BaseActivity() {
    private lateinit var binding: ActivityTrackOrderBinding
    private lateinit var orderData: OrderResponse.OrderResult
    lateinit var orderViewModel: OrderViewModel
    private lateinit var trackOrderAdapter: TrackOrderAdapter
    private lateinit var adapter: OrderDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        if (intent.getStringExtra(ORDER) != null) {
            orderData =
                Gson().fromJson(intent.getStringExtra(ORDER), OrderResponse.OrderResult::class.java)

            binding.ProductName.text =
                "Order Purchased from " + orderData.barberFname + " " + orderData.barberLname
            loadProductImage(binding.image, orderData.barberImage)
            binding.productPrice.text = getString(R.string.format_price, orderData.getTotal())
            binding.productQuantity.text = "(" + orderData.productCount + " Items add)"
            binding.txtOrderID.text = "Order#" + orderData.id
            binding.txtOrderDateTime.text = "Delivering Date " + orderData.getDate()

            binding.txtSeeItem.setOnClickListener {
                orderViewModel.getOrderDetail(this, progressHUD, orderData.id.toString())
            }

            binding.txtAddress.text = "Delivery Address\n" + orderData.address
            binding.txtOrderID.text = "Order#" + orderData.id
            binding.totalPrice.text =
                getString(R.string.format_price, orderData.subTotal!!.toDouble())
            binding.txtShippingFee.text = getString(
                R.string.format_price,
                orderData.shippingCharges!!.toDouble()
            )
            binding.txtTotalPay.text =
                getString(R.string.format_price, orderData.totalPrice!!.toDouble())

        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvProducts.layoutManager = layoutManager
        adapter = OrderDetailAdapter(this)
        trackOrderAdapter = TrackOrderAdapter(this)

        orderViewModel.orderItem.observe(this, Observer {
            if (it != null) {
                if (it.isNotEmpty()) {
                    adapter.setData(it)
                    binding.rvProducts.adapter = adapter
                    binding.rvProducts.visibility = View.VISIBLE
                } else {
                    binding.rvProducts.visibility = View.GONE

                }
            }
        })


        val layoutManager1 = LinearLayoutManager(this)
        binding.rvTrackOrder.layoutManager = layoutManager1

        binding.rvTrackOrder.itemAnimator = FadeInAnimator()
        //binding.rvTrackOrder.adapter = trackOrderAdapter
        trackOrderAdapter.saveData(trackData())
        binding.rvTrackOrder.adapter = AlphaInAnimationAdapter(trackOrderAdapter).apply {
            setFirstOnly(true)
            setDuration(1000)
            setInterpolator(OvershootInterpolator(.5f))
        }
        /*val alphaInAnimationAdapter = SlideInLeftAnimationAdapter(trackOrderAdapter)
        alphaInAnimationAdapter.setDuration(1000)
        alphaInAnimationAdapter.setInterpolator(OvershootInterpolator())
        alphaInAnimationAdapter.setFirstOnly(false)*/


        //binding.rvTrackOrder.itemAnimator = SlideInLeftAnimator()

    }


    private fun trackData() :ArrayList<TrackModel>{
        val list=ArrayList<TrackModel>()
        list.add(TrackModel("Order Placed","Your order placed successfully","June 15,2022",R.drawable.ic_order_ready_icon))
        list.add(TrackModel("Packed & Shipped","Seller has packed your Order and shipped","June 18,2022",R.drawable.ic_order_packed_icon))
        list.add(TrackModel("Out for delivery","Your order out for delivery","June 19,2022",R.drawable.ic_order_trans_icon))
        list.add(TrackModel("Delivered","Your order has delivered","June 20,2022",R.drawable.ic_order_delivered_icon))
        return list
    }
}