package com.ibcemobile.smoxstyler.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.responses.AddressResponse

class AddressAdapter(var myItemClickListener: MyItemClickListener) :
    RecyclerView.Adapter<AddressAdapter.MyViewHolder>() {
    private val addressList: ArrayList<AddressResponse.AddressData> = ArrayList()


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtAddress = itemView.findViewById<TextView>(R.id.txtAddress)!!
        val imgEdit = itemView.findViewById<ImageView>(R.id.imgEdit)!!
        val txtSelect = itemView.findViewById<RadioButton>(R.id.radioButton)!!
        val imgDelete = itemView.findViewById<ImageView>(R.id.imgDelete)!!
        val root = itemView.findViewById<RelativeLayout>(R.id.root)!!

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.address_item_row, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = addressList[position]
        holder.txtAddress.text = data.first_name + " " + data.last_name + " \n" + data.address_one + " " + data.address_two + ", " + data.city + ", " + data.zipcode

        holder.txtSelect.isChecked = data.make_default == "1"
        holder.imgEdit.setOnClickListener { myItemClickListener.editClick(data) }
        holder.imgDelete.setOnClickListener { myItemClickListener.deleteClick(data) }
        holder.imgDelete.visibility=if (data.make_default == "1") View.GONE else View.VISIBLE

        holder.root.setOnClickListener{
            myItemClickListener.selectClick(data)
        }
    }


    fun setData(list: ArrayList<AddressResponse.AddressData>) {
        addressList.clear()
        addressList.addAll(list)
    }

    interface MyItemClickListener {
        fun editClick(addressData: AddressResponse.AddressData)
        fun deleteClick(addressData: AddressResponse.AddressData)
        fun selectClick(addressData: AddressResponse.AddressData)
    }


}

