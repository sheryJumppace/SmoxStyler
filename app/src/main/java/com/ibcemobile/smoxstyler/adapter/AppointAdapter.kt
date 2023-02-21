package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.AppointmentItemRowBinding
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.type.AppointmentType


class AppointAdapter : ListAdapter<Appointment, AppointAdapter.ViewHolder>(AppointDiffCallback()) {
    var context: Context? = null
    val adapter = ServiceNameAdapter()

    interface ItemClickListener {
        fun onItemClick(view: View, appointment: Appointment, position: Int)
    }

    private var onItemClickListener: ItemClickListener? = null

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = getItem(position)
        holder.txtEventsName.text = appointment.username
        var service = ""
        for (i in 0 until appointment.services.size) {
            service += appointment.services[i].title + ", "
        }

        holder.apply {
            bind(createOnClickListener(appointment, position), appointment, false)
            itemView.tag = appointment
        }

        holder.txtTime.text = appointment.getSlots()

        when (appointment.status) {
            AppointmentType.Completed -> {
                holder.txtOpt.text = context?.getString(R.string.service_done)
                holder.llApprove.visibility = View.GONE
                holder.txtTime.setCompoundDrawablesWithIntrinsicBounds(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_clock_light
                    )
                }, null, null, null)
            }
            AppointmentType.Pending -> {
                holder.txtTime.setCompoundDrawablesWithIntrinsicBounds(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_clock
                    )
                }, null, null, null)
                holder.llApprove.visibility = View.VISIBLE
                holder.txtComplete.text=context!!.getString(R.string.txt_approved)
            }
            else -> {
                holder.txtTime.setCompoundDrawablesWithIntrinsicBounds(context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_clock
                    )
                }, null, null, null)
                holder.llApprove.visibility = View.VISIBLE

            }
        }
        if (appointment.isPaid) {
            holder.txtComplete.visibility = View.VISIBLE
            holder.txtPrice.setTextColor(ContextCompat.getColor(context!!, R.color.green))
        } else {
            holder.txtComplete.visibility = View.VISIBLE
            holder.txtPrice.setTextColor(ContextCompat.getColor(context!!, R.color.quantum_googred))
        }
        holder.itemView.setOnClickListener{
            onItemClickListener!!.onItemClick(it,appointment,position)
        }

        if (service.endsWith(", ")) {
            service = service.substring(0, service.length - 2)
        }
        holder.txtServiceName.text = service
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            AppointmentItemRowBinding.inflate(
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
        private val binding: AppointmentItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        var txtEventsName: TextView = binding.txtEventsName
        var txtComplete: TextView = binding.txtComplete
        var llApprove: LinearLayout = binding.declineLayout
        var txtServiceName: TextView = binding.txtServiceName
        var txtTime: TextView = binding.txtTime
        var txtOpt: TextView = binding.txtOpt
        var txtPrice: TextView = binding.txtPrice
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



private class AppointDiffCallback : DiffUtil.ItemCallback<Appointment>() {

    override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
        return oldItem == newItem
    }
}

