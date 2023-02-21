
package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.ProductItemRowBinding
import com.ibcemobile.smoxstyler.responses.ProductResponse


class ProductEditAdapter : ListAdapter<ProductResponse.ProductItem, ProductEditAdapter.ViewHolder>(ProductDiffCallback()) {
    private lateinit var context:Context
    private var onItemClickListener: ItemClickListener? = null
    private var productList=ArrayList<ProductResponse.ProductItem>()

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val product=getItem(position)
        holder.apply {
            bind(createOnClickListener(product), product)
            itemView.tag =product
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ProductItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    private fun createOnClickListener(product: ProductResponse.ProductItem): View.OnClickListener {
        return View.OnClickListener {
            onItemClickListener?.onItemClick(it, product)
         }
    }

    class ViewHolder(
        private val binding: ProductItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: ProductResponse.ProductItem) {
            binding.apply {
                clickListener = listener
                txtPrice.paintFlags= Paint.STRIKE_THRU_TEXT_FLAG
                product = item
                binding.rlProductItem.background
                executePendingBindings()
                //binding.txtDescription.text=item.productDescription.replace("\\n","\n")

            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View, product: ProductResponse.ProductItem)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    fun saveData(list:ArrayList<ProductResponse.ProductItem>){
        productList.addAll(list)
    }
}


class ProductDiffCallback : DiffUtil.ItemCallback<ProductResponse.ProductItem>() {

    override fun areItemsTheSame(oldItem: ProductResponse.ProductItem, newItem: ProductResponse.ProductItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ProductResponse.ProductItem, newItem: ProductResponse.ProductItem): Boolean {
        return oldItem == newItem
    }
}

