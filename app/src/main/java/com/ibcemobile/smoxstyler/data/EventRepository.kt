package com.ibcemobile.smoxstyler.data

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Event
import com.kaopiz.kprogresshud.KProgressHUD
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository module for handling data operations.
 */
class EventRepository : BaseObservable() {
    var isUpdated: MutableLiveData<Boolean> = MutableLiveData()
     fun postEvent(context: Context, event: Event) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val params = HashMap<String, String>()
        params["event"] = event.event
        params["created"] = dateFormat.format(calendar.time)
        params["message"] = event.message
        params["start"] = event.start_date
        params["end"] = event.end_date
        params["name"] = event.name
        params["image_url"] = event.image

        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        if (!(context as Activity).isFinishing)
            progressHUD.show()

        APIHandler(
            context,
            Request.Method.POST,
            Constants.KLocalBroadCast.event,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    (context as Activity).finish()

                    val error = result.getBoolean("error")
                    isUpdated.value = error

                    if (result.has("message")){
                        Toast.makeText(
                            context,
                            result.getString("message"), Toast.LENGTH_LONG
                        ).show()
                    }


                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        context,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    fun updateEvent(context: Context, event: Event) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val params = HashMap<String, String>()
        params["id"] = event.id.toString()
        params["event"] = event.event
        params["created"] = dateFormat.format(calendar.time)
        params["message"] = event.message
        params["start"] = event.start_date
        params["end"] = event.end_date
        params["name"] = event.name
        params["image_url"] = event.image

        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        if (!(context as Activity).isFinishing)
            progressHUD.show()

        APIHandler(
            context,
            Request.Method.PUT,
            Constants.KLocalBroadCast.event,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    (context as Activity).finish()

                    val error = result.getBoolean("error")
                    isUpdated.value = error

                    if (result.has("message")){
                        Toast.makeText(
                            context,
                            result.getString("message"), Toast.LENGTH_LONG
                        ).show()
                    }


                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        context,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }

    fun deleteEvent(context: Context, event: Event) {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val params = HashMap<String, String>()
        params["event"] = event.event
        params["created"] = dateFormat.format(calendar.time)
        params["message"] = event.message
        params["start"] = event.start_date
        params["end"] = event.end_date
        params["name"] = event.name
        params["image_url"] = event.image

        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        if (!(context as Activity).isFinishing)
            progressHUD.show()

        APIHandler(
            context,
            Request.Method.PUT,
            Constants.KLocalBroadCast.event,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    (context as Activity).finish()

                    val error = result.getBoolean("error")
                    isUpdated.value = error

                    if (result.has("message")){
                        Toast.makeText(
                            context,
                            result.getString("message"), Toast.LENGTH_LONG
                        ).show()
                    }


                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        context,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            }
        )
    }


    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: EventRepository().also { instance = it }
            }
    }
}

