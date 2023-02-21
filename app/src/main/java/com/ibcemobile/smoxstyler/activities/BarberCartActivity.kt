package com.ibcemobile.smoxstyler.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.CartByBarberAdapter
import com.ibcemobile.smoxstyler.adapter.ProductByBarberAdapter
import com.ibcemobile.smoxstyler.databinding.ActivityBarberCartBinding
import com.ibcemobile.smoxstyler.responses.CartBarberItem
import com.ibcemobile.smoxstyler.utils.APPOINTMENT_ID
import com.ibcemobile.smoxstyler.utils.BARBER_ID
import com.ibcemobile.smoxstyler.viewmodels.CartViewModel
import com.ibcemobile.smoxstyler.viewmodels.ProductViewModel

class BarberCartActivity : BaseActivity() {
    private var barberAdapter= CartByBarberAdapter()
    lateinit var binding: ActivityBarberCartBinding
    lateinit var productViewModel: ProductViewModel
    lateinit var cartViewModel: CartViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarberCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        val layoutManager = LinearLayoutManager(this)
        binding.rvBCart.layoutManager = layoutManager
        binding.rvBCart.adapter = barberAdapter
        barberAdapter.setItemClickListener(object :CartByBarberAdapter.ItemClickListener{
            override fun onItemClick(product: CartBarberItem) {
                val intent = Intent(this@BarberCartActivity, CartActivity::class.java)
                intent.putExtra(BARBER_ID,product.barberId.toString())
                startActivity(intent)
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
            }
        })



        cartViewModel.barberCarts.observe(this, Observer {
            if (it!=null){
                if (!it.isNullOrEmpty()){
                    barberAdapter.submitList(it)
                    binding.txtNotFound.visibility= View.GONE

                }else{
                    barberAdapter.submitList(it)
                    binding.txtNotFound.visibility= View.VISIBLE
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        cartViewModel.getCartBarberList(this, progressHUD)

    }
}
