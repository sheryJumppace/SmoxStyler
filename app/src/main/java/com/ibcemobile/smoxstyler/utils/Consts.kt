package com.ibcemobile.smoxstyler.utils

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.AddProductActivity
import com.ibcemobile.smoxstyler.activities.OrderActivity
import com.ibcemobile.smoxstyler.activities.PaymentActivity
import com.ibcemobile.smoxstyler.manager.Constants
import java.net.InetAddress
import java.net.NetworkInterface
import java.text.SimpleDateFormat
import java.util.*

const val EXTRA_FCM_MESSAGE: String = "message"
const val ACTION_DATE_CHANGE: String = "data-change"
const val ACTION_NEW_FCM_EVENT: String = "new-push-event"
const val ACTION_CHAT_EVENT: String = "chat-event"
const val ACTION_OPEN_CHAT_EVENT: String = "open-chat-event"
const val ACTION_REFRESH_CHAT: String = "refresh-chat-event"
const val ACTION_BILLING_CONNECT: String = "billing-connect-event"
const val ACTION_SUBSCRIPTION_PROCESS: String = "subscription-process-event"
const val KEY_ADDRESS: String = "address"
const val KEY_ZIP: String = "zip"
const val KEY_ADD_1: String = "address1"
const val KEY_ADD_2: String = "address2"
const val KEY_CITY: String = "city"
const val KEY_STATE: String = "state"
const val U_IMAGE: String = "_image"
const val SELECTED_DATE: String = "selected_date"
const val SELECTED_DATE1: String = "selected_date1"
const val APPOINTMENT_ID: String = "appointment_id"
const val ORDER: String = "order"
const val ANDROID: String = "2"
const val BARBER_ID: String = "barber_id"
const val ADDRESS: String = "address"
const val EVENTS: String = "events"
const val FROM_LAST: String = "from_last"

val PREFERRED_IMAGE_SIZE_FULL = dpToPx(320)

fun showDialogBookedAppointment(
    context: Activity,
    ids: Int,
    mess: String,
    id: Int,
    btmText: String,
    isClick: Boolean
) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.message_dailog_fragment)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)
    val txtApprove = dialog.findViewById<TextView>(R.id.txtBtn)
    txtApprove.text = btmText
    val img = dialog.findViewById<ImageView>(R.id.imgMessage)
    img.setImageResource(id)
    txtMessage.text = mess

    txtApprove.setOnClickListener {
        if (isClick) {
            dialog.dismiss()
            context.finish()
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra("appoint_id", ids)
            context.startActivity(intent)
            context.overridePendingTransition(
                R.anim.activity_enter,
                R.anim.activity_exit
            )
        }


    }
    dialog.show()
}


fun showDialogOrderPlace(
    context: Activity
) {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.order_place_dailog_fragment)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)
    val txtApprove = dialog.findViewById<TextView>(R.id.txtBtn)

    txtApprove.setOnClickListener {
        dialog.dismiss()
        context.finish()
        val intent = Intent(context, OrderActivity::class.java)
        context.startActivity(intent)
        context.overridePendingTransition(
            R.anim.activity_enter,
            R.anim.activity_exit
        )

    }
    dialog.show()
}


fun getCurrentStartTime(): String {
    val calendar = Calendar.getInstance()
    when (calendar.get(Calendar.MINUTE)) {
        in 0..9 -> calendar.set(Calendar.MINUTE, 10)
        in 10..19 -> calendar.set(Calendar.MINUTE, 20)
        in 20..29 -> calendar.set(Calendar.MINUTE, 30)
        in 30..39 -> calendar.set(Calendar.MINUTE, 40)
        in 40..49 -> calendar.set(Calendar.MINUTE, 50)
        in 50..59 -> {
            calendar.set(Calendar.MINUTE, 0)
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }
    }

    Log.e("TAG", "getCurrentStartTime: ${calendar.time}")

    val timeFormat = SimpleDateFormat(Constants.KDateFormatter.hourAM, Locale.getDefault())
    val tt = timeFormat.format(calendar.time)

    Log.e("TAG", "getCurrentStartTime: new time AM PM $tt")

    return tt
}

fun currentDate(): String {
    val completedDate = Date()
    val dateFormat = SimpleDateFormat(Constants.KDateFormatter.server, Locale.getDefault())
    return Constants.convertLocalToUTC(completedDate, dateFormat)
}

fun getIPAddress(useIPv4: Boolean): String {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr: String = addr.hostAddress
                    //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                    val isIPv4 = sAddr.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return sAddr
                    } else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr.substring(
                                0,
                                delim
                            ).uppercase(
                                Locale.getDefault()
                            )
                        }
                    }
                }
            }
        }
    } catch (ex: Exception) {
    } // for now eat exceptions
    return ""
}

fun showReminder(activity: Activity) {
    val dialog = Dialog(activity)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.reminder_dailog)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    val txtDecline = dialog.findViewById<TextView>(R.id.txtDecline)
    val txtApprove = dialog.findViewById<TextView>(R.id.txtApprove)
    txtApprove.setOnClickListener {
        val packagesName = "com.smox.smoxuser"
        try {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packagesName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            activity.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packagesName")
                )
            )
        }
        dialog.dismiss()
    }
    txtDecline.setOnClickListener {
        dialog.dismiss()
    }
    dialog.show()
}

fun getSmsTodayYestFromMilli(msgTimeMillis: Long): String {
    val messageTime = Calendar.getInstance()
    messageTime.timeInMillis = msgTimeMillis
    val now = Calendar.getInstance()
    val strTimeFormat = "h:mm aa"
    val strDateFormat = "EEE',' MMM d y',' h:mm aa"
    return if (now[Calendar.DATE] === messageTime[Calendar.DATE] &&
        now[Calendar.MONTH] === messageTime[Calendar.MONTH]
        &&
        now[Calendar.YEAR] === messageTime[Calendar.YEAR]
    ) {
        "today at " + DateFormat.format(strTimeFormat, messageTime)
    } else if (now[Calendar.DATE] - messageTime[Calendar.DATE] === 1
        &&
        now[Calendar.MONTH] === messageTime[Calendar.MONTH]
        &&
        now[Calendar.YEAR] === messageTime[Calendar.YEAR]
    ) {
        "yesterday at " + DateFormat.format(strTimeFormat, messageTime)
    } else {
        "" + DateFormat.format(strDateFormat, messageTime)
    }
}