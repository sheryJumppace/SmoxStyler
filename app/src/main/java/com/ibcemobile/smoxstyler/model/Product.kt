package com.ibcemobile.smoxstyler.model

import com.google.gson.annotations.SerializedName
import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

data class Product (
    @SerializedName("id") val id : Int,
    @SerializedName("user_id") val user_id : Int,
    @SerializedName("category_id") val category_id : Int,
    @SerializedName("category_name") val category_name : String,
    @SerializedName("product_name") val product_name : String,
    @SerializedName("main_img") val main_img : String,
    @SerializedName("stock") val stock : Int,
    @SerializedName("description") val description : String,
    @SerializedName("feature") val feature : String,
    @SerializedName("price") val price : Double,
    @SerializedName("discounted_price") val discountedPrice : Double,
    @SerializedName("shipping_price") val shipping_price : Double,
    @SerializedName("total_price") val total_price : Double,
    @SerializedName("model") val model : String,
    @SerializedName("is_active") val is_active : Int,
    @SerializedName("review_count") val review_count : Double,
    @SerializedName("review_user_count") val review_user_count : Int,
    @SerializedName("is_deleted") val is_deleted : Int,
    @SerializedName("created_at") val created_at : String,
    @SerializedName("updated_at") val updated_at : String,
    @SerializedName("deleted_at") val deleted_at : String,
    @SerializedName("stripe_public_key") val stripe_public_stripe_public_key : String,
    @SerializedName("stripe_secret_key") val stripe_secret_stripe_secret_key : String
)

