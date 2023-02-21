package com.ibcemobile.smoxstyler.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.CartActivity
import com.ibcemobile.smoxstyler.activities.TrackOrderActivity
import com.ibcemobile.smoxstyler.adapter.ProductBoughtAdapter
import com.ibcemobile.smoxstyler.databinding.ProductBoughtFragmentBinding
import com.ibcemobile.smoxstyler.model.type.OrderType
import com.ibcemobile.smoxstyler.responses.OrderResponse
import com.ibcemobile.smoxstyler.utils.ADDRESS
import com.ibcemobile.smoxstyler.utils.ORDER
import com.ibcemobile.smoxstyler.viewmodels.OrderViewModel
import com.ibcemobile.smoxstyler.viewmodels.ProductViewModel


class ProductBoughtFragment : BaseFragment() {
    private lateinit var binding: ProductBoughtFragmentBinding
    lateinit var orderViewModel: OrderViewModel
    private var productBoughtAdapter: ProductBoughtAdapter? = null
    private var page: Int = 1
    private var isLastPage = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ProductBoughtFragmentBinding.inflate(inflater, container, false)

        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        binding.rvOrder.layoutManager = LinearLayoutManager(requireActivity())
        productBoughtAdapter = ProductBoughtAdapter(requireActivity())
        binding.rvOrder.adapter=productBoughtAdapter
        orderViewModel.orderResponse.observe(viewLifecycleOwner, Observer {

            if (it != null) {
                if (!it.isNullOrEmpty()) {
                    productBoughtAdapter?.saveData(it)
                    binding.txtNotFound.visibility = View.GONE

                } else {
                    isLastPage = true
                    if (page == 1) {
                        binding.txtNotFound.visibility = View.VISIBLE
                    } else {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "All Order fetch",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLastItemDisplaying(binding.rvOrder) && productBoughtAdapter!!.itemCount >= 10) {
                    page++
                    orderViewModel.getProductBoughtList(
                        requireActivity(),
                        progressHUD,
                        OrderType.upcoming.name,
                        page
                    )
                }
            }
        })
        binding.tabLayoutBottom.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                page=1
                if (tab.position == 0) {
                    page = 1
                    isLastPage = false
                    productBoughtAdapter?.clearData()
                    orderViewModel.getProductBoughtList(
                        requireContext(), progressHUD,
                        OrderType.upcoming.name, page
                    )


                } else if (tab.position == 1) {
                    page = 1
                    isLastPage = false
                    productBoughtAdapter?.clearData()
                    orderViewModel.getProductBoughtList(
                        requireContext(), progressHUD,
                        OrderType.past.name, page
                    )
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })

        productBoughtAdapter!!.setItemClickListener(object :
            ProductBoughtAdapter.MyItemClickListener {
            override fun clicked(listener: View, orderResponse: OrderResponse.OrderResult) {

                when(listener.id){
                    R.id.txtTrackOrder ->{
                        val intent = Intent(requireActivity(), TrackOrderActivity::class.java)
                        intent.putExtra(ORDER, Gson().toJson(orderResponse))
                        startActivity(intent)
                    }
                    else -> {
                        val modalBottomSheet = OrderBottomSheetFragment(orderResponse)
                        modalBottomSheet.show(
                            requireActivity().supportFragmentManager,
                            OrderBottomSheetFragment.TAG
                        )
                    }
                }




            }
        })
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
        page = 1
        isLastPage = false
        productBoughtAdapter?.clearData()
        orderViewModel.getProductBoughtList(
            requireActivity(),
            progressHUD,
            OrderType.upcoming.name,
            page
        )
    }

    private fun showConfirmDeleteProduct(productId: Int) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle("DELETE PRODUCT")
        builder.setMessage("Are you sure you want to delete the selected product?")
        builder.setPositiveButton("DELETE") { _, _ ->
            //productViewModel.deleteProduct(requireActivity(),progressHUD, productId, app.currentUser.id.toString())

        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }


}