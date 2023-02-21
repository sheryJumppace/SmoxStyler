package com.ibcemobile.smoxstyler.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.model.type.AppointmentType


class AppointmentViewModel internal constructor(private val repository: AppointmentRepository, private val appointmentId: Int) : ViewModel() {

    var appointmentDetail: MutableLiveData<Appointment> = repository.selectedAppointment
    var updateAppointment: MutableLiveData<Boolean> = repository.updateAppointment

    fun getAppointmentDetail(context: Context){
        repository.getAppointmentDetails(context, appointmentId)
    }

    fun fetchAppointmentByDate(context: Context,page:Int,date:String,type:AppointmentType){
        repository.fetchAppointmentByDate(context,page,date,type)
    }

    fun updateAppointment(context: Context,appointment: Appointment,type: AppointmentType){
        repository.updateAppointment(context,appointment,type)
    }
    fun sendPaymentRequest(context: Context, appointment: Appointment){
        repository.sendPaymentRequest(context, appointment)
    }
}

class AppointmentViewModelFactory(
    private val repository: AppointmentRepository,
    private val appointmentId: Int
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = AppointmentViewModel(repository, appointmentId) as T
}