
package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.ServiceItemRowEditBinding
import com.ibcemobile.smoxstyler.model.Service


class ServiceAdapter(
    private val isSelect: Boolean = false,
) : ListAdapter<Service, ServiceAdapter.ViewHolder>(ServiceDiffCallback()) {
    private lateinit var context: Context
    private var onItemClickListener: ItemClickListener? = null

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = getItem(position)
        holder.apply {
            bind(createOnClickListener(service,position), service)
            itemView.tag = service
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ServiceItemRowEditBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), isSelect
        )
    }

    private fun createOnClickListener(
        service: Service,
        position: Int
    ): View.OnClickListener {
        return View.OnClickListener {
            if (isSelect) {
                service.isSelected.set(!service.isSelected.get())
            } else {
                onItemClickListener?.onItemClick(it, service,position)
            }
        }
    }

    class ViewHolder(
        private val binding: ServiceItemRowEditBinding,
        private val small: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: Service) {
            binding.apply {
                clickListener = listener
                service = item
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface ItemClickListener {
        fun onItemClick(view: View, serviceId: Service, position: Int)
    }
}

private class ServiceDiffCallback : DiffUtil.ItemCallback<Service>() {

    override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem == newItem
    }
}

