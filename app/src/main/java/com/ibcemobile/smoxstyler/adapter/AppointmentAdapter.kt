package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.CalenderAppointmentItemRowBinding
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.type.AppointmentType


class AppointmentAdapter :
    ListAdapter<Appointment, AppointmentAdapter.ViewHolder>(AppointmentDiffCallback()) {
    var isEditable = false
    var context: Context? = null

    interface ItemClickListener {
        fun onItemClick(view: View, appointment: Appointment, position: Int)
    }

    private var onItemClickListener: ItemClickListener? = null

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = getItem(position)

        val isCanDelete =
            isEditable && appointment.customerId == 0 && appointment.status == AppointmentType.Approved

        holder.txtEventsName.text = appointment.user.firstName
        holder.txtTime.text = appointment.getSlots()

        holder.apply {
            bind(createOnClickListener(appointment, position), appointment, isCanDelete)
            itemView.tag = appointment
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            CalenderAppointmentItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private fun createOnClickListener(appointment: Appointment, pos: Int): View.OnClickListener {
        return View.OnClickListener {
            onItemClickListener?.onItemClick(it, appointment, pos)
        }
    }

    class ViewHolder(
        private val binding: CalenderAppointmentItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        var txtEventsName: TextView = binding.txtEventsName
        var txtTime: TextView = binding.txtTime
        fun bind(listener: View.OnClickListener, item: Appointment, canDelete: Boolean) {
            binding.apply {
                clickListener = listener
                isCanDelete = canDelete
                appointment = item
                executePendingBindings()
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}


private class AppointmentDiffCallback : DiffUtil.ItemCallback<Appointment>() {

    override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
        return oldItem == newItem
    }
}