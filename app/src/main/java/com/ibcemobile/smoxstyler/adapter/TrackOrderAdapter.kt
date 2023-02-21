package com.ibcemobile.smoxstyler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.model.TrackModel

class TrackOrderAdapter(var context: Context) : RecyclerView.Adapter<TrackOrderAdapter.MyViewHolder>() {

    var list=ArrayList<TrackModel>()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackTitle: TextView = itemView.findViewById(R.id.trackTitle)
        val trackMessage: TextView = itemView.findViewById(R.id.trackMessage)
        val img: ImageView = itemView.findViewById(R.id.image)
        val trackDate: TextView = itemView.findViewById(R.id.trackDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.order_track_item_row, parent, false)
        )
    }
    override fun getItemCount(): Int {
        return list.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Glide.with(context).load(list[position].image).into(holder.img)
        holder.trackTitle.text=list[position].trackTitle
        holder.trackMessage.text=list[position].trackMessage
        holder.trackDate.text=list[position].trackDate

    }

    fun saveData(arr: ArrayList<TrackModel>){
        list.clear()
        list.addAll(arr)
        notifyDataSetChanged()
    }



}

