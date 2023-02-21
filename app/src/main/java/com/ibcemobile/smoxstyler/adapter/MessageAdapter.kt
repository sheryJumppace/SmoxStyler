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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ibcemobile.smoxstyler.MESSAGE_TYPE_IMAGE
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.model.Chats
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private val mContext: Context, private val mChat: ArrayList<Chats>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = if (viewType == MSG_TYPE_RIGHT) {
            LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false)
        } else {
            LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mChat[position]

        if (chat.getSeen()) {
            holder.imgSeen.setImageResource(R.drawable.ic_baseline_done_all_24)
        } else {
            holder.imgSeen.setImageResource(R.drawable.ic_baseline_gray_done_all_24)
        }
        if (chat.message_type == MESSAGE_TYPE_IMAGE) {
            holder.rl_image.visibility = View.VISIBLE
            holder.rl_message.visibility = View.GONE
            holder.time_tv1.text = holder.convertTime(chat.msg_time)
            Glide.with(mContext).load(chat.message)
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.image_upload)

            // holder.image_upload.setOnClickListener(v -> singlePhotoViewListener.openPhoto(chat.getMessage()));
        } else {
            holder.rl_image.visibility = View.GONE
            holder.rl_message.visibility = View.VISIBLE
            holder.show_message.text = chat.message
            if (chat.msg_time != null) {
                holder.time_tv.text = holder.convertTime(chat.msg_time)
            }
            if (chat.sender_profile != null) {
                Glide.with(mContext).load(chat.sender_profile)
                    .thumbnail(0.05f)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.mipmap.small_placeholder)
                    .into(holder.profile_image)
            }

        }


    }

    fun addItem(chat: Chats?) {
        val size = mChat.size
        mChat.add(chat!!)
        notifyItemInserted(size)

    }

    override fun getItemCount(): Int {
        return mChat.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mChat[position].sender_id.toString() == SessionManager(mContext).userId.toString()) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var show_message: TextView = itemView.findViewById(R.id.show_message)
        var profile_image: ImageView = itemView.findViewById(R.id.profile_image)
        var image_upload: ImageView = itemView.findViewById(R.id.image_upload)
        var time_tv: TextView = itemView.findViewById(R.id.time_tv)
        var time_tv1: TextView = itemView.findViewById(R.id.time_tv1)
        var imgSeen: ImageView = itemView.findViewById(R.id.imgSeen)
        var rl_image: RelativeLayout = itemView.findViewById(R.id.rl_image)
        var rl_message: RelativeLayout = itemView.findViewById(R.id.rl_message)
        fun convertTime(time: Long?): String {
            val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
            return formatter.format(Date(time!!))
        }

    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }
}