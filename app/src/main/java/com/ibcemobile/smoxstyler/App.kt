package com.ibcemobile.smoxstyler

import android.app.Application
import android.text.TextUtils
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.facebook.FacebookSdk
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ibcemobile.smoxstyler.activities.BaseActivity
import com.ibcemobile.smoxstyler.model.Barber
import com.ibcemobile.smoxstyler.model.type.ApplicationStatus
import com.ibcemobile.smoxstyler.utils.ActivityLifecycle

//Chat settings
const val USER_STATUS_TABLE = "User_status"
const val CHAT_ROOM_TABLE = "chatRoom"
const val CHATS_TABLE = "chats"
const val CHAT_ROOM_ID = "chatRoomId"
const val COL_LAST_MESSAGE = "last_message"
const val MESSAGE_COUNT = "message_count"
const val MESSAGE_TYPE_IMAGE = 2
const val MESSAGE_TYPE_TEXT = 1
const val USER_NAME = "userName"
const val CHAT_ROOM = "chatRoom"
const val CHATROOM_ID = "chat_room_id"
const val USER_ID = "userId"


class App : Application() {
    val TAG: String = App::class.java.simpleName
    var currentUser = Barber()
    var currentPage = 0
    var myLocation: LatLng? = null
    var currentActivity: Class<*>? = null
    var unreadEvents = 0
    var applicationStatus = ApplicationStatus.Background
    var requestQueue: RequestQueue? = null
        get() {
            if (field == null) {
                field = Volley.newRequestQueue(applicationContext)
            }
            return field
        }

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(applicationContext)
        FacebookSdk.sdkInitialize(applicationContext)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        //registerActivityLifecycleCallbacks(ActivityLifecycle)
    }


    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        requestQueue?.add(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        requestQueue?.add(req)
    }

    fun cancelPendingRequests(tag: Any) {
        requestQueue?.cancelAll(tag)
    }


    fun snackBarExit(activity: BaseActivity?) {
        Snackbar.make(
            activity!!.findViewById(android.R.id.content),
            activity.getString(R.string.press_again),
            Snackbar.LENGTH_LONG
        )
            .setAction("Exit") {
                activity.finish()
            }
            .setActionTextColor(ContextCompat.getColor(activity, R.color.light_yellow))
            .show()
    }
}