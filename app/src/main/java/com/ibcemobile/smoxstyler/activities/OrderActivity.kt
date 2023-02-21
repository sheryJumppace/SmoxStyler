package com.ibcemobile.smoxstyler.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ibcemobile.smoxstyler.databinding.ActivityOrderBinding
import com.ibcemobile.smoxstyler.fragment.CalendarEventsFragment
import com.ibcemobile.smoxstyler.fragment.CalenderFragment
import com.ibcemobile.smoxstyler.fragment.ProductBoughtFragment
import com.ibcemobile.smoxstyler.fragment.ProductSellFragment

class OrderActivity : BaseActivity() {
    lateinit var binding: ActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        val mPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        binding.tabLayoutTop.setupWithViewPager(binding.viewpager)

        mPagerAdapter.addFrag(ProductSellFragment(), "Products Sold")
        mPagerAdapter.addFrag(ProductBoughtFragment(), "Products Purchased")

        binding.viewpager.adapter = mPagerAdapter

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