package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.BarberProductItemRowBinding
import com.ibcemobile.smoxstyler.databinding.CartByBarberItemRowBinding
import com.ibcemobile.smoxstyler.responses.BarberResponse
import com.ibcemobile.smoxstyler.responses.CartBarberItem


class CartByBarberAdapter :
    ListAdapter<CartBarberItem, CartByBarberAdapter.ViewHolder>(BarberBYCartDiffCallback()) {
    private lateinit var context: Context
    private var onItemClickListener: ItemClickListener? = null


    fun setItemClickListener(clickListener: CartByBarberAdapter.ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        holder.apply {
            bind(product)
            itemView.tag = product
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(product)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            CartByBarberItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    class ViewHolder(
        private val binding: CartByBarberItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val img = binding.image
        fun bind(item: CartBarberItem) {
            binding.apply {
                product = item
                executePendingBindings()
            }

        }
    }

    interface ItemClickListener {
        fun onItemClick(product: CartBarberItem)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

class BarberBYCartDiffCallback : DiffUtil.ItemCallback<CartBarberItem>() {

    override fun areItemsTheSame(oldItem: CartBarberItem, newItem: CartBarberItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: CartBarberItem, newItem: CartBarberItem): Boolean {
        return oldItem == newItem
    }
}

