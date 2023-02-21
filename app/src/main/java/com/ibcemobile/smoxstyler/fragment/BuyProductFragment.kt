package com.ibcemobile.smoxstyler.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.BarberCartActivity
import com.ibcemobile.smoxstyler.adapter.CartByBarberAdapter
import com.ibcemobile.smoxstyler.adapter.ProductByBarberAdapter
import com.ibcemobile.smoxstyler.databinding.BuyProductFragmentBinding
import com.ibcemobile.smoxstyler.model.type.AppointmentType
import com.ibcemobile.smoxstyler.responses.BarberResponse
import com.ibcemobile.smoxstyler.utils.showSnackbar
import com.ibcemobile.smoxstyler.viewmodels.CartViewModel
import com.ibcemobile.smoxstyler.viewmodels.ProductViewModel


class BuyProductFragment : BaseFragment() {
    private var barberAdapter= ProductByBarberAdapter()
    private lateinit var binding: BuyProductFragmentBinding
    lateinit var productViewModel: ProductViewModel
    lateinit var cartViewModel: CartViewModel
    private var productList=ArrayList<BarberResponse.BarberData>()
    var page:Int=1
    var isLastPage=false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = BuyProductFragmentBinding.inflate(inflater, container, false)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        barberAdapter.setItemClickListener(object : ProductByBarberAdapter.ItemClickListener {
            override fun onItemClick(barberData: BarberResponse.BarberData) {

                val modalBottomSheet = ProductBottomSheetFragment(barberData.id.toString(),object:OnSheetDestroy{
                    override fun onSheetDestroy() {
                        cartViewModel.getCartBarberList(requireActivity(), progressHUD)
                    }
                })
                modalBottomSheet.show(activity!!.supportFragmentManager, ProductBottomSheetFragment.TAG)

            }
        })
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvProducts.layoutManager = layoutManager
        binding.rvProducts.adapter = barberAdapter

        productViewModel.barberData.observe(viewLifecycleOwner, Observer { barbers ->
            if (barbers != null) {
                if (barbers.isNotEmpty()) {
                    isLastPage=false
                    barberAdapter.saveData(barbers)
                    binding.txtStNoProduct.visibility = View.GONE
                } else {
                    isLastPage=true
                    if (page==1){
                        binding.txtStNoProduct.visibility = View.VISIBLE
                    }else{
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "All barbers fetch",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        cartViewModel.barberCarts.observe(requireActivity(), Observer {

            if (it!=null){
                if (!it.isNullOrEmpty()){
                    binding.cartCount.text=it.size.toString()
                    binding.cartCount.visibility=View.VISIBLE
                }else{
                    binding.cartCount.visibility=View.GONE
                }
            }
        })

        binding.fabCart.setOnClickListener {
            val intent = Intent(requireActivity(), BarberCartActivity::class.java)
            startActivity(intent)
           requireActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)

        }

        binding.rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLastItemDisplaying(binding.rvProducts) && barberAdapter.itemCount >= 10) {
                    page++
                    productViewModel.getBarbers(requireActivity(), progressHUD,page)
                }
            }
        })

        return binding.root
    }

    private fun isLastItemDisplaying(recyclerView: RecyclerView): Boolean {
        if (recyclerView.adapter!!.itemCount != 0) {
            val lastVisibleItemPosition =
                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.adapter!!.itemCount - 1)
                return true
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        page=1
        isLastPage=false
        barberAdapter.clearData()
        productViewModel.getBarbers(requireActivity(), progressHUD,page)
        cartViewModel.getCartBarberList(requireActivity(), progressHUD)
    }

    interface OnSheetDestroy {
        fun onSheetDestroy()
    }
}