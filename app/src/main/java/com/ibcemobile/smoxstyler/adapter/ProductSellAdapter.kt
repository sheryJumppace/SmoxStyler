package com.ibcemobile.smoxstyler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.OrderItemRowBinding
import com.ibcemobile.smoxstyler.responses.OrderResponse

class ProductSellAdapter(var context: Context) : RecyclerView.Adapter<ProductSellAdapter.MyViewHolder>() {

    var orderList = ArrayList<OrderResponse.OrderResult>()
    var myItemClickListener: MyOrderItemClickListener? = null

    fun setItemClickListener(clickListener: ProductSellAdapter.MyOrderItemClickListener) {
        myItemClickListener = clickListener
    }

    class MyViewHolder(var binding: OrderItemRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, orderData: OrderResponse.OrderResult) {
            binding.apply {
                clickListener = listener
                data = orderData
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(
            OrderItemRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    fun saveData(list:ArrayList<OrderResponse.OrderResult>){
        //orderList.clear()
        orderList.addAll(list)
        notifyDataSetChanged()
    }


    fun clearData() {
        orderList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val data = orderList[position]
        holder.apply {
            bind(createSetClickListener(data), data)
            itemView.tag = data
        }

    }

    private fun createSetClickListener(orderData: OrderResponse.OrderResult): View.OnClickListener {
        return View.OnClickListener {
            myItemClickListener?.clicked(it, orderData)
        }
    }

    interface MyOrderItemClickListener {
        fun clicked(view: View, orderData: OrderResponse.OrderResult)
    }


}

