package com.ibcemobile.smoxstyler.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.FragmentBarberMainBinding


class BarberMainFragment : Fragment() {
    private lateinit var binding: FragmentBarberMainBinding
    private var navController: NavController? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBarberMainBinding.inflate(inflater, container, false)
        // val v = inflater.inflate(R.layout.fragment_home, container, false)
        val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.frameContainer) as? NavHostFragment
        navController = nestedNavHostFragment?.navController

        navController?.navigate(R.id.upNextFragment)
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        listenBackStack()
        return binding.root
    }

    private val mOnNavigationItemSelectedListener=
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.bottom_up_next -> {
                navController?.navigate(R.id.upNextFragment)

                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_calendar -> {
                navController?.navigate(R.id.calenderFragment)

                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_funds -> {
                navController?.navigate(R.id.fundsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_smox_talk -> {
                navController?.navigate(R.id.chatsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottom_smox_profile -> {
                navController?.navigate(R.id.profileFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun listenBackStack() {

        // Get NavHostFragment
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.frameContainer)

        // ChildFragmentManager of the current NavHostFragment
        val navHostChildFragmentManager = navHostFragment?.childFragmentManager

        navHostChildFragmentManager?.addOnBackStackChangedListener {
            val backStackEntryCount = navHostChildFragmentManager.backStackEntryCount
            val fragments = navHostChildFragmentManager.fragments
        }


        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backStackEntryCount = navHostChildFragmentManager!!.backStackEntryCount
                if (backStackEntryCount == 1) {
                    OnBackPressedCallback@ this.isEnabled = false
                    requireActivity().onBackPressed()
                } else {
                    navController?.navigateUp()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


}