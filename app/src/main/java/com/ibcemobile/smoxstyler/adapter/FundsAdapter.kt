package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.FundsItemRowBinding
import com.ibcemobile.smoxstyler.model.Funds
import de.hdodenhof.circleimageview.CircleImageView

class FundsAdapter : ListAdapter<Funds, FundsAdapter.ViewHolder>(FundsPostDiffCallback()) {


    interface ItemClickListener {
        fun onItemClick(view: View, slot: Funds)
    }
    private var onItemClickListener: ItemClickListener? = null

    fun setItemClickListener(clickListener: ItemClickListener) {
        onItemClickListener = clickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val slot = getItem(position)
        holder.txtPrice.text="$"+slot.price+".00"

        if (slot.is_transfer == "1"){
            holder.txtPrice.setTextColor(Color.parseColor("#00C70F"))
            holder.txtStatus.text="In bank account"
        }else{
            holder.txtPrice.setTextColor(Color.parseColor("#FF5252"))
            holder.txtStatus.text="Under Process"
        }

        if (slot.refund_id.isNotEmpty()){
            holder.txtPrice.setTextColor(Color.parseColor("#fc8c03"))
            holder.txtStatus.setTextColor(Color.parseColor("#fc8c03"))
            holder.txtStatus.text="Refunded"
        }
        holder.apply {
            bind(createOnClickListener(slot, position),slot)
            itemView.tag = slot
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FundsItemRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private fun createOnClickListener(slot: Funds, position: Int): View.OnClickListener {
        return View.OnClickListener {
            onItemClickListener?.onItemClick(it, slot)
            notifyDataSetChanged()
        }
    }


    class ViewHolder(
        private val binding: FundsItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val txtPrice: TextView=binding.txtPrice
        val txtStatus: TextView=binding.txtStatus
        val txtName: TextView=binding.txtName
        val txtDate: TextView=binding.txtDate
        val Image: CircleImageView=binding.imgCustomer
           fun bind(listener: View.OnClickListener, item: Funds) {
               binding.apply {
                   clickListener = listener
                   fund = item
                   executePendingBindings()
               }
           }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}



private class FundsPostDiffCallback : DiffUtil.ItemCallback<Funds>() {

    override fun areItemsTheSame(oldItem: Funds, newItem: Funds): Boolean {
        return oldItem == newItem
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Funds, newItem: Funds): Boolean {
        return oldItem == newItem
    }
}


