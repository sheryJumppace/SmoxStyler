package com.ibcemobile.smoxstyler.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.BuyProductItemRowBinding
import com.ibcemobile.smoxstyler.responses.ProductResponse


class ProductAdapter(var context: Context) : RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {
    private var onItemClickListener: ItemClickListener? = null
    var productResponse: ArrayList<ProductResponse.ProductItem> = ArrayList()

    fun setItemClickListener(clickListener: ProductAdapter.ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = productResponse[position]
        holder.apply {
            bind(createOnClickListener(product), product)
            itemView.tag = product
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            BuyProductItemRowBinding.inflate(
                LayoutInflater.from(context),
                parent,false
            )
        )

    }


    class MyViewHolder(
        private val binding: BuyProductItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: ProductResponse.ProductItem) {
            binding.apply {
                clickListener = listener
                txtPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                product = item
                executePendingBindings()
            }
        }
    }

    fun setData(list: ArrayList<ProductResponse.ProductItem>) {
        productResponse.clear()
        productResponse.addAll(list)
    }

    private fun createOnClickListener(cartData: ProductResponse.ProductItem): View.OnClickListener {
        return View.OnClickListener {
            onItemClickListener?.onItemClick(it, cartData)
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View, p: ProductResponse.ProductItem)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return productResponse.size
    }


}




