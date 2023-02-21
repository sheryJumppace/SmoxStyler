package com.ibcemobile.smoxstyler.model

import com.ibcemobile.smoxstyler.manager.Constants
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.text.ParseException

class Funds(json: JSONObject) : Serializable{
    var id:String = ""
    var price:Int = 0
    var appointment_id:String = ""
    var full_name:String = ""
    var last_name:String = ""
    var image:String = ""
    var refund_id:String=""
    var status_change_at:String = ""
    var is_transfer:String = ""


    init {
        try {
            if (json.has("id")) {
                this.id = json.getString("id")
            }
            if (json.has("refund_id")) {
                this.refund_id = json.getString("refund_id")
            }
            if (json.has("price")) {
                this.price = json.getInt("price")
            }
            if (json.has("appointment_id")) {
                this.appointment_id = json.getString("appointment_id")
            }
            if (json.has("first_name")) {
                this.full_name = "Received From "+json.getString("first_name")
            }
            if (json.has("last_name")) {
                this.last_name = json.getString("last_name")
            }

            if (json.has("is_transfer")) {
                this.is_transfer = json.getString("is_transfer")
            }

            if (json.has("image")) {
                this.image = json.getString("image")
            }
            if (json.has("status_change_at")) {
                this.status_change_at = Constants.mFormats(json.getString("status_change_at"))
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }


}