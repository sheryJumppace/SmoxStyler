package com.ibcemobile.smoxstyler.model

import com.ibcemobile.smoxstyler.model.type.UpNextStatus
import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable
import java.text.ParseException

open class UpNextOption: Serializable {
    var id:Int = 0
    var title:String = ""
    var isStatic = false
    constructor() {
        this.title = UpNextStatus.Automatically.name
        this.id = 0
        isStatic = true
    }
    constructor(title: String) {
        this.title = title
        this.id = 0
        isStatic = true
    }
    constructor(json: JSONObject) {
        try {
            if (json.has("id")) {
                id = json.getInt("id")
            }
            if (json.has("title")){
                title = json.getString("title")
            }
            isStatic = false
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e:ParseException){
            e.printStackTrace()
        }

    }
}
