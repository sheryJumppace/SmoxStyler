package com.ibcemobile.smoxstyler.retrofit


import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.model.ProductCategory
import com.ibcemobile.smoxstyler.responses.*
import io.reactivex.Observable
import org.json.JSONObject
import retrofit2.http.*

interface ApiInterface {

    @GET("get_profile")
    fun getBarberProfile(): Observable<ProfileResponse>


    @POST("user_by_phone")
    fun getBarberProfileByContact(@Body jsonObject: JsonObject): Observable<ContactResponse>

    @POST("delete_account")
    fun deleteAccount(): Observable<SimpleOkResponse2>

    @POST("send_notification")
    fun sendNotification(@Body jsonObject: JsonObject): Observable<JsonObject>

    @HTTP(method = "GET", path = "review", hasBody = true)
    fun getBarberReview(@Body jsonObject: JsonObject): Observable<ReviewResponse>

    @PUT("{path}")
    fun updateProfile(@Path("path") path: String,@Body jsonObject: JsonObject): Observable<JsonObject>

    @POST("{path}")
    fun updateProfileWithPost(@Path("path") path: String,@Body jsonObject: JsonObject): Observable<JsonObject>


    @GET("barbers_list/{path}")
    fun getBarberList(@Path("path") path: String): Observable<BarberResponse>

    ////////////////////////////  Product   ////////////////////////////////

    @GET("products/{barberId}/{userId}")
    fun getAllProducts(@Path("barberId") barberId: String,@Path("userId") userId: Int): Observable<ProductResponse>

    @GET("barber/products")
    fun getMyProducts(): Observable<ProductResponse>

    @DELETE("products/{productId}")
    fun deleteProduct(@Path("productId") barberId: String): Observable<SimpleOkResponse>

    @PUT("barber/products")
    fun updateProduct(@Body jsonObject: JsonObject): Observable<SimpleOkResponse>

    @POST("products/add")
    fun addNewProduct(@Body jsonObject: JsonObject): Observable<SimpleOkResponse2>

    @GET("products/categories")
    fun getProductCategory(): Observable<ProductCategory>

    ////////////////////////////  Cart   ////////////////////////////////

    @POST("cart")
    fun addToCart(@Body jsonObject: JsonObject): Observable<SimpleOkResponse2>

    @GET("cart")
    fun getCartList(): Observable<CartResponse>

    @GET("cart/{barberId}")
    fun getCartListByBarber(@Path("barberId") barberId: String): Observable<CartResponse>

    @POST("cart")
    fun updateCart(@Body jsonObject: JsonObject): Observable<SimpleOkResponse2>

    @GET("cart/barbers")
    fun getCartBarberList(): Observable<CartBarberResponse>


    ///////////////////////// Address /////////////////////////

    @POST("deliveryAddress")
    fun addAddress(@Body jsonObject: JsonObject): Observable<SimpleOkResponse>

    //@DELETE("deliveryAddress/delete")
    @HTTP(method = "DELETE", path = "deliveryAddress/delete", hasBody = true)
    fun deleteAddress(@Body jsonObject: JsonObject): Observable<SimpleOkResponse>

    @POST("deliveryAddress")
    fun updateAddress(@Body jsonObject: JsonObject): Observable<SimpleOkResponse>

    @GET("deliveryAddress")
    fun getAddress(): Observable<AddressResponse>

    /////////////////////////////  Order //////////////////////

    @POST("checkout")
    fun placeOrder(@Body jsonObject: JsonObject): Observable<CartPaymentResponse>

    @POST("checkout")
    fun confirmOrder(@Body jsonObject: JsonObject): Observable<SimpleOkResponse2>

    @GET("orderlist/customer/{sortBy}")
    fun getProductBoughtList(@Path("sortBy") sortBy: String, @Query("page") pageNumber: Int): Observable<OrderResponse>

    @GET("orderlist/barber/{sortBy}")
    fun getProductSellList(@Path("sortBy") sortBy: String, @Query("page") pageNumber: Int): Observable<OrderResponse>

    @GET("order/{orderId}")
    fun getOrderDetail(@Path("orderId") orderId: String): Observable<OrderItem>

    @POST("cancelOrder")
    fun cancelOrder(@Body jsonObject: JsonObject): Observable<SimpleOkResponse2>


    //////////////////////////// Product validation ///////////////////

    @POST("check_dimension")
    fun checkProduct(@Body jsonObject: JsonObject): Observable<ProductValidResponse>


}