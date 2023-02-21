package com.ibcemobile.smoxstyler.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.responses.ProfileResponse
import com.ibcemobile.smoxstyler.responses.SimpleOkResponse2
import com.ibcemobile.smoxstyler.retrofit.ApiRepository
import com.ibcemobile.smoxstyler.utils.shortToast
import com.kaopiz.kprogresshud.KProgressHUD
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class UserViewModel : ViewModel() {

    var barberProfile = MutableLiveData<ProfileResponse>()
    var isUpdateProfile = MutableLiveData<JsonObject>()
    var isDeleteAccount = MutableLiveData<SimpleOkResponse2>()


    fun getBarberProfile(
        context: Context, progressBar: KProgressHUD
    ) {
        progressBar.show()
        ApiRepository(context).getBarberProfile().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ProfileResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: ProfileResponse) {
                    progressBar.dismiss()
                    if (res.error) {
                        Toast.makeText(
                            context, context.getString(R.string.error_connection), Toast.LENGTH_LONG
                        ).show()
                    } else {
                        barberProfile.postValue(res)
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    //Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code() == 401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    } else shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun updateProfile(
        context: Context, progressBar: KProgressHUD, apiPath: String, jsonObject: JsonObject
    ) {
        progressBar.show()
        ApiRepository(context).updateProfile(apiPath, jsonObject).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<JsonObject> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: JsonObject) {
                    progressBar.dismiss()
                    if (!res.get("error").asBoolean) {
                        isUpdateProfile.value = res
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    try {
                        Log.e("TAG", "onError: ${e.message}")
                        if ((e as HttpException).code() == 401) {
                            shortToast(context.getString(R.string.authError))
                            APIHandler(context).logout()
                        } else shortToast(e.message())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onComplete() {

                }

            })
    }

    fun updateProfileWithPost(
        context: Context, progressBar: KProgressHUD, apiPath: String, jsonObject: JsonObject
    ) {
        progressBar.show()
        ApiRepository(context).updateProfileWithPost(apiPath, jsonObject)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<JsonObject> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: JsonObject) {
                    progressBar.dismiss()
                    if (!res.get("error").asBoolean) {
                        isUpdateProfile.value = res
                    }
                    Toast.makeText(
                        context, res.get("message").asString, Toast.LENGTH_LONG
                    ).show()
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code() == 401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    } else shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun deleteAccount(
        context: Context, progressBar: KProgressHUD
    ) {
        progressBar.show()
        ApiRepository(context).deleteAccount().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse2> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse2) {
                    progressBar.dismiss()
                    Toast.makeText(
                        context, res.message, Toast.LENGTH_LONG
                    ).show()
                    if (!res.error) {
                        isDeleteAccount.value = res
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code() == 401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    } else shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }


    fun sendNotification(context: Context, jsonObject: JsonObject) {
        ApiRepository(context).sendNotification(jsonObject).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<JsonObject> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: JsonObject) {
                    // progressBar.dismiss()

                }

                override fun onError(e: Throwable) {
                    //progressBar.dismiss()
                    Log.e("TAG", "onError: ${e.message}")
                    if ((e as HttpException).code() == 401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    } else shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }


}