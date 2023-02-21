package com.ibcemobile.smoxstyler.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ibcemobile.smoxstyler.databinding.ProductsFragmentBinding
import java.util.ArrayList


class ProductsFragment : Fragment() {
    private lateinit var binding: ProductsFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = ProductsFragmentBinding.inflate(inflater, container, false)


        val mPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewpager)

        mPagerAdapter.addFrag(MyProductFragment(), "My Products")
        mPagerAdapter.addFrag(BuyProductFragment(), "Buy Products")

        binding.viewpager.adapter = mPagerAdapter

        return binding.root
    }




    class ViewPagerAdapter(manager: FragmentManager?) :
        FragmentStatePagerAdapter(manager!!) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }

        fun addFrag(fragment: Fragment) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add("")
        }

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
    }
}