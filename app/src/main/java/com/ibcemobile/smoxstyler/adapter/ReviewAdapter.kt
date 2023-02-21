package com.ibcemobile.smoxstyler.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.databinding.RatingItemRowBinding
import com.ibcemobile.smoxstyler.model.Review


class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ViewHolder>(ReviewDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = getItem(position)
        holder.apply {
            bind(createOnClickListener(review), review)
            itemView.tag = review
        }

        holder.txtRating.text= review.rating.toString().substring(0,3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RatingItemRowBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    private fun createOnClickListener(review: Review): View.OnClickListener {
        return View.OnClickListener {
            review.isMore.set(true)
        }
    }

    class ViewHolder(
        private val binding: RatingItemRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val txtRating :TextView=binding.txtRating
        fun bind(listener: View.OnClickListener, item: Review) {
            binding.apply {
                clickListener = listener
                review = item
                executePendingBindings()
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }
}

private class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {

    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem == newItem
    }
}

