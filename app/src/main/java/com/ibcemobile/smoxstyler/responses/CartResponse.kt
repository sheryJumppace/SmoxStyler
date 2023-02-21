package com.ibcemobile.smoxstyler.responses

class CartResponse(
    val error: Boolean,
    val message: String,
    val result: CartData
)

class CartData(
    val cart_items: ArrayList<CartItems>,
    val cart_count: Int,
    val subtotal: Double,
    val discounted_price: Double,
    val discount: Double,
    val default_address: AddressResponse.AddressData,
    val shipping: Double,
    val zip_error: String,
    val total: Double
) {
    fun defaultAddress(): String {

        return default_address.first_name + " " + default_address.last_name + "\n" + default_address.address_one + ", " +
                default_address.address_two + ", " + default_address.city + ", " + default_address.state + ", " +
                default_address.zipcode + "\n" + default_address.phone
    }

    class CartItems(
        var id: Int,
        val product_id: Int,
        val barber_id: Int,
        val user_id: Int,
        var quantity: Int,
        val is_deleted: Int,
        val created_at: String,
        val updated_at: String,
        val product: ProductResponse.ProductItem
    )
}

