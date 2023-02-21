package com.ibcemobile.smoxstyler.model

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class TimeSlots :Serializable {
    var timeslot:String=""
    var status:Int=0

    constructor() : super()
    constructor(json: JSONObject){
        try {
            if (json.has("timeslot")) {
                timeslot = json.getString("timeslot")
            }


            if (json.has("status")) {
                status = json.getInt("status")
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

}