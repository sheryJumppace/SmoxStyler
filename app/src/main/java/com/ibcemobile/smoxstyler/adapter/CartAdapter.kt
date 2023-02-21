package com.ibcemobile.smoxstyler.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.CartItemRowBinding
import com.ibcemobile.smoxstyler.responses.CartData

class CartAdapter(var context: Context) : RecyclerView.Adapter<CartAdapter.MyViewHolder>() {


    var cartList: ArrayList<CartData.CartItems> = ArrayList()
    var myItemClickListener: MyItemClickListener? = null

    fun setItemClickListener(clickListener: MyItemClickListener) {
        myItemClickListener = clickListener
    }

    class MyViewHolder(var binding: CartItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, cartData: CartData.CartItems) {
            binding.apply {
                clickListener = listener
                data = cartData
                binding.totalPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                executePendingBindings()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        return MyViewHolder(
            CartItemRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = cartList[position]
        holder.apply {
            bind(createOnClickListener(data), data)
            itemView.tag = data
        }

    }

    fun setData(list: ArrayList<CartData.CartItems>) {
        cartList.clear()
        cartList.addAll(list)
    }

    interface MyItemClickListener {
        fun clicked(view: View, cartData: CartData.CartItems)
    }


    private fun createOnClickListener(cartData: CartData.CartItems): View.OnClickListener {
        return View.OnClickListener {
            myItemClickListener?.clicked(it, cartData)
        }
    }
}

