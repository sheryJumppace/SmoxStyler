package com.ibcemobile.smoxstyler.responses

import com.google.gson.annotations.SerializedName

data class BarberResponse(
    @SerializedName("error") var error: Boolean,
    @SerializedName("result") var result: ArrayList<BarberData>

) {
    class BarberData(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("email") var email: String = "",
        @SerializedName("phone") var phone: String = "",
        @SerializedName("first_name") var firstName: String = "",
        @SerializedName("last_name") var lastName: String = "",
        @SerializedName("image") var image: String = "",
        @SerializedName("business_name") var business_name: String = "",
        @SerializedName("distance") var distance: String = "",
        @SerializedName("total_products") var totalProducts: Int = 0,
        )

}