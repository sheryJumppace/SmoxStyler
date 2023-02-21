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
import com.ibcemobile.smoxstyler.model.ProductCategory
import com.ibcemobile.smoxstyler.responses.*
import com.ibcemobile.smoxstyler.retrofit.ApiRepository
import com.ibcemobile.smoxstyler.utils.shortToast
import com.kaopiz.kprogresshud.KProgressHUD
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

/**
 *
 * Start New Project LoveBuzz
 * Create Splash Screen WelCome Screen
 * Add Application Class And Base Class
 *
 * Work on Smox
 * Check product Dimensional
 * Add Validation in App Side
 * Fix UI Design in Track order
 *
 *
 */

class ProductViewModel : ViewModel() {

    var productRes = MutableLiveData<ProductResponse>()
    var productCategory = MutableLiveData<List<ProductCategory.CategoryList>>()
    var barberData = MutableLiveData<ArrayList<BarberResponse.BarberData>>()
    var isValidProduct = MutableLiveData<ProductValidResponse>()

    fun getBarbers(
        context: Context, progressBar: KProgressHUD, page: Int
    ) {
        progressBar.show()
        ApiRepository(context).getBarberList(page.toString()).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<BarberResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: BarberResponse) {
                    progressBar.dismiss()
                    if (res.error) {
                        Toast.makeText(
                            context, context.getString(R.string.error_connection), Toast.LENGTH_LONG
                        ).show()
                    } else {
                        barberData.postValue(res.result)
                    }
                }

                override fun onError(e: Throwable) {
                    progressBar.dismiss()
                    Log.e("++--++", "onError: ${e.message}")
                    if ((e as HttpException).code() == 401) {
                        shortToast(context.getString(R.string.authError))
                        APIHandler(context).logout()
                    } else shortToast(e.message())
                }

                override fun onComplete() {

                }

            })
    }

    fun getProductsByBarber(
        context: Context, progressBar: KProgressHUD, barberId: String, userId: Int
    ) {
        progressBar.show()
        ApiRepository(context).getAllProducts(barberId, userId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ProductResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: ProductResponse) {
                    try {
                        progressBar.dismiss()
                        Log.d("++--++", "ProductViewModel 105 getProductsByBarber res: $res")
                        if (res.error) {
                            Toast.makeText(
                                context, res.message, Toast.LENGTH_LONG
                            ).show()
                        } else {
                            productRes.postValue(res)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onError(e: Throwable) {
                    try {
                        progressBar.dismiss()
                        Log.d(
                            "++--++",
                            "ProductViewModel 105 getProductsByBarber onError: ${e.message}"
                        )

                        Log.e("++--++", "onError: ${e.message}")
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

    fun getMyProducts(
        context: Context,
        progressBar: KProgressHUD,
    ) {
        progressBar.show()
        ApiRepository(context).getMyProducts().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ProductResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: ProductResponse) {
                    progressBar.dismiss()

                    Log.d("++--++", "ProductViewModel 149 getMyProducts res: $res")

                    if (res.error) {
                        Toast.makeText(
                            context, res.message, Toast.LENGTH_LONG
                        ).show()
                    } else {
                        productRes.postValue(res)
                    }
                }

                override fun onError(e: Throwable) {
                    try {
                        progressBar.dismiss()
                        Log.e("++--++", "onError: ${e.message}")
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


    fun checkProduct(
        context: Context, progressBar: KProgressHUD, jsonObject: JsonObject
    ) {
        progressBar.show()
        ApiRepository(context).checkProduct(jsonObject).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ProductValidResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: ProductValidResponse) {
                    Log.e("++--++", "checkProduct onNext: ${res}")
                    progressBar.dismiss()
                    isValidProduct.value = res

                }

                override fun onError(e: Throwable) {
                    try {
                        progressBar.dismiss()
                        Log.e("++--++", "onError: ${e.message}")
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

    fun deleteProduct(
        context: Context, progressBar: KProgressHUD, productId: Int, barberId: String
    ) {
        progressBar.show()
        ApiRepository(context).deleteProduct(productId.toString()).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse) {
                    progressBar.dismiss()
                    Toast.makeText(
                        context, res.message, Toast.LENGTH_LONG
                    ).show()
                    //getProductsByBarber(context, progressBar, barberId, barberId.toInt())
                    getMyProducts(context, progressBar)
                }

                override fun onError(e: Throwable) {
                    Log.e("++--++", "onError: ${e.message}")
                    try {
                        progressBar.dismiss()
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

    fun updateProduct(context: Activity, progressBar: KProgressHUD, jsonObject: JsonObject) {
        progressBar.show()
        ApiRepository(context).updateProduct(jsonObject).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse) {
                    progressBar.dismiss()

                    Toast.makeText(
                        context, res.message, Toast.LENGTH_LONG
                    ).show()
                    context.finish()

                }

                override fun onError(e: Throwable) {
                    try {
                        progressBar.dismiss()
                        Log.e("++--++", "onError: ${e.message}")
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

    fun addNewProduct(context: Activity, progressBar: KProgressHUD, jsonObject: JsonObject) {

        progressBar.show()
        ApiRepository(context).addNewProduct(jsonObject).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SimpleOkResponse2> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: SimpleOkResponse2) {
                    progressBar.dismiss()

                    Toast.makeText(
                        context, res.message, Toast.LENGTH_LONG
                    ).show()
                    if (!res.error) context.finish()

                }

                override fun onError(e: Throwable) {
                    try {
                        progressBar.dismiss()
                        Log.e("++--++", "onError: ${e.message}")
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

    fun getCategoryList(context: Context, progressBar: KProgressHUD) {
        progressBar.show()
        ApiRepository(context).getProductCategory().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ProductCategory> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: ProductCategory) {
                    progressBar.dismiss()
                    if (!res.error) {
                        if (res.result.isNotEmpty()) {
                            productCategory.postValue(res.result)
                        }
                    } else Toast.makeText(
                        context, res.message, Toast.LENGTH_LONG
                    ).show()
                }

                override fun onError(e: Throwable) {
                    try {
                        progressBar.dismiss()
                        Log.e("++--++", "onError: ${e.message}")
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


}