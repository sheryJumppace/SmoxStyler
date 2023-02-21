package com.ibcemobile.smoxstyler.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.BuyProductItemRowBinding
import com.ibcemobile.smoxstyler.databinding.OrderDetailItemRowBinding
import com.ibcemobile.smoxstyler.responses.OrderItem
import com.ibcemobile.smoxstyler.responses.ProductResponse


class OrderDetailAdapter(var context: Context) : RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder>() {
    private var onItemClickListener: ItemClickListener? = null
    var orderItem: ArrayList<OrderItem.OrderItemRow> = ArrayList()

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = orderItem[position]
        holder.apply {
            bind(product)
            itemView.tag = product
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            OrderDetailItemRowBinding.inflate(
                LayoutInflater.from(context),
                parent,false
            )
        )

    }


    class MyViewHolder(
        private val binding: OrderDetailItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItem.OrderItemRow) {
            binding.apply {
                txtDiscPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                order = item
                executePendingBindings()
            }
        }
    }

    fun setData(list: ArrayList<OrderItem.OrderItemRow>) {
        orderItem.clear()
        orderItem.addAll(list)
    }

    interface ItemClickListener {
        fun onItemClick(view: View, p: ProductResponse.ProductItem)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return orderItem.size
    }


}




