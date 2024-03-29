package com.ibcemobile.smoxstyler.manager

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class Singleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: Singleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Singleton(context).also {
                    INSTANCE = it
                }
            }
    }
    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

}
