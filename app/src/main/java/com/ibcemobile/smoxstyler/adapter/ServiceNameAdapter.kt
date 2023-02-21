package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.ItemServiceNameBinding
import com.ibcemobile.smoxstyler.model.Service

class ServiceNameAdapter : ListAdapter<Service, ServiceNameAdapter.ViewHolder>(ServiceNameDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = getItem(position)
        holder.apply {
            itemView.tag = review
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemServiceNameBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }




    class ViewHolder(
        private val binding: ItemServiceNameBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind( item: Service) {
            binding.apply {
                service = item
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

private class ServiceNameDiffCallback : DiffUtil.ItemCallback<Service>() {

    override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem == newItem
    }

}

