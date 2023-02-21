package com.ibcemobile.smoxstyler.responses

import com.google.gson.annotations.SerializedName

data class SimpleOkResponse2(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String
)

