package com.ibcemobile.smoxstyler.model

import com.google.gson.annotations.SerializedName


data class ProductCategory(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: List<CategoryList>
) {
    class CategoryList(
        @SerializedName("id") val id: Int,
        @SerializedName("category_name") val category_name: String,
        @SerializedName("created_at") val created_at: String,
        @SerializedName("updated_at") val updated_at: String
    ) {
        override fun toString(): String {
            return category_name
        }
    }
}


