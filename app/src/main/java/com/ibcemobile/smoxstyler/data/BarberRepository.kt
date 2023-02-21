package com.ibcemobile.smoxstyler.data

import android.content.Context
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.google.android.gms.maps.model.LatLng
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Barber
import com.kaopiz.kprogresshud.KProgressHUD

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


/**
 * Repository module for handling data operations.
 */
class BarberRepository : BaseObservable() {

    var barbers: MutableLiveData<List<Barber>> = MutableLiveData()

    fun getBarber(id: Int): Barber? {
        return barbers.value?.find { it.id == id }
    }

    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: BarberRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: BarberRepository().also { instance = it }
            }
    }

    fun fetchList(
        context: Context,
        location: LatLng? = null,
        query: String? = null,
        isFavorite: Boolean = false
    ) {
        val dateFormat = SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
        //val date = dateFormat.format(Date())
        val date = Constants.convertLocalToUTC(Date(), dateFormat)

        val latLng = location ?: App.instance.myLocation
        val params = HashMap<String, String>()
        query?.apply {
            params["search"] = this
        }

        if (latLng != null) {
            params["lat"] = latLng.latitude.toString()
            params["lng"] = latLng.longitude.toString()
        } else {
            params["lat"] = "0"
            params["lng"] = "0"
        }
        params["range"] = "50"
        params["favorite"] = if (isFavorite) "1" else "0"
        params["date"] = date

        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        progressHUD.show()
        APIHandler(context,
            Request.Method.GET,
            Constants.API.barbers,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val jsonArray = result.getJSONArray("result")
                    val items: ArrayList<Barber> = ArrayList()
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val barber = Barber(json)
                        if (barber.id > 0) {
                            items.add(barber)
                        }

                    }

                    barbers.value = items

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


}
