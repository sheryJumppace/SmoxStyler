package com.ibcemobile.smoxstyler.responses

import com.google.gson.annotations.SerializedName

class ReviewResponse(
    val error: Boolean,
    val result: Result
) {
    class Result(
        val total: Int,
        val clean_rating: Float,
        val work_rating: Float,
        val behave_rating: Float,
        val reviews: ArrayList<BarberReview>
    ) {
        class BarberReview(
            @SerializedName("id") val id: Int,
            @SerializedName("user_id") val user_id: Int,
            @SerializedName("barber_id") val barber_id: Int,
            @SerializedName("comment") val comment: String,
            @SerializedName("rating") val rating: Double,
            @SerializedName("created_at") val created_at: String,
            @SerializedName("appointment_id") val appointment_id: Int,
            @SerializedName("clean_rating") val clean_rating: Int,
            @SerializedName("work_rating") val work_rating: Int,
            @SerializedName("behave_rating") val behave_rating: Int,
            @SerializedName("image") val image: String,
            @SerializedName("name") val name: String
        )
    }

}

