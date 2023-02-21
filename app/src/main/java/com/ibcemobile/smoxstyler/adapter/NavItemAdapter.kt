package com.ibcemobile.smoxstyler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R

class NavItemAdapter(private val myItemClickListener: MyItemClickListener) :
    RecyclerView.Adapter<NavItemAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtHeading: TextView = itemView.findViewById(R.id.txtHeading)
        val txtSubHeading: TextView = itemView.findViewById(R.id.txtSubHeading)
        val img: ImageView = itemView.findViewById(R.id.img)
        val rlRoot: RelativeLayout = itemView.findViewById(R.id.rlRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.nav_item_row, parent, false)
        )
    }
    override fun getItemCount(): Int {
        return 3
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.img.setImageResource(R.drawable.ic_styler_icon)
                holder.txtHeading.text = "Styler"
                holder.txtSubHeading.text = "Styler profile, Edit profile……."
                holder.rlRoot.setOnClickListener { myItemClickListener.clicked(position) }
            }
            1 -> {
                holder.img.setImageResource(R.drawable.ic_product)
                holder.txtHeading.text = "Products"
                holder.txtSubHeading.text = "Browse our products here...…."
                holder.rlRoot.setOnClickListener {
                    myItemClickListener.clicked(position)
                }
            }
            2 -> {

                holder.img.setImageResource(R.mipmap.ic_order)
                holder.txtHeading.text = "Orders"
                holder.txtSubHeading.text = "Check what you have ordered"
                holder.rlRoot.setOnClickListener {
                    myItemClickListener.clicked(position)
                }
            }

            3 -> {
                holder.img.setImageResource(R.drawable.ic_cart)
                holder.txtHeading.text = "Cart"
                holder.txtSubHeading.text = "Check what you have in cart"
                holder.rlRoot.setOnClickListener { myItemClickListener.clicked(position) }

            }
            4 -> {
                holder.img.setImageResource(R.drawable.ic_aboit_us_icon)
                holder.txtHeading.text = "About us"
                holder.txtSubHeading.text = "Wanna know us ?...…."
                holder.rlRoot.setOnClickListener { myItemClickListener.clicked(position) }

            }
            5 -> {
                holder.img.setImageResource(R.drawable.ic_contact_us_icon)
                holder.txtHeading.text = "Contact us"
                holder.txtSubHeading.text = "Connect with us…"
                holder.rlRoot.setOnClickListener { myItemClickListener.clicked(position) }

            }


        }

    }


    interface MyItemClickListener {
        fun clicked(id: Int)

    }


}

