package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.ListItemUpnextOptionBinding
import com.ibcemobile.smoxstyler.model.UpNextOption


class UpNextOptionAdapter :
    ListAdapter<UpNextOption, UpNextOptionAdapter.ViewHolder>(UpNextOptionDiffCallback()) {
    private lateinit var context: Context
    private var onItemClickListener: ItemClickListener? = null

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = getItem(position)
        holder.apply {
            bind(createOnClickListener(position), option)
            itemView.tag = option
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ListItemUpnextOptionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private fun createOnClickListener(position: Int): View.OnClickListener {
        return View.OnClickListener {
            onItemClickListener?.onItemClick(it, position)
        }
    }

    class ViewHolder(
        private val binding: ListItemUpnextOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(listener: View.OnClickListener, item: UpNextOption) {
            binding.apply {
                clickListener = listener
                upNext = item
                executePendingBindings()
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}

private class UpNextOptionDiffCallback : DiffUtil.ItemCallback<UpNextOption>() {

    override fun areItemsTheSame(oldItem: UpNextOption, newItem: UpNextOption): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: UpNextOption, newItem: UpNextOption): Boolean {
        return oldItem == newItem
    }
}

