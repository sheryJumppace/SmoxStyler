package com.ibcemobile.smoxstyler.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Barber
import com.ibcemobile.smoxstyler.model.TimeSlotResult
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.set

class TimeSlotListViewModel internal constructor(private val barber: Barber) : ViewModel() {
    var timeSlotList: MutableLiveData<List<TimeSlotResult>> = MutableLiveData()





    fun fetchList(context: Context, day: Date, newStartTime: String, newEndTime: String, appointmentId:Int) {
        val dateFormatter = SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
        val date = dateFormatter.format(day)
        val params = HashMap<String, String>()
        params["barber_id"] = barber.id.toString()
        params["current_date"] = getCurrentDate()!!
        params["current_time"] = getFormattedTime()!!
        params["appointment_id"] = appointmentId.toString()
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
                Log.e("TAG", "fetchList: timeslots $params")
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

fun getCurrentDate(): String? {
    val d = Date(Date().time + 28800000)
    return SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault()).format(d)
}


fun getFormattedTime(): String? {
    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return dateFormatter.format(calendar.time)
}

class TimeSlotListViewModelFactory(
    private val barber: Barber
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = TimeSlotListViewModel(barber) as T
}