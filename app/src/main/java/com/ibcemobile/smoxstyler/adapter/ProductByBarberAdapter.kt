package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.BarberProductItemRowBinding
import com.ibcemobile.smoxstyler.responses.BarberResponse
import com.ibcemobile.smoxstyler.responses.ProductResponse


class ProductByBarberAdapter :
    ListAdapter<BarberResponse.BarberData, ProductByBarberAdapter.ViewHolder>(BarberDiffCallback()) {
    private lateinit var context: Context
    private var onItemClickListener: ItemClickListener? = null
    private var productList=ArrayList<BarberResponse.BarberData>()


    fun setItemClickListener(clickListener: ProductByBarberAdapter.ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //val product = getItem(position)
        holder.apply {
            bind(productList[position])
            itemView.tag = productList[position]
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(productList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            BarberProductItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    class ViewHolder(
        private val binding: BarberProductItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BarberResponse.BarberData) {
            binding.apply {
                product = item
                executePendingBindings()
            }

        }
    }

    interface ItemClickListener {
        fun onItemClick(product: BarberResponse.BarberData)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun saveData(list:ArrayList<BarberResponse.BarberData>){
        productList.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        productList.clear()
        notifyDataSetChanged()
    }
}

class BarberDiffCallback : DiffUtil.ItemCallback<BarberResponse.BarberData>() {

    override fun areItemsTheSame(oldItem: BarberResponse.BarberData, newItem: BarberResponse.BarberData): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: BarberResponse.BarberData, newItem: BarberResponse.BarberData): Boolean {
        return oldItem == newItem
    }
}

