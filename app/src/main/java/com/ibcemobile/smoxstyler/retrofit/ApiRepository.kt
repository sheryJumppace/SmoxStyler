package com.ibcemobile.smoxstyler.retrofit

import android.content.Context
import android.content.LocusId
import android.util.Log
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.model.ProductCategory
import com.ibcemobile.smoxstyler.responses.*
import io.reactivex.Observable

class ApiRepository(private val context: Context) {

    private val apiService: RetrofitApiService = RetrofitApiService()


    fun getBarberProfile(
    ): Observable<ProfileResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getBarberProfile()
    }

    fun getBarberProfileByContact(
        jsonObject: JsonObject
    ): Observable<ContactResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getBarberProfileByContact(jsonObject)
    }
    fun sendNotification(
        jsonObject: JsonObject
    ): Observable<JsonObject> {
        return apiService.getRetrofitServiceForAuthForApp(context).sendNotification(jsonObject)
    }


    fun getBarberReview(
        jsonObject: JsonObject
    ): Observable<ReviewResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getBarberReview(jsonObject)
    }

    fun updateProfile(
        path:String,
        jsonObject: JsonObject
    ): Observable<JsonObject> {
        return apiService.getRetrofitServiceForAuthForApp(context).updateProfile(path,jsonObject)
    }
    fun updateProfileWithPost(
        path:String,
        jsonObject: JsonObject
    ): Observable<JsonObject> {
        return apiService.getRetrofitServiceForAuthForApp(context).updateProfileWithPost(path,jsonObject)
    }

    fun deleteAccount(
    ): Observable<SimpleOkResponse2> {
        return apiService.getRetrofitServiceForAuthForApp(context).deleteAccount()
    }

    fun getBarberList(
        page: String
    ): Observable<BarberResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getBarberList(page)
    }

    fun getAllProducts(
        barberId: String,
        userId: Int
    ): Observable<ProductResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getAllProducts(barberId, userId)
    }

    fun getMyProducts(): Observable<ProductResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getMyProducts()
    }

    fun deleteProduct(
        productId: String
    ): Observable<SimpleOkResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).deleteProduct(productId)
    }

    fun updateProduct(
        jsonObject: JsonObject
    ): Observable<SimpleOkResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).updateProduct(jsonObject)
    }

    fun addNewProduct(
        jsonObject: JsonObject
    ): Observable<SimpleOkResponse2> {
        return apiService.getRetrofitServiceForAuthForApp(context).addNewProduct(jsonObject)
    }

    fun getProductCategory(
    ): Observable<ProductCategory> {
        return apiService.getRetrofitServiceForAuthForApp(context).getProductCategory()
    }

    ///////////////////////////  Cart /////////////////////////////////////

    fun addToCart(
        jsonObject: JsonObject
    ): Observable<SimpleOkResponse2> {
        return apiService.getRetrofitServiceForAuthForApp(context).addToCart(jsonObject)
    }

    fun getCartList(
    ): Observable<CartResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getCartList()
    }

    fun getCartListByBarber(barberId: String
    ): Observable<CartResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getCartListByBarber(barberId)
    }

    fun updateCart(
        jsonObject: JsonObject
    ): Observable<SimpleOkResponse2> {
        return apiService.getRetrofitServiceForAuthForApp(context).updateCart(jsonObject)
    }

    fun getCartBarberList(
    ): Observable<CartBarberResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getCartBarberList()
    }


    ///////////////////////////  Address /////////////////////////////////////


    fun getAddressList(
    ): Observable<AddressResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getAddress()
    }

    fun editAddress(jsonObject: JsonObject
    ): Observable<SimpleOkResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).updateAddress(jsonObject)
    }

    fun deleteAddress(jsonObject: JsonObject
    ): Observable<SimpleOkResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).deleteAddress(jsonObject)
    }

    fun addAddress(jsonObject: JsonObject
    ): Observable<SimpleOkResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).addAddress(jsonObject)
    }


    /////////////////////////////  Order //////////////////////////

    fun placeOrder(
        jsonObject: JsonObject
    ): Observable<CartPaymentResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).placeOrder(jsonObject)
    }

    fun confirmOrder(
        jsonObject: JsonObject
    ): Observable<SimpleOkResponse2> {
        return apiService.getRetrofitServiceForAuthForApp(context).confirmOrder(jsonObject)
    }

    fun getProductBoughtList(
        sortBy:String,
        page: Int
    ): Observable<OrderResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getProductBoughtList(sortBy,page)
    }

    fun getProductSellList(
        sortBy:String,
        page: Int
    ): Observable<OrderResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).getProductSellList(sortBy,page)
    }

    fun getOrderDetail(
        orderId:String
    ): Observable<OrderItem> {
        return apiService.getRetrofitServiceForAuthForApp(context).getOrderDetail(orderId)
    }
    fun cancelOrder(
        jsonObject: JsonObject
    ): Observable<SimpleOkResponse2> {
        return apiService.getRetrofitServiceForAuthForApp(context).cancelOrder(jsonObject)
    }



    fun checkProduct(
        jsonObject: JsonObject
    ): Observable<ProductValidResponse> {
        return apiService.getRetrofitServiceForAuthForApp(context).checkProduct(jsonObject)
    }



}