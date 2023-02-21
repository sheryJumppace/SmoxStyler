package com.ibcemobile.smoxstyler.fragment

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
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.ProductSellAdapter
import com.ibcemobile.smoxstyler.databinding.ProductSellFragmentBinding
import com.ibcemobile.smoxstyler.model.type.OrderType
import com.ibcemobile.smoxstyler.responses.OrderResponse
import com.ibcemobile.smoxstyler.viewmodels.OrderViewModel


class ProductSellFragment : BaseFragment() {
    private lateinit var binding: ProductSellFragmentBinding
    lateinit var orderViewModel: OrderViewModel
    private var productSellAdapter: ProductSellAdapter? = null
    private var page: Int = 1
    private var isLastPage = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ProductSellFragmentBinding.inflate(inflater, container, false)

        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        binding.rvOrder.layoutManager = LinearLayoutManager(requireActivity())
        productSellAdapter = ProductSellAdapter(requireActivity())
        binding.rvOrder.adapter=productSellAdapter

        orderViewModel.orderResponse.observe(viewLifecycleOwner, Observer {

            if (it != null) {
                if (!it.isNullOrEmpty()) {
                    productSellAdapter?.saveData(it)
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

      /*  orderViewModel.getProductSellList(
            requireActivity(),
            progressHUD,
            OrderType.upcoming.name,
            page
        )*/

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLastItemDisplaying(binding.rvOrder) && productSellAdapter!!.itemCount >= 10) {
                    page++
                    orderViewModel.getProductSellList(
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
                page = 1
                if (tab.position == 0) {
                    page = 1
                    isLastPage = false
                    productSellAdapter?.clearData()
                    orderViewModel.getProductSellList(
                        requireContext(), progressHUD,
                        OrderType.upcoming.name, page
                    )


                } else if (tab.position == 1) {
                    page = 1
                    isLastPage = false
                    productSellAdapter?.clearData()
                    orderViewModel.getProductSellList(
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

        productSellAdapter!!.setItemClickListener(object :ProductSellAdapter.MyOrderItemClickListener{
            override fun clicked(view: View, orderData: OrderResponse.OrderResult) {
                val modalBottomSheet = OrderBottomSheetFragment(orderData)
                modalBottomSheet.show(requireActivity().supportFragmentManager, OrderBottomSheetFragment.TAG)
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
        productSellAdapter?.clearData()
        orderViewModel.getProductSellList(
            requireActivity(),
            progressHUD,
            OrderType.upcoming.name,
            page
        )

    }




}