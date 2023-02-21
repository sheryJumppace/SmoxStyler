package com.ibcemobile.smoxstyler.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.model.Holiday

class HourOperationOffDaysAdapter( private val onItemClickListener: MyItemClickListener?) :
    RecyclerView.Adapter<HourOperationOffDaysAdapter.MyViewHolder>() {

    var items: ArrayList<Holiday> = ArrayList()



    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle = itemView.findViewById<TextView>(R.id.txtTitle)
        val txtDate = itemView.findViewById<TextView>(R.id.txtDate)
        val imgTrash = itemView.findViewById<ImageView>(R.id.imgTrash)
        val imgEdit = itemView.findViewById<ImageView>(R.id.imgEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.off_days_item_row, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.txtTitle.text = items[position].title
        holder.txtDate.text = items[position].day+" , "+items[position].date

        holder.imgEdit.setOnClickListener {
            onItemClickListener?.edit(items[position])
        }
        holder.imgTrash.setOnClickListener {
            onItemClickListener?.delete(items[position])
        }
    }

    fun saveData(holidays: ArrayList<Holiday>) {
        items.clear()
        items.addAll(holidays)
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }


    interface MyItemClickListener {
        fun edit(holiday: Holiday)
        fun delete(holiday: Holiday)
    }


}

