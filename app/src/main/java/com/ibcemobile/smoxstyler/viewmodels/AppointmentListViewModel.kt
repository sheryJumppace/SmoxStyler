package com.ibcemobile.smoxstyler.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.Event
import com.ibcemobile.smoxstyler.model.type.AppointmentType
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.kaopiz.kprogresshud.KProgressHUD
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class AppointmentListViewModel internal constructor(private val repository: AppointmentRepository) : ViewModel() {

    var appointments: MutableLiveData<List<Appointment>> = repository.appointments
    var appointmentsByDate: MutableLiveData<List<Appointment>> = repository.appointmentsByDate
    var appointmentsByDatePend: MutableLiveData<List<Appointment>> = repository.appointmentsByDatePend
    var appointmentsByDateLast: MutableLiveData<List<Appointment>> = repository.appointmentsByDateLast
    var events: MutableLiveData<List<Event>> = repository.events
    var staus: MutableLiveData<String> = repository.staus
    var updateAppointment: MutableLiveData<Boolean> = repository.updateAppointment

    fun fetchList(context: Context, date: String, barberId: Int) {
        repository.fetchList(context, date, barberId)
    }

    fun getAppointmentsByDate(context: Context,page:Int,type: AppointmentType,date:String) {
        return repository.fetchAppointmentByDate(context,page,date,type)
    }

    fun updateAppointment(context: Context,appointment: Appointment,type: AppointmentType){
        repository.updateAppointment(context,appointment,type)
    }


}
