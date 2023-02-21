package com.ibcemobile.smoxstyler.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ibcemobile.smoxstyler.databinding.StylerReviewFragmentBinding

class StylerReviewFragment : Fragment() {

    private lateinit var  binding: StylerReviewFragmentBinding

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StylerReviewFragmentBinding.inflate(inflater, container, false)
        binding.imgBack.setOnClickListener { requireActivity().onBackPressed() }

        return binding.root
    }


}