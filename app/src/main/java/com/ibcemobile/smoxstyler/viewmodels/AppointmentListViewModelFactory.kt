
package com.ibcemobile.smoxstyler.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.ibcemobile.smoxstyler.data.AppointmentRepository

class AppointmentListViewModelFactory(
    private val repository: AppointmentRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = AppointmentListViewModel(repository) as T
}
