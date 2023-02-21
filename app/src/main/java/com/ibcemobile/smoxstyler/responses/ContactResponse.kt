package com.ibcemobile.smoxstyler.responses

import com.google.gson.annotations.SerializedName

data class ContactResponse(

    @SerializedName("error") val error: Boolean?=null,
    @SerializedName("result") val result: Result?=null
) {
    class Result(
        @SerializedName("user") val contacts: ContactUser?=null
    ) {
        class ContactUser(
            @SerializedName("id") val id: String?=null,
            @SerializedName("phone") val phone: String?=null,
            @SerializedName("first_name") val first_name: String?=null,
            @SerializedName("last_name") val last_name: String?=null,
            @SerializedName("image") val image: String?=null,
            @SerializedName("email") val email: String?=null,
            )

    }
}

