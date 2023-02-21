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
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.HttpException

class CartViewModel : ViewModel() {

    var isCartUpdate = MutableLiveData<Boolean>()
    var cartData = MutableLiveData<CartData>()
    var barberCarts = MutableLiveData<ArrayList<CartBarberItem>>()

    fun getCartListByBarber(context: Context, progressBar: KProgressHUD, barberId: String) {
        //progressBar.show()
        ApiRepository(context).getCartListByBarber(barberId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CartResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: CartResponse) {
                    //progressBar.dismiss()
                    if (res.error) {
                        Toast.makeText(
                            context,
                            res.message, Toast.LENGTH_LONG
                        ).show()
                    } else {
                        cartData.postValue(res.result)
                    }
                }

                override fun onError(e: Throwable) {
                    // progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code() == 401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    } else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun getCartBarberList(context: Context, progressBar: KProgressHUD) {
        progressBar.show()
        ApiRepository(context).getCartBarberList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CartBarberResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: CartBarberResponse) {
                    progressBar.dismiss()
                    if (res.error) {
                        Toast.makeText(
                            context,
                            res.message, Toast.LENGTH_LONG
                        ).show()
                    } else {
                        barberCarts.postValue(res.result)
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code() == 401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    } else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun updateCart(
        context: Activity,
        progressBar: KProgressHUD,
        jsonObject: JsonObject,
        barberId: String
    ) {
        progressBar.show()
        ApiRepository(context).updateCart(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse2> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse2) {
                    progressBar.dismiss()

                    if (res.error)
                        Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                    else {
                        getCartListByBarber(context, progressBar, barberId)
                    }

                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code() == 401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    } else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun addToCart(context: Activity, progressBar: KProgressHUD, jsonObject: JsonObject) {

        progressBar.show()
        ApiRepository(context).addToCart(jsonObject)
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

                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    //Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code() == 401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    } else
                        shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }


}