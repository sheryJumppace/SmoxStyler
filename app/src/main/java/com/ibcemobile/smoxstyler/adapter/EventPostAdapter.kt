package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.CalenederEventsItemRowBinding
import com.ibcemobile.smoxstyler.model.Event


class EventPostAdapter internal constructor(eventAction: EventActions): ListAdapter<Event, EventPostAdapter.ViewHolder>(EventPostDiffCallback()) {
    var eventAction: EventActions = eventAction

    interface ItemClickListener {
        fun onItemClick(view: View, appointmentId: Int)
    }
    private var onItemClickListener: ItemClickListener? = null

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.apply {
            bind(createOnClickListener(item.id), item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CalenederEventsItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false),eventAction)
    }

    private fun createOnClickListener(appointmentId: Int): View.OnClickListener {
        return View.OnClickListener {
            onItemClickListener?.onItemClick(it, appointmentId)
        }
    }

    class ViewHolder(
        private val binding: CalenederEventsItemRowBinding
   ,eventAction: EventActions
    ) : RecyclerView.ViewHolder(binding.root) {
        var eventAction: EventActions = eventAction
        fun bind(listener: View.OnClickListener, item: Event) {
            binding.apply {
                event = item
                executePendingBindings()
            }
            binding.imgTrash.setOnClickListener{

                eventAction.onDeleteClick(adapterPosition)

            }
            binding.imgEdit.setOnClickListener {
                eventAction.onEditClick(event = item ,pos =adapterPosition )

            }
        }

    }


    interface EventActions {
        fun onDeleteClick(pos:Int)
        fun onEditClick(event: Event , pos:Int)

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}

private class EventPostDiffCallback : DiffUtil.ItemCallback<Event>() {

    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}

