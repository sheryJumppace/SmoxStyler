package com.ibcemobile.smoxstyler.responses

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ProductValidResponse(

    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: String,
)

