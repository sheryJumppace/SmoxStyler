package com.ibcemobile.smoxstyler.responses

import com.google.gson.annotations.SerializedName

class AddressResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<AddressData>
){
    class AddressData(
        @SerializedName("id") val id : String="",
        @SerializedName("user_id") val user_id : String="",
        @SerializedName("first_name") val first_name : String="",
        @SerializedName("last_name") val last_name : String="",
        @SerializedName("address_one") val address_one : String="",
        @SerializedName("address_two") val address_two : String="",
        @SerializedName("city") val city : String="",
        @SerializedName("state") val state : String="",
        @SerializedName("country") val country : String="",
        @SerializedName("zipcode") val zipcode : String="",
        @SerializedName("latitude") val latitude : String="",
        @SerializedName("longitude") val longitude : String="",
        @SerializedName("phone") val phone : String="",
        @SerializedName("make_default") val make_default : String="",
        @SerializedName("is_deleted") val is_deleted : String="",
        @SerializedName("created_at") val created_at : String="",
        @SerializedName("updated_at") val updated_at : String=""

    ) {
        fun defaultAddress():String{
            return "$first_name $last_name\n$address_one, $city, $state, $zipcode\n$phone"
        }
    }
    

}



