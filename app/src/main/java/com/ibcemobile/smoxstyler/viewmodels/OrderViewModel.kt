package com.ibcemobile.smoxstyler.viewmodels

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.responses.*
import com.ibcemobile.smoxstyler.retrofit.ApiRepository
import com.ibcemobile.smoxstyler.utils.shortToast
import com.kaopiz.kprogresshud.KProgressHUD
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class OrderViewModel : ViewModel() {

    var isCartUpdate = MutableLiveData<Boolean>()
    var orderPlace = MutableLiveData<CartPaymentResponse.PaymentResult>()
    var orderResponse = MutableLiveData<ArrayList<OrderResponse.OrderResult>>()
    var orderItem = MutableLiveData<ArrayList<OrderItem.OrderItemRow>>()
    var barberCarts = MutableLiveData<ArrayList<CartBarberItem>>()
    var isOrderPlaced = MutableLiveData(false)

    fun getProductBoughtList(
        context: Context,
        progressBar: KProgressHUD,
        sortBy: String,
        page: Int
    ) {
        progressBar.show()
        ApiRepository(context).getProductBoughtList(sortBy, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<OrderResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: OrderResponse) {
                    progressBar.dismiss()
                    if (res.error) {
                        Toast.makeText(
                            context,
                            res.message, Toast.LENGTH_LONG
                        ).show()
                    } else {
                        orderResponse.postValue(res.result)
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code()==401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    }
                    else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun getProductSellList(context: Context, progressBar: KProgressHUD, sortBy: String, page: Int) {
        progressBar.show()
        ApiRepository(context).getProductSellList(sortBy, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<OrderResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: OrderResponse) {
                    progressBar.dismiss()
                    if (res.error) {
                        Toast.makeText(
                            context,
                            res.message, Toast.LENGTH_LONG
                        ).show()
                    } else {
                        orderResponse.postValue(res.result)
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code()==401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    }
                    else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun getOrderDetail(context: Context, progressBar: KProgressHUD, orderId: String) {
        progressBar.show()
        ApiRepository(context).getOrderDetail(orderId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<OrderItem> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: OrderItem) {
                    progressBar.dismiss()
                    if (res.error) {
                        Toast.makeText(
                            context,
                            res.message, Toast.LENGTH_LONG
                        ).show()
                    } else {
                        orderItem.postValue(res.result)
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code()==401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    }
                    else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun cancelOrder(
        context: Activity,
        progressBar: KProgressHUD,
        orderId: String
    ) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("order_id", orderId)
        progressBar.show()
        ApiRepository(context).cancelOrder(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse2> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse2) {
                    progressBar.dismiss()

                    Toast.makeText(
                        context,
                        res.message, Toast.LENGTH_LONG
                    ).show()
                    isCartUpdate.value = res.error

                    //getCartListByBarber(context,progressBar, barberId)

                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code()==401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    }
                    else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun placeOrder(context: Activity, progressBar: KProgressHUD, jsonObject: JsonObject) {

        progressBar.show()
        ApiRepository(context).placeOrder(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CartPaymentResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: CartPaymentResponse) {
                    progressBar.dismiss()

                    if (res.error)
                    Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()

                    orderPlace.value = res.result

                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    //Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code()==401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    }
                    else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun confirmPayment(context: Context, progressBar: KProgressHUD, jsonObject: JsonObject) {
        progressBar.show()
        ApiRepository(context).confirmOrder(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse2> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse2) {
                    Log.e("TAG", "onNext: "+res )
                    progressBar.dismiss()
                    if (!res.error) {
                        isOrderPlaced.postValue(true)
                    }

                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    //Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code()==401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    }
                    else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }


}