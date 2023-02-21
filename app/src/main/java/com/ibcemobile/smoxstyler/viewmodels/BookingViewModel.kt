package com.ibcemobile.smoxstyler.viewmodels

import android.content.Context
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.data.BookingRepository
import com.ibcemobile.smoxstyler.model.Category
import com.ibcemobile.smoxstyler.model.Service
import com.ibcemobile.smoxstyler.model.SmoxUser
import com.ibcemobile.smoxstyler.model.TimeSlotResult
import com.ibcemobile.smoxstyler.responses.ContactResponse
import org.json.JSONObject
import java.util.*


class BookingViewModel internal constructor(
    private val repository: BookingRepository
) : ViewModel() {
    var contactUser: MutableLiveData<ContactResponse.Result.ContactUser> = repository.getcontacts
    var timeSlotList: MutableLiveData<List<TimeSlotResult>> = repository.timeSlotList
    var services: MutableLiveData<List<Category>> = repository.services



    fun fetchServices(context: Context,barber_Id: String){
        repository.fetchServices(context,barber_Id)
    }

    fun sendReorderService(context: Context, data: JSONObject) {
        repository.sendReorderService(context, data)
    }

    fun deleteServiceFromServer(context: Context, serviceId:Int){
        repository.deleteServiceFromServer(context = context, serviceId = serviceId)
    }


    fun getContactDetail(context: Context,jsonObject: JsonObject){
        repository.getBarberProfileByContact(context,jsonObject)
    }

    fun getTimeSlots(context: Context, day: Date, newStartTime: String, newEndTime: String, barber_Id:String, appointmentId:String){
        repository.fetchSlots(context,day,newStartTime,newEndTime,barber_Id, appointmentId)
    }




}

class BookingViewModelFactory(
    private val repository: BookingRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        BookingViewModel(repository) as T
}