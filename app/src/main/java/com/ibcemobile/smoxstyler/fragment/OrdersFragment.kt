package com.ibcemobile.smoxstyler.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.ibcemobile.smoxstyler.adapter.ProductSellAdapter
import com.ibcemobile.smoxstyler.adapter.ProductBoughtAdapter
import com.ibcemobile.smoxstyler.databinding.OrderFragmentBinding

class OrdersFragment : Fragment() {

    private lateinit var binding: OrderFragmentBinding

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = OrderFragmentBinding.inflate(inflater, container, false)

        binding.rvOrder.layoutManager= LinearLayoutManager(requireActivity())
        binding.rvOrder.adapter= ProductSellAdapter(requireActivity())
        binding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position==0){
                    binding.rvOrder.layoutManager= LinearLayoutManager(requireActivity())
                    binding.rvOrder.adapter= ProductSellAdapter(requireActivity())
                }else if (tab.position==1){
                    binding.rvOrder.layoutManager= LinearLayoutManager(requireActivity())
                    binding.rvOrder.adapter= ProductBoughtAdapter(requireActivity())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })

        return binding.root
    }


}