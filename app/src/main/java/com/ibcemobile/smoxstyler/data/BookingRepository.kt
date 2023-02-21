package com.ibcemobile.smoxstyler.data

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.*
import com.ibcemobile.smoxstyler.responses.ContactResponse
import com.ibcemobile.smoxstyler.retrofit.ApiRepository
import com.ibcemobile.smoxstyler.utils.shortToast
import com.ibcemobile.smoxstyler.viewmodels.getCurrentDate
import com.ibcemobile.smoxstyler.viewmodels.getFormattedTime
import com.kaopiz.kprogresshud.KProgressHUD
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray

import org.json.JSONObject
import retrofit2.HttpException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository module for handling data operations.
 */
class BookingRepository : BaseObservable() {

    var isUpdated: MutableLiveData<Boolean> = MutableLiveData()
    var getcontacts: MutableLiveData<ContactResponse.Result.ContactUser> = MutableLiveData<ContactResponse.Result.ContactUser>()
    var timeSlotList: MutableLiveData<List<TimeSlotResult>> = MutableLiveData()
    var services: MutableLiveData<List<Category>> = MutableLiveData()




    fun fetchServices(context: Context,barber_Id: String) {

        val params = HashMap<String, String>()
        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        if (!(context as Activity).isFinishing)
            progressHUD.show()

        APIHandler(
            context,
            Request.Method.GET,
            Constants.API.get_category+"/"+barber_Id,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val jsonArray = result.getJSONArray("result")
                    val items: ArrayList<Category> = ArrayList()
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val service = Category(json)
                        items.add(service)
                    }
                    services.value = items
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        context.applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun fetchSlots(context: Context, day: Date, newStartTime: String, newEndTime: String,barber_Id:String, appointmentId:String) {
        val dateFormatter = SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
        val date = dateFormatter.format(day)
        val params = HashMap<String, String>()
        params["barber_id"] = barber_Id
        params["current_date"] = getCurrentDate()!!
        params["current_time"] = getFormattedTime()!!
        params["appointment_id"] = appointmentId
        if ((newStartTime != "-" && newEndTime != "-") && (newStartTime != newEndTime)) {
            try {
                var strStartTime = "$date $newStartTime"
                var strEndTime = "$date $newEndTime"
                val dFormatter =
                    SimpleDateFormat(Constants.KDateFormatter.local, Locale.getDefault())
                val startDate = dFormatter.parse(strStartTime)
                val endDate = dFormatter.parse(strEndTime)
                dFormatter.applyPattern(Constants.KDateFormatter.server)
                strStartTime = dFormatter.format(startDate!!)
                strEndTime = dFormatter.format(endDate!!)
                params["start_time"] = strStartTime
                params["end_time"] = strEndTime
                //Log.e("TAG", "fetchList: $params")
                APIHandler(
                    context,
                    Request.Method.GET,
                    Constants.API.appointment_timeslots,
                    params,
                    object : APIHandler.NetworkListener {
                        override fun onResult(result: JSONObject) {
                            timeSlotList.value = getTimeSlots(result.getJSONArray("result"))
                        }

                        override fun onFail(error: String?) {
                            Toast.makeText(
                                context,
                                error, Toast.LENGTH_LONG
                            ).show()
                        }
                    })
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } else {
            //slots.value = arrayListOf("Closed")
            timeSlotList.value = arrayListOf(TimeSlotResult("Closed", 0, false))
        }
    }


    private fun getTimeSlots(jsonArray: JSONArray): ArrayList<TimeSlotResult> {
        val timeSlotList = arrayListOf<TimeSlotResult>()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            val timeSlot = TimeSlotResult(json.getString("timeslot"), json.getInt("status"), false)
            timeSlotList.add(timeSlot)
        }
        return timeSlotList
    }
    private fun getCurrentDate(): String? {
        val d = Date(Date().time + 28800000)
        return SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault()).format(d)
    }


    private fun getFormattedTime(): String? {
        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormatter.format(calendar.time)
    }


    companion object {
        @Volatile
        private var instance: BookingRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: BookingRepository().also { instance = it }
            }
    }


    fun sendReorderService(context: Context, data: JSONObject) {

        APIHandler(
            context,
            Request.Method.POST,
            Constants.API.rearrange_service,
            data,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    Log.e("Data Json Object Result", result.toString())

                }

                override fun onFail(error: String?) {
                    Log.e("Data Json Object Result", error.toString())
                }
            },
            "json"
        )
    }


    fun deleteServiceFromServer(context: Context, serviceId: Int) {
        val params = HashMap<String, String>()

        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        if (!(context as Activity).isFinishing)
            progressHUD.show()

        APIHandler(
            context,
            Request.Method.DELETE,
            Constants.API.service + "/" + serviceId,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()

                    isUpdated.value=true

                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        context.applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


    fun getBarberProfileByContact(context: Context,jsonObject: JsonObject){
        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        if (!(context as Activity).isFinishing)
            progressHUD.show()


        ApiRepository(context).getBarberProfileByContact(jsonObject)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ContactResponse> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(res: ContactResponse) {
                    progressHUD.dismiss()
                    if (!res.error!!){
                        if (res.result!=null){
                            getcontacts.value=res.result.contacts
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    progressHUD.dismiss()
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
