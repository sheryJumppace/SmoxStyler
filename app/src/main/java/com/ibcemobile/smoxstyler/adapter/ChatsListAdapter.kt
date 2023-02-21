package com.ibcemobile.smoxstyler.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.database.ValueEventListener
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.model.ChatUsers
import com.ibcemobile.smoxstyler.utils.getSmsTodayYestFromMilli
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatsListAdapter(val context: Context, val itemClickListener: MyItemClickListener) :
    RecyclerView.Adapter<ChatsListAdapter.MyViewHolder>() {

    private val chatUserList: ArrayList<ChatUsers> = ArrayList()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var user_name: TextView
        var msg_count: TextView
        var last_message: TextView
        var profile: CircleImageView
        var msg_time: TextView
        var rl_root: RelativeLayout

        fun convertTime(time: Long?): String {
            val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
            return formatter.format(Date(time!!))
        }

        init {
            msg_count = itemView.findViewById(R.id.txtMessageCount)
            last_message = itemView.findViewById(R.id.txtLastMessage)
            profile = itemView.findViewById(R.id.img)
            user_name = itemView.findViewById(R.id.txtName)
            msg_time = itemView.findViewById(R.id.txtTime)
            rl_root = itemView.findViewById(R.id.rl_root)

        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.smox_talk_item_row, parent, false)
        )
    }


    override fun getItemCount(): Int {
        return chatUserList.size
    }

    fun setData(list:ArrayList<ChatUsers>){
        chatUserList.clear()
        chatUserList.addAll(list)
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val users = chatUserList[position]

        holder.user_name.text = users.user_name

        if (users.last_message!!.contains("http")){
            holder.last_message.text=context.getString(R.string.txt_photo)
            holder.last_message.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_baseline_image_15), null, null, null)

        }else{
            holder.last_message.text = users.last_message
        }
        if (users.message_count!!.toInt()>0){
            if (users.sender_id!=App.instance.currentUser.id.toString()){
                holder.msg_count.visibility=View.VISIBLE
                holder.msg_count.text = users.message_count
            }else{
                holder.msg_count.visibility=View.GONE
            }
        }else{
            holder.msg_count.visibility=View.GONE
        }
        holder.msg_time.text = getSmsTodayYestFromMilli(users.last_message_time!!.toLong())
        Glide.with(context).load(users.receiver_profile)
            .thumbnail(0.05f)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.small_placeholder)
            .into(holder.profile)

        holder.rl_root.setOnClickListener {
            itemClickListener.clicked(users)
        }

    }


    interface MyItemClickListener {
        fun clicked(users: ChatUsers)
    }


}

