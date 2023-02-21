package com.ibcemobile.smoxstyler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.OrderItemRowBinding
import com.ibcemobile.smoxstyler.databinding.ProductBoughtItemRowBinding
import com.ibcemobile.smoxstyler.responses.BarberResponse
import com.ibcemobile.smoxstyler.responses.OrderResponse

class ProductBoughtAdapter(var context: Context) :
    RecyclerView.Adapter<ProductBoughtAdapter.MyViewHolder>() {

    var orderList=ArrayList<OrderResponse.OrderResult>()
    var myItemClickListener:MyItemClickListener?=null

    fun setItemClickListener(clickListener: MyItemClickListener) {
        myItemClickListener = clickListener
    }

    class MyViewHolder(var binding: ProductBoughtItemRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener,orderResponse: OrderResponse.OrderResult){
            binding.apply {
                clickListener=listener
                data=orderResponse
                executePendingBindings()
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context=parent.context
       return MyViewHolder(ProductBoughtItemRowBinding.inflate(LayoutInflater.from(parent.context),parent,false))
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

        val data=orderList[position]
        holder.apply {
            bind(createListener(data),data)
            itemView.tag=data
        }

    }


    interface MyItemClickListener {
        fun clicked(listener: View,orderResponse: OrderResponse.OrderResult)
    }

    private fun createListener(orderResponse: OrderResponse.OrderResult):View.OnClickListener{
        return View.OnClickListener {
            myItemClickListener?.clicked(it,orderResponse)
        }
    }

}

