package com.ibcemobile.smoxstyler.data

import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import com.ibcemobile.smoxstyler.model.Orders
import com.ibcemobile.smoxstyler.responses.OrderResponse

class OrdersRepository : BaseObservable() {
    companion object {
        @Volatile
        private var instance: OrdersRepository? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: OrdersRepository().also { instance = it }
            }
    }

    var productOrders: MutableLiveData<OrderResponse> = MutableLiveData()
    var orderUpdate: MutableLiveData<Boolean> = MutableLiveData()


}