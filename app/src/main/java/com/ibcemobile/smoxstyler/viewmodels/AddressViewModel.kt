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
import com.ibcemobile.smoxstyler.responses.AddressResponse
import com.ibcemobile.smoxstyler.retrofit.ApiRepository
import com.kaopiz.kprogresshud.KProgressHUD
import com.ibcemobile.smoxstyler.responses.SimpleOkResponse
import com.ibcemobile.smoxstyler.utils.shortToast

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.HttpException
import java.util.ArrayList

class AddressViewModel : ViewModel() {

    var isUpdate = MutableLiveData<Boolean>()
    var isDeleted = MutableLiveData(false)
    var addressData = MutableLiveData<ArrayList<AddressResponse.AddressData>>()

    fun getAllAddress(
        context: Context,
        progressBar: KProgressHUD
    ) {
        progressBar.show()
        ApiRepository(context).getAddressList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<AddressResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: AddressResponse) {
                    progressBar.dismiss()
                    if (res.error) {
                        Toast.makeText(
                            context,
                            res.message, Toast.LENGTH_LONG
                        ).show()
                    } else {
                        addressData.value=res.result
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



    fun deleteAddress(
        context: Context,
        progressBar: KProgressHUD,
        jsonObject: JsonObject
    ) {
        progressBar.show()
        ApiRepository(context).deleteAddress(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse) {
                    progressBar.dismiss()
                    Toast.makeText(
                        context,
                        res.message, Toast.LENGTH_LONG
                    ).show()
                    isDeleted.value=res.error
                }

                override fun onError(e: Throwable) {
                    Log.e("TAG", "onError: ${e.message}")
                    progressBar.dismiss()
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

    fun updateAddress(context: Activity, progressBar: KProgressHUD, jsonObject: JsonObject) {addressData

        progressBar.show()
        ApiRepository(context).editAddress(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse) {
                    progressBar.dismiss()

                        Toast.makeText(
                            context,
                            res.message, Toast.LENGTH_LONG
                        ).show()
                    isUpdate.value=res.error


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

    fun addNewAddress(context: Activity, progressBar: KProgressHUD, jsonObject: JsonObject) {

        progressBar.show()
        ApiRepository(context).addAddress(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse) {
                    progressBar.dismiss()

                    Toast.makeText(
                        context,
                        res.message, Toast.LENGTH_LONG
                    ).show()
                    isUpdate.value=res.error


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



}