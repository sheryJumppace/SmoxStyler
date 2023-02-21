package com.ibcemobile.smoxstyler.adapter

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BindingAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.manager.Constants

@BindingAdapter("app:imageUrl", "app:placeHolder", "app:isCircle")
fun loadCircleImage(view: ImageView, imageUrl: String?, placeHolder: Drawable?, isCircle:Boolean = false) {
    if(imageUrl == null) return
    val url = Constants.downloadUrl(imageUrl)
    var requestOptions:RequestOptions = RequestOptions().centerCrop()
    requestOptions = if(isCircle){
        requestOptions.circleCrop()
    }else{
        requestOptions.transform(RoundedCorners(7))
    }
    requestOptions = if(placeHolder != null){
        requestOptions.placeholder(placeHolder)
    }else{
        requestOptions.placeholder(R.drawable.small_placeholder)

    }
    Glide.with(view.context)
        .load(url)
        .apply(requestOptions)
        .into(view)
}

@BindingAdapter("app:productUrl")
fun loadProductImage(view: ImageView, imageUrl: String?) {
    if(imageUrl == null) return
    val url = Constants.downloadUrlOfProduct(imageUrl)
    val requestOptions:RequestOptions = RequestOptions().fitCenter()
        .transform(RoundedCorners(5))
        .placeholder(R.drawable.small_placeholder)

    Glide.with(view.context)
        .load(url)
        .apply(requestOptions)
        .into(view)
}

@BindingAdapter("app:productListUrl")
fun loadProductListImage(view: ImageView, imageUrl: String?) {
    if(imageUrl == null) return
    val url = Constants.downloadUrlOfProduct(imageUrl)
    val requestOptions:RequestOptions = RequestOptions()
        .transform(RoundedCorners(5))
        .placeholder(R.mipmap.small_placeholder)
    Glide.with(view.context)
        .load(url)
        .apply(requestOptions)
        .into(view)
}

@BindingAdapter("app:isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}
@BindingAdapter("android:src")
fun resource(view: ImageView, resource: Int) {
    view.setImageResource(resource)
}


//@BindingAdapter("layoutHeight")
//fun setLayoutHeight(view: View, height: Int) {
//    val layoutParams = view.layoutParams
//    layoutParams.height = height
//    view.layoutParams = layoutParams
//}
//
//@BindingAdapter("layoutWidth")
//fun setLayoutWidth(view: View, width: Int) {
//    val layoutParams = view.layoutParams
//    layoutParams.width = width
//    view.layoutParams = layoutParams
//}