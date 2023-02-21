package com.ibcemobile.smoxstyler.responses

import com.google.gson.annotations.SerializedName

data class SimpleOkResponse(

    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<String>
)

