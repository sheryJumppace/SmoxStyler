package com.ibcemobile.smoxstyler.viewmodels

import android.content.Context
import androidx.lifecycle.*
import com.ibcemobile.smoxstyler.data.UpNextOptionRepository
import com.ibcemobile.smoxstyler.model.UpNextOption

class UpNextOptionListViewModel internal constructor(private val repository: UpNextOptionRepository) : ViewModel() {
    var options: MutableLiveData<List<UpNextOption>> = repository.options
    fun fetchList(context: Context){
        repository.fetchList(context = context)
    }
    fun getOption(position:Int): UpNextOption? {
        return repository.getOption(position)
    }
    fun addOption(context: Context, title:String) {
        repository.addOption(context, title)
    }
    fun deleteOption(context: Context, position:Int) {
        repository.deleteOption(context, position)
    }
}

class UpNextOptionListViewModelFactory(
    private val repository: UpNextOptionRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = UpNextOptionListViewModel(repository) as T
}