package com.ibcemobile.smoxstyler.model

import java.io.Serializable


open class CustomMenu(val icon:Int, val title:String,val subTitle:String) : Serializable {
    var badge: String = ""
}

