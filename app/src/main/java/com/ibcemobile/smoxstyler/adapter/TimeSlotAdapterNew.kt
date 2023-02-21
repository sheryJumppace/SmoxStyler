package com.ibcemobile.smoxstyler.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ItemRowTimeSlotsBinding
import com.ibcemobile.smoxstyler.model.TimeSlotResult


class TimeSlotAdapterNew :
    ListAdapter<TimeSlotResult, TimeSlotAdapterNew.ViewHolder>(TimeSlotDiffCallbackk()) {
    private lateinit var context: Context
    private var selectedSlotCountFixed: Int = 0
    private var selectedSlotCountActual: Int = 0
    private var timeSlotList = arrayListOf<TimeSlotResult>()

    interface ItemClickListener {
        fun onTimeClick(slot: TimeSlotResult)
    }

    private var onItemClickListener: ItemClickListener? = null
    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun submitList(list: List<TimeSlotResult?>?) {
        super.submitList(list?.let { ArrayList(it) })
        timeSlotList = list as ArrayList<TimeSlotResult>
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = getItem(position)
        holder.apply {
            bind(slot)
            itemView.tag = slot
        }

        if (slot.isSelected) {
            holder.txtSlot.setBackgroundResource(R.drawable.bg_slot_selected)
            onItemClickListener?.onTimeClick(slot)
        } else {
            if (slot.status == 2)
                holder.txtSlot.setBackgroundResource(R.drawable.bg_slot_un_selected_pink)
            else
                holder.txtSlot.setBackgroundResource(R.drawable.bg_slot_un_select)
        }

        if (slot.status == 0) {
            holder.txtSlot.setBackgroundResource(R.drawable.bg_slot_unavailable)
            holder.itemView.isEnabled = false
        }

        holder.itemView.setOnClickListener {
            if (selectedSlotCountFixed > 0) {
                Log.e(
                    "TAG",
                    "onBindViewHolder: actual: $selectedSlotCountActual  fixed: $selectedSlotCountFixed"
                )

                val endPos: Int = position + selectedSlotCountFixed
                if (endPos <= timeSlotList.size) {
                    var i = 0
                    var booked: Boolean
                    while (i < timeSlotList.size) {

                        if (i == position) {
                            booked = false
                            for (k in position until endPos) {
                                if (timeSlotList[k].status == 1||timeSlotList[k].status == 2) {
                                    timeSlotList[k].isSelected = true
                                } else {
                                    booked = true
                                }
                                i++
                            }
                            if (booked) {
                                if (selectedSlotCountFixed > 1)
                                    Toast.makeText(
                                        context,
                                        "Middle slot is already booked, please choose different time slot.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                else
                                    Toast.makeText(
                                        context,
                                        "Slot is already booked, please choose different time slot.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                for (k in position until endPos) {
                                    timeSlotList[k].isSelected = false
                                }
                                refreshTimeslot()
                            }
                            continue
                        }
                        timeSlotList[i].isSelected = false
                        i++
                    }
                    notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        context,
                        "Time exceeds, please choose different time slot.",
                        Toast.LENGTH_SHORT
                    ).show()
                    refreshTimeslot()


                }
            } else
                Toast.makeText(context, "Please select services first.", Toast.LENGTH_SHORT).show()
        }
        holder.itemView.isEnabled = slot.timeslot != "Closed"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            ItemRowTimeSlotsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun canSelectSlotCount(canSelectSlotCount: Int) {
        selectedSlotCountFixed = canSelectSlotCount
        refreshTimeslot()
    }

    private fun refreshTimeslot() {
        for (item in timeSlotList) {
            item.isSelected = false
        }
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemRowTimeSlotsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val txtSlot = binding.txtTime
        fun bind(item: TimeSlotResult) {
            binding.apply {
                time = item.timeslot
                executePendingBindings()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}

private class TimeSlotDiffCallbackk : DiffUtil.ItemCallback<TimeSlotResult>() {

    override fun areItemsTheSame(oldItem: TimeSlotResult, newItem: TimeSlotResult): Boolean {
        return oldItem.timeslot == newItem.timeslot
    }

    override fun areContentsTheSame(oldItem: TimeSlotResult, newItem: TimeSlotResult): Boolean {
        return oldItem == newItem
    }

}
