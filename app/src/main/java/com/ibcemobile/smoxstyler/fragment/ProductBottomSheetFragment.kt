package com.ibcemobile.smoxstyler.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.activities.ProductDetailActivity
import com.ibcemobile.smoxstyler.adapter.ProductAdapter
import com.ibcemobile.smoxstyler.databinding.ProductBottomSheetContentBinding
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.responses.ProductResponse
import com.ibcemobile.smoxstyler.viewmodels.CartViewModel
import com.ibcemobile.smoxstyler.viewmodels.ProductViewModel
import com.kaopiz.kprogresshud.KProgressHUD

class ProductBottomSheetFragment(val barBerId: String, val onDestroy:BuyProductFragment.OnSheetDestroy) : BottomSheetDialogFragment() {
    private lateinit var adapter: ProductAdapter
    private lateinit var binding: ProductBottomSheetContentBinding
    lateinit var productViewModel: ProductViewModel
    lateinit var cartViewModel: CartViewModel
    var cartListId = arrayListOf<Int>()
    var newAddCartId = 0
    var isDetailClick=false
    lateinit var progressHUD:KProgressHUD

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ProductBottomSheetContentBinding.inflate(inflater, container, false)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        progressHUD = KProgressHUD(requireContext())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)

        initAdapter(progressHUD)
        initObserver(progressHUD)
        return binding.root
    }


    private fun initObserver(progressHUD: KProgressHUD) {
        cartViewModel.isCartUpdate.observe(viewLifecycleOwner, Observer {
            if (!it) {
                cartListId.add(newAddCartId)
                productViewModel.getProductsByBarber(
                    requireActivity(),
                    progressHUD,
                    barBerId,
                    App.instance.currentUser.id
                )
            }
        })

        cartViewModel.cartData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.cart_items != null && it.cart_items.isNotEmpty()) {
                    for (item in it.cart_items) {
                        cartListId.add(item.product_id)
                    }
                }
            }
            productViewModel.getProductsByBarber(
                requireActivity(), progressHUD, barBerId,
                App.instance.currentUser.id
            )
        })

        productViewModel.productRes.value = null
        productViewModel.productRes.observe(viewLifecycleOwner, Observer { products ->
            if (products != null) {
                if (products.result.isNotEmpty()) {
                    if (cartListId.size > 0) {
                        for (prodItem in products.result) {
                            val ind = cartListId.find { it == prodItem.id }
                            if (ind != null) {
                                prodItem.is_cart_added = 1
                            }
                        }
                    }

                    adapter.setData(products.result)
                    binding.rvProducts.adapter = adapter
                    binding.txtStNoProduct.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                } else {
                    binding.txtStNoProduct.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.GONE

                }
            }
        })
    }


    private fun initAdapter(progressHUD: KProgressHUD) {
        val layoutManager =
            GridLayoutManager(requireActivity(), 2, LinearLayoutManager.VERTICAL, false)
        binding.rvProducts.layoutManager = layoutManager
        adapter = ProductAdapter(requireActivity())
        adapter.setItemClickListener(object : ProductAdapter.ItemClickListener {
            override fun onItemClick(view: View, p: ProductResponse.ProductItem) {
                when (view.id) {
                    R.id.rl_root -> {
                        val intent = Intent(activity, ProductDetailActivity::class.java)
                        intent.putExtra(Constants.API.PRODUCT, p)
                        startActivity(intent)
                        activity!!.overridePendingTransition(
                            R.anim.activity_enter,
                            R.anim.activity_exit
                        )
                        isDetailClick=true
                    }
                    R.id.btnAddToCart -> {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("product_id", p.id)
                        jsonObject.addProperty("barber_id", p.user_id)
                        jsonObject.addProperty("quantity", "1")
                        jsonObject.addProperty("user_id", App.instance.currentUser.id.toString())
                        newAddCartId = p.id
                        cartViewModel.addToCart(requireActivity(), progressHUD, jsonObject)
                    }

                }
            }
        })
    }

    companion object {
        const val TAG = "ProductBottomSheet"
    }

    override fun onResume() {
        super.onResume()

            cartViewModel.getCartListByBarber(requireContext(), progressHUD, barBerId)

    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroy.onSheetDestroy()
    }
}