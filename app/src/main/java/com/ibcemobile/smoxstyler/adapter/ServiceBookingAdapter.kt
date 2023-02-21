package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.ServiceItemRowBinding
import com.ibcemobile.smoxstyler.model.Service


class ServiceBookingAdapter(private val isHide:Boolean) : ListAdapter<Service, ServiceBookingAdapter.ViewHolder>(ServiceBookingDiffCallback()) {
    private lateinit var context:Context
    private var onItemClickListener: ItemClickListener? = null


    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = getItem(position)

        holder.apply {
            bind(createOnClickListener(position), service)
            itemView.tag = service
        }
        if (isHide){
            holder.radioB.visibility=View.GONE
        }else{
            holder.radioB.visibility=View.VISIBLE
        }






    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ServiceItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    private fun createOnClickListener(position: Int): View.OnClickListener {
        return View.OnClickListener {
            try {
                getItem(position).isSelected.set(!getItem(position).isSelected.get())

                onItemClickListener?.onServiceItemClick(it, position, getItem(position))


            } catch (ex : Exception) {
                ex.printStackTrace()
            }
        }
    }

    class ViewHolder(
        private val binding: ServiceItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val radioB:RadioButton=binding.radioButton
        fun bind(listener: View.OnClickListener, item: Service) {
            binding.apply {
                clickListener = listener
                service = item
                executePendingBindings()
            }
        }
    }

    interface ItemClickListener {
        fun onServiceItemClick(view: View, position: Int, service: Service)
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }
}

private class ServiceBookingDiffCallback : DiffUtil.ItemCallback<Service>() {

    override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
        return oldItem == newItem
    }
}

