package com.ibcemobile.smoxstyler.model

import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.text.ParseException

class Holiday :Serializable {
    var id: String = ""
    var barber_id: String = ""
    var date: String = ""
    var title: String = ""
    var created_at: String = ""
    var day: String = ""



    constructor(json: JSONObject) {
        try {
            if (json.has("id")) {
                id = json.getString("id")
            }
            if (json.has("barber_id")) {
                this.barber_id = json.getString("barber_id")
            }
            if (json.has("date")) {
                this.date = Constants.formatDate(json.getString("date"))!!
            }
            if (json.has("title")) {
                this.title = json.getString("title")
            }
            if (json.has("created_at")) {
                this.created_at = json.getString("created_at")
            }
            if (json.has("day")) {
                this.day = json.getString("day")
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

    }

    fun getFieldNameOfDay():String{
        return  when(day) {
            "su"->"Sunday"
            "mo"->"Monday"
            "tu"->"Tuesday"
            "we"->"Wednesday"
            "th"->"Thursday"
            "fr"->"Friday"
            "sa"->"Saturday"
            else->"Monday"
        }
    }


}