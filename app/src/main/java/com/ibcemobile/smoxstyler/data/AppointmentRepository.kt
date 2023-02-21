package com.ibcemobile.smoxstyler.data

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.Event
import com.ibcemobile.smoxstyler.model.Service
import com.ibcemobile.smoxstyler.model.type.AppointmentType
import com.ibcemobile.smoxstyler.utils.currentDate
import com.kaopiz.kprogresshud.KProgressHUD

import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository module for handling data operations.
 */
open class AppointmentRepository : BaseObservable() {
    var appointments: MutableLiveData<List<Appointment>> = MutableLiveData()
    var appointmentsByDate: MutableLiveData<List<Appointment>> = MutableLiveData()
    var appointmentsByDatePend: MutableLiveData<List<Appointment>> = MutableLiveData()
    var appointmentsByDateLast: MutableLiveData<List<Appointment>> = MutableLiveData()
    var events: MutableLiveData<List<Event>> = MutableLiveData()
    var updateAppointment: MutableLiveData<Boolean> = MutableLiveData()
    var selectedAppointment: MutableLiveData<Appointment> = MutableLiveData()
    var staus: MutableLiveData<String> = MutableLiveData()
    var isSuccessToSentPaymentRequest: MutableLiveData<Boolean> = MutableLiveData()
    var isCheckCompleted = false
    protected lateinit var sessionManager: SessionManager

    fun getAppointmentDetails(context: Context, id: Int) {
        fetchAppointment(context, id)
    }

    fun updateAppointment(context: Context,appointment: Appointment,type: AppointmentType) {
        val message = when (type) {
            AppointmentType.Completed -> String.format(
                "%s appointment with %s has been completed",
                appointment.services[0].title,
                appointment.user.firstName
            )

            AppointmentType.Approved -> String.format(
                "%s appointment is Approved by %s",
                appointment.services[0].title,
                appointment.user.firstName
            )

            AppointmentType.Canceled -> String.format(
                "%s appointment is cancelled by %s",
                appointment.services[0].title,
                appointment.user.firstName
            )
            else -> ""
        }
        val tag = Constants.API.appointment_status + "/" + appointment.id.toString()
        val params = HashMap<String, String>()
        params["status"] = type.name.lowercase(Locale.getDefault())
        params["receiver"] = appointment.customerId.toString()
        params["message"] = message
        params["date"] = currentDate()
        params["fee"] = appointment.cancellationFee.toString()
        params["barber_id"] = appointment.barberId.toString()

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
            tag,
            params,
            object : APIHandler.NetworkListener {
                @SuppressLint("SetTextI18n")
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val error = result.getBoolean("error")
                    updateAppointment.value = error

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
            })
    }

    fun fetchList(context: Context, date: String, barberID: Int) {
        sessionManager = SessionManager.getInstance(context)
        val params = HashMap<String, String>()
        params["date"] = date
        params["timezone"] = TimeZone.getDefault().id

       /* val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        if (!(context as Activity).isFinishing)
            progressHUD.show()*/

        APIHandler(
            context,
            Request.Method.GET,
            Constants.API.appointment_by_date + "/" + barberID,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    //progressHUD.dismiss()
                    parseResult(result,context)


                }

                override fun onFail(error: String?) {
                    //progressHUD.dismiss()
                    Toast.makeText(
                        context.applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()

                }
            })
    }

    private fun parseResult(result: JSONObject, context: Context) {
        val jsonArray = result.getJSONArray("result")
        val items: ArrayList<Appointment> = ArrayList()
        for (i in 0 until jsonArray.length()) {
            val json = jsonArray.getJSONObject(i)
            val appointment = Appointment(json)
            items.add(appointment)
        }
        appointments.value = items
        try {
            if (result.has("up_next") && !result.isNull("up_next")) {
                val upNext = result.getJSONObject("up_next")
                if (upNext.has("status")) {
                    staus.value = upNext.getString("status")
                }
            }
        } catch (e: JSONException) {

        }

        if (result.has("sub_data")) {
            val subData = result.getJSONObject("sub_data")
            sessionManager.isSubscribed = subData.getBoolean("is_subscribed")
            sessionManager.subscription_enddate = subData.getString("subscription_enddate")
        }


        if (result.has("events")) {
            val evs = result.getJSONArray("events")
            val its: ArrayList<Event> = ArrayList()
            for (i in 0 until evs.length()) {
                val json = evs.getJSONObject(i)
                val item = Event(json)
                its.add(item)
            }
            events.value = its
        }
    }

    fun sendPaymentRequest(context: Context, appointment: Appointment) {
        if (isCheckCompleted) return
        isCheckCompleted = true


        val services = appointment.services.map { it.title }.joinToString("#||#") { it }
        val subPrices = appointment.services.map { it.price.toString() }.joinToString(",") { it }
        var amount = 0.0f
        for (s in appointment.services) {
            amount += s.price
        }
        val params = HashMap<String, String>()
        params["amount"] = amount.toString()
        params["sub_prices"] = subPrices
        params["services"] = services

        val dateFormat = SimpleDateFormat(Constants.KDateFormatter.displayFull, Locale.getDefault())
        //val date = dateFormat.format(Date())
        val date = Constants.convertLocalToUTC(Date(), dateFormat)
        params["date"] = date

        APIHandler(
            context,
            Request.Method.PUT,
            Constants.API.send_payment_request + "/" + appointment.id,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    //progressHUD.dismiss()
                    Toast.makeText(
                        context.applicationContext,
                        "Success to send the Payment Request", Toast.LENGTH_LONG
                    ).show()
                    isSuccessToSentPaymentRequest.value = true

                }

                override fun onFail(error: String?) {
                    //progressHUD.dismiss()
                    isCheckCompleted = false
                    Toast.makeText(
                        context.applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun fetchAppointmentByDate(context: Context, page:Int, date:String, type: AppointmentType) {
        val params = HashMap<String, String>()
        params["order_by"] = type.name
        params["page"] = page.toString()
        params["date"] = date
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
            Constants.API.appointment_by_filter,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val jsonArray = result.getJSONArray("result")
                    val items: ArrayList<Appointment> = ArrayList()
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        json.put("pos",i)
                        val appointment = Appointment(json)
                        items.add(appointment)
                    }
                    when(type){
                        AppointmentType.Pending->{
                            appointmentsByDatePend.value = items
                        }
                        AppointmentType.Completed->{
                            appointmentsByDateLast.value = items
                        }
                        else-> {
                            appointmentsByDate.value = items
                        }
                    }
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

    private fun fetchAppointment(context: Context, id: Int) {
        val params = HashMap<String, String>()
        val tab = Constants.API.appointment + "/" + id

        APIHandler(
            context,
            Request.Method.GET,
            tab,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    //progressHUD.dismiss()
                    val json = result.getJSONObject("result")
                    selectedAppointment.value = Appointment(json)

                }

                override fun onFail(error: String?) {
                    ///progressHUD.dismiss()
                    Toast.makeText(
                        context.applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun fetchServices(context: Context, appointment: Appointment) {
        val params = HashMap<String, String>()
        params["ids"] = appointment.services.map { it.id }.joinToString(",") { it.toString() }


        val progressHUD = KProgressHUD(context)
        progressHUD.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
        progressHUD.show()

        APIHandler(
            context,
            Request.Method.GET,
            Constants.API.service,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    val jsonArray = result.getJSONArray("result")
                    val items: ArrayList<Service> = ArrayList()
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val service = Service(json)
                        items.add(service)
                    }
                    appointment.services = items
                    selectedAppointment.value = appointment
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


    companion object {
        @Volatile
        private var instance: AppointmentRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: AppointmentRepository().also { instance = it }
            }
    }
}
