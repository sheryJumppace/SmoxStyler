package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.OnDaysItemRowBinding
import com.ibcemobile.smoxstyler.model.OpenDay
import com.suke.widget.SwitchButton


class OpenHourAdapter : ListAdapter<OpenDay, OpenHourAdapter.ViewHolder>(OpenHourDiffCallback()) {
    private lateinit var context:Context
    private var onItemClickListener: ItemClickListener? = null

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = getItem(position)
        holder.apply {
            bind(createOnClickListener(position),
                createOnChangeListener(position),
                contact)
            itemView.tag = contact
        }

        holder.txtST.setOnClickListener {
            onItemClickListener?.onItemClick(it, position, true)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            OnDaysItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))



    }

    private fun createOnClickListener(position: Int): View.OnClickListener {
        return View.OnClickListener {
            onItemClickListener?.onItemClick(it, position, false)
        }
    }

    private fun createOnChangeListener(position: Int): SwitchButton.OnCheckedChangeListener {
        return SwitchButton.OnCheckedChangeListener{ view, isChecked->
            onItemClickListener?.onDayChange(view, position, isChecked)
        }
    }
    class ViewHolder(
        private val binding: OnDaysItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val txtST=binding.txtST
        fun bind(listener: View.OnClickListener, switchListener: SwitchButton.OnCheckedChangeListener, openHour: OpenDay) {
            binding.apply {
                clickListener = listener
                switchButton.setOnCheckedChangeListener(switchListener)
                item = openHour
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int, isStart:Boolean)
        fun onDayChange(view: View, position: Int, isOpen:Boolean)
    }
}

private class OpenHourDiffCallback : DiffUtil.ItemCallback<OpenDay>() {

    override fun areItemsTheSame(oldItem: OpenDay, newItem: OpenDay): Boolean {
        return oldItem.day == newItem.day
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: OpenDay, newItem: OpenDay): Boolean {
        return oldItem == newItem
    }
}

