package com.ibcemobile.smoxstyler.requests

import com.google.gson.annotations.SerializedName

data class CheckoutRequest(
    var barber_id:String?=null,
    var name:String?=null,
    var phone:String?=null,
    var email:String?=null,
    var address_id:String?=null,
    var address:String?=null,
    var subtotal:String?=null,
    var discounted_price:String?=null,
    var discount:String?=null,
    var shipping:String?=null,
    var total:String?=null,
    var cart_id:ArrayList<Int>?=null,
    var latitude:String?=null,
    var longitude:String?=null,
    )