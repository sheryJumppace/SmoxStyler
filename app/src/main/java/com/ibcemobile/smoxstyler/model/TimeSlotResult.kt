package com.ibcemobile.smoxstyler.model

import com.google.gson.annotations.SerializedName

class TimeSlotResult(
    @SerializedName("timeslot")
    val timeslot: String = "",
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("isSelected")
    var isSelected: Boolean = false)
{
    fun canSelected():Boolean{

        return status!=1
    }
}