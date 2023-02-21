package com.ibcemobile.smoxstyler.responses

import com.google.gson.annotations.SerializedName
import com.ibcemobile.smoxstyler.manager.Constants
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderResponse(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<OrderResult>
) {
    class OrderResult(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("barber_id") var barberId: Int? = null,
        @SerializedName("user_id") var userId: Int? = null,
        @SerializedName("sub_total") var subTotal: String? = null,
        @SerializedName("shipping_charges") var shippingCharges: String? = null,
        @SerializedName("discount") var discount: String? = null,
        @SerializedName("total_price") var totalPrice: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("phone") var phone: String? = null,
        @SerializedName("address") var address: String? = null,
        @SerializedName("payment_status") var paymentStatus: String? = null,
        @SerializedName("order_status") var orderStatus: String? = null,
        @SerializedName("delivery_date") var deliveryDate: String? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("barber_image") var barberImage: String? = null,
        @SerializedName("product_name") var productName: String? = null,
        @SerializedName("quantity") var quantity: Int? = null,
        @SerializedName("product_image") var productImage: String? = null,
        @SerializedName("barber_fname") var barberFname: String? = null,
        @SerializedName("barber_lname") var barberLname: String? = null,
        @SerializedName("product_count") var productCount: Int? = null
    ) {
        fun getTotal(): Double {
            return totalPrice!!.toDouble()
        }
        fun getDate(): String {
            return Constants.mFormatsTo(deliveryDate!!)
        }
        fun getOrderDate():String{
            val inputFormat = SimpleDateFormat(Constants.KDateFormatter.server, Locale.getDefault())
            inputFormat.timeZone= TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(createdAt)
            val outputFormat = SimpleDateFormat(Constants.KDateFormatter.displayFullTime, Locale.getDefault())
            outputFormat.timeZone= TimeZone.getDefault()
            return outputFormat.format(date)
        }
    }
}

class OrderItem(
    @SerializedName("error") val error: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: ArrayList<OrderItemRow>

) {
    class OrderItemRow(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("cart_id") var cart_id: Int? = null,
        @SerializedName("barber_id") var barberId: Int? = null,
        @SerializedName("user_id") var userId: Int? = null,
        @SerializedName("category_id") var categoryId: Int? = null,
        @SerializedName("product_id") var productId: Int? = null,
        @SerializedName("product_name") var productName: String? = null,
        @SerializedName("quantity") var quantity: String? = null,
        @SerializedName("main_image") var mainImage: String? = null,
        @SerializedName("actual_price") var actualPrice: String? = null,
        @SerializedName("discounted_price") var discountedPrice: String? = null,
        @SerializedName("shipping_charge") var shippingCharge: String? = null,
        @SerializedName("total_price") var totalPrice: String? = null,
        @SerializedName("created_at") var createdAt: String? = null,
        @SerializedName("updated_at") var updatedAt: String? = null,
        @SerializedName("deleted_at") var deletedAt: String? = null
    )
}