package com.ibcemobile.smoxstyler.manager

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.MainActivity
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.activities.*
import com.ibcemobile.smoxstyler.model.Verify
import com.ibcemobile.smoxstyler.utils.PushNotificationUtils

import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private enum class PushType {
        Appointment,
        Review,
        Account,
        Payment,
        Event
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title: String?
        val message: String?
        if (remoteMessage.notification != null) {
            title = remoteMessage.notification!!.title
            message = remoteMessage.notification!!.body
            Log.e(TAG, "Notification Body: " + remoteMessage.notification!!.body!!)
        } else {
            title = remoteMessage.from
            message = remoteMessage.data["message"]
            Log.e(TAG, "QB Notification Body: " + remoteMessage.data)
        }
        if (title == null || message == null) {
            return
        }
        Log.e(
            "Notification data:- ",
            "Title :- $title \t Message:- $message \n Remote data:- ${remoteMessage.data}"
        )
        handleNotification(title, message, remoteMessage.data)
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        SessionManager.getInstance(applicationContext).deviceToken = token
    }

    private fun handleNotification(title: String, message: String, data: Map<String, String>) {

        if (data.isNotEmpty()) {
            if (data["message"] != null) {
                showNotificationMessage("", message, Intent(this, MainActivity::class.java))
                return
            }
            try {
                val json = JSONObject(data.toString())
                if (json.has("type")) {
                    Log.e(TAG, "type of notification ${json.getString("type")}")
                    when (json.getString("type")) {
                        PushType.Appointment.name.lowercase(Locale.getDefault()) -> {
                            val appointmentId = json.getInt("appointment_id")
                            val resultIntent = Intent(applicationContext, AppointmentDetailActivity::class.java)
                            resultIntent.putExtra("appoint_id", appointmentId)
                            resultIntent.putExtra("isFromNoti", true)
                            Log.e(TAG, "Title:- $title , Message :- $message")
                            showNotificationMessage(title, message, resultIntent)
                            return
                        }
                        PushType.Review.name.lowercase(Locale.getDefault()) -> {
//                            val reviewId = json.getInt("review_id")
                            val resultIntent =
                                Intent(applicationContext, ReviewActivity::class.java)
                            resultIntent.putExtra("barber_id", App.instance.currentUser.id)
                            showNotificationMessage(title, message, resultIntent)
                            return
                        }
                        PushType.Account.name.lowercase(Locale.getDefault()) -> {
                            val isVerified = json.getBoolean("is_verified")
                            if (isVerified) {
                                val builder = AlertDialog.Builder(applicationContext)
                                builder.setTitle("Congratulations!")
                                builder.setMessage("Your account has been approved successfully.")
                                builder.setPositiveButton("OK", null)
                                builder.show()
                            } else {
                                val verify = Verify()
                                verify.requiredFields = json.getString("fields")
                                verify.accountToken = json.getString("account_token")

                                val resultIntent = Intent(
                                    applicationContext,
                                    SignInActivity::class.java
                                )
                                resultIntent.putExtra("verify", verify)
                                showNotificationMessage(title, message, resultIntent)
                            }

                            return
                        }
                        PushType.Payment.name.lowercase(Locale.getDefault()) -> {
                            val appointmentId = json.getInt("appointment_id")
                            val resultIntent =
                                Intent(applicationContext, PaymentActivity::class.java)
                            resultIntent.putExtra("appoint_id", appointmentId)
                            showNotificationMessage(title, message, resultIntent)
                            return
                        }
                        PushType.Event.name.lowercase(Locale.getDefault()) -> {
//                            val pushNotification = Intent(Constants.KLocalBroadCast.event)
//                            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification)
                            val unReadCount = App.instance.unreadEvents + 1
                            App.instance.unreadEvents = unReadCount

                            ObservingService.getInstance()
                                .postNotification(Constants.KLocalBroadCast.event, "")

                            val resultIntent =
                                Intent(applicationContext, AddEventActivity::class.java)
                            showNotificationMessage(title, message, resultIntent)
                            return
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: " + e.message)
            }

        }

        showNotificationMessage(title, message, Intent(this, AppointmentDetailActivity::class.java))
    }

    /**
     * Showing notification with text only
     */
    @SuppressLint("SimpleDateFormat")
    private fun showNotificationMessage(
        title: String,
        message: String,
        intent: Intent
    ) {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat(Constants.KDateFormatter.display)
        val formattedDate = df.format(c.time)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        PushNotificationUtils(applicationContext)
            .showNotificationMessage(title, message, formattedDate, intent)
    }

    companion object {
        private val TAG = "MyFirebaseMsgService"
    }
}
