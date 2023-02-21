package com.ibcemobile.smoxstyler.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.databinding.ListItemCategoriesBinding
import com.ibcemobile.smoxstyler.model.Category


class CategorySelectorAdapter internal constructor(
    var context: Context, private var categoryActions: CategorySelectActions,
    private val isLower: Boolean = true
) :
    RecyclerView.Adapter<CategorySelectorAdapter.ViewHolder>() {

    var isSelected=0
    var categoryList = arrayListOf<Category>()


    class ViewHolder(
        val binding: ListItemCategoriesBinding,
        private var categoryList: ArrayList<Category>,
        private val lower: Boolean
    ) : RecyclerView.ViewHolder(binding.root){
        fun setDataToView(position: Int) {
          //  binding.root.setOnClickListener(this)
            binding.txtName.text = categoryList.get(position).cat_name
            binding.txtName.isAllCaps = lower
        }

      /*  override fun onClick(v: View?) {
            if (v!!.id == binding.root.id) {

                categoryActions.onItemClick(adapterPosition)

            }
        }*/
    }

    fun doRefresh(categoryList: ArrayList<Category>) {
        this.categoryList = categoryList
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context);
        val binding = ListItemCategoriesBinding.inflate(inflater)
        return ViewHolder(binding, categoryList, isLower)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setDataToView(position)
        if (isSelected==position) {
            holder.binding.txtName.setBackgroundResource(R.drawable.rect_yellow_bg)
        }else{
            holder.binding.txtName.setBackgroundResource(R.drawable.rect_border)
        }
        holder.binding.txtName.setOnClickListener{
        categoryActions.onItemClick(position)
            isSelected=position
            notifyDataSetChanged()

        }



    }

    public interface CategorySelectActions {
        fun onItemClick(pos: Int)
    }
}