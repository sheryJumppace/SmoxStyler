package com.ibcemobile.smoxstyler.viewmodels

import android.content.Context
import androidx.lifecycle.*
import com.ibcemobile.smoxstyler.data.EventRepository
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.Event


class EventListViewModel internal constructor(private val repository: EventRepository) : ViewModel() {

    var isUpdate: MutableLiveData<Boolean> = repository.isUpdated


    fun postEvent(context: Context,event: Event){
        repository.postEvent(context,event)
    }

    fun updateEvent(context: Context,event: Event){
        repository.updateEvent(context,event)
    }
}

class EventListViewModelFactory(
    private val repository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = EventListViewModel(repository) as T
}