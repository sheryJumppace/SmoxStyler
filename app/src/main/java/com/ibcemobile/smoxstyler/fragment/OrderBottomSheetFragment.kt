package com.ibcemobile.smoxstyler.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.OrderDetailAdapter
import com.ibcemobile.smoxstyler.databinding.OrderBottomSheetContentBinding
import com.ibcemobile.smoxstyler.model.type.OrderType
import com.ibcemobile.smoxstyler.responses.OrderResponse
import com.ibcemobile.smoxstyler.viewmodels.OrderViewModel
import com.kaopiz.kprogresshud.KProgressHUD

class OrderBottomSheetFragment(private val orderDetail: OrderResponse.OrderResult) :
    BottomSheetDialogFragment() {
    private lateinit var binding: OrderBottomSheetContentBinding
    lateinit var orderViewModel: OrderViewModel
    var progressHUD: KProgressHUD? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = OrderBottomSheetContentBinding.inflate(inflater, container, false)
        orderViewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        progressHUD = KProgressHUD(requireContext())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)


        binding.txtAddress.text = "Delivery Address\n" + orderDetail.address
        binding.txtOrderID.text = "Order#" + orderDetail.id
        binding.totalPrice.text =
            requireActivity().getString(R.string.format_price, orderDetail.subTotal!!.toDouble())
        binding.txtShippingFee.text = requireActivity().getString(
            R.string.format_price,
            orderDetail.shippingCharges!!.toDouble()
        )
        binding.txtTotalPay.text =
            requireActivity().getString(R.string.format_price, orderDetail.totalPrice!!.toDouble())
        binding.quantity.text=orderDetail.quantity.toString()
        binding.txtOrderedOn.text = orderDetail.getOrderDate()

        if (orderDetail.orderStatus == OrderType.pending.name || orderDetail.orderStatus == OrderType.placed.name) {
            binding.cancelOrder.visibility = View.VISIBLE
            binding.cancelOrder.setOnClickListener {
                showConfirmProduct(orderDetail.id.toString())
            }
        } else {
            binding.cancelOrder.visibility = View.GONE
        }

        initObserver(progressHUD!!)

        return binding.root
    }


    private fun initObserver(progressHUD: KProgressHUD) {
        orderViewModel.isCartUpdate.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                dismiss()
            }
        })

        //orderViewModel.getOrderDetail(requireContext(), progressHUD, orderDetail.id.toString())

    }

    private fun showConfirmProduct(orderId: String) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle("CANCEL ORDER")
        builder.setMessage("Are you sure you want to cancel order?")
        builder.setPositiveButton("CONFIRM") { _, _ ->
            orderViewModel.cancelOrder(requireActivity(), progressHUD!!, orderId)
        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }


    companion object {
        const val TAG = "ProductBottomSheet"
    }
}