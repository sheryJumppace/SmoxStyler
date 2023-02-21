package com.ibcemobile.smoxstyler.model

import android.content.Context
import androidx.room.Ignore
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.SessionManager
import com.ibcemobile.smoxstyler.model.type.AppointmentType
import com.ibcemobile.smoxstyler.model.type.UserType
import com.ibcemobile.smoxstyler.manager.Constants

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("UNUSED_PARAMETER")
open class Appointment : Serializable {
    var id: Int = -1
    var customerId: Int = -1
    var contactId: Int = -1
    var digitsCode = 1
    var barberId: Int = -1
    var comment: String = ""
    var duration: Int = 0
    var preferredDate: Date = Date()
    var officialDate: Date? = null
    var completedDate: Date? = null
    var strOfficialDate: String = ""
    var strPreferredDate: String = ""
    var username: String = ""
    var image: String = ""
    var status: AppointmentType = AppointmentType.NoShow
    var services = ArrayList<Service>()
    var user: Barber = Barber()
    var isPaid = false
    var isSection = false
    var cancellationFee: Float = 0.0f
    var price:Float= 0.0f
    var timeslot:ArrayList<String>?=ArrayList()
    var appointment_date:String=""
    var start_time_filter:Date?=null


    @Ignore
    private var isDraggable = false

    @Ignore
    fun isDraggable(): Boolean {
        isDraggable = status == AppointmentType.Approved
        return isDraggable
    }
    lateinit var sessionManager: SessionManager
    constructor() : super() {
        id = -1
        comment = ""
        preferredDate = Date()
        officialDate = null
        completedDate = null
        services = ArrayList()
        user = Barber()
        isSection = false
    }

    constructor(json: JSONObject) {
        try {
            if (json.has("id")) {
                this.id = json.getInt("id")
            }
            if (json.has("customer_id")) {
                this.customerId = json.getInt("customer_id")
            }
            if (json.has("contact_id")) {
                this.contactId = json.getInt("contact_id")
            }
            if (json.has("daily_code")) {
                this.digitsCode = json.getInt("daily_code")
            }
            if (json.has("barber_id")) {
                this.barberId = json.getInt("barber_id")
            }
            if (json.has("comment")) {
                this.comment = json.getString("comment")
            }
            if (json.has("duration")) {
                this.duration = json.getInt("duration")
            }

            if (json.has("services")) {
                if (!json.isNull("services")) {
                    val items = json.getJSONArray("services")
                    for (i in 0 until items.length()) {
                        val model = Service(items.getJSONObject(i))
                        this.services.add(model)
                    }
                }
            }

            if (json.has("timeslot") ) {
                if (!json.isNull("timeslot")) {
                    val tim = json.getJSONArray("timeslot")
                    for (i in 0 until tim.length()) {
                        timeslot?.add(tim.getString(i))
                    }
                    this.timeslot=timeslot
                }
            }




           /* if (App.instance.currentUser.id == this.customerId) {
                this.username = user.name
            } else {
                if (json.has("pos")) {
                    val int: Int = json.getInt("pos") + 1
                    if (int < 10) {
                        this.username = "BO$int"
                    } else {
                        this.username = "B$int"
                    }
                }
            }*/



            if (json.has("customer")) {
                if (!json.isNull("customer")) {
                    val json = json.getJSONObject("customer")
                    this.user = Barber(json)
                }
            }

            if (json.has("image")) {

                if (json.getString("image").startsWith("http"))
                    this.image=json.getString("image")
                else
                    this.image = Constants.KUrl.image + json.getString("image")

            }

            if (json.has("price")) {
                if (json.getString("price").isNotEmpty() && json.getString("price")!="null" ) {
                    this.price = json.getString("price").toFloat()
                }
            }
            if (this.contactId == 0 && this.customerId == 0) {
                this.user.firstName = String.format("A%02d", this.digitsCode)
                this.user.lastName = ""
            }else{
                this.user.firstName = json.getString("first_name")
                this.user.lastName = json.getString("last_name")
            }
            this.user.id = this.barberId

            if (json.has("paid")) {
                this.isPaid = json.getInt("paid") == 1
            }

            if (json.has("first_name")) {
                this.username=json.getString("first_name")+" "+json.getString("last_name")
            }
            val dateFormat = SimpleDateFormat(Constants.KDateFormatter.server, Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("Etc/UTC")
            if (json.has("status")) {
                this.status = AppointmentType.valueOf(json.getString("status")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
            }
            if (json.has("cancellation_fee")) {
                this.cancellationFee = json.getDouble("cancellation_fee").toFloat()
            }
            if (json.has("appointment_date")) {
                if (json.getString("appointment_date").isNotEmpty() && json.getString("appointment_date")!="null") {
                    this.appointment_date = Constants.formatDate(json.getString("appointment_date"))!!
                    //val convertToLocalDateTime = json.getString("appointment_date").toDate().formatTo(Constants.KDateFormatter.server)
                    //this.officialDate = dateFormat.parse(convertToLocalDateTime)!!
                }
            }

            if (json.has("preferred_at")) {
                this.strPreferredDate = json.getString("preferred_at")
                try {
                    val convertToLocalDateTime =
                        strPreferredDate.toDate().formatTo(Constants.KDateFormatter.server)
                    this.preferredDate = dateFormat.parse(convertToLocalDateTime)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }
            if (json.has("official_date")) {
                val d = json.getString("official_date")
                if (!(d == "0000-00-00 00:00:00" || d.isEmpty())) {
                    val convertToLocalDateTime =
                    d.toDate().formatTo(Constants.KDateFormatter.server)
                     this.officialDate = dateFormat.parse(convertToLocalDateTime)

                }
            }
            if (json.has("utc_official_at")) {
                val d = json.getString("utc_official_at")
                if (!(d == "0000-00-00 00:00:00" || d.isEmpty())) {
                    this.strOfficialDate = d
                    try {
                        val convertToLocalDateTime =
                            d.toDate().formatTo(Constants.KDateFormatter.server)
                        this.officialDate = dateFormat.parse(convertToLocalDateTime)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }
            }


            if (json.has("completed_at") && !json.getString("completed_at").equals("null")) {
                val strDate = json.getString("completed_at")
                try {
                    this.completedDate = dateFormat.parse(strDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            if (json.has("start_time_filter") && !json.getString("start_time_filter").equals("null")) {
                val strDate = json.getString("appointment_date")+" "+json.getString("start_time_filter")+":00"
                try {
                    this.start_time_filter = dateFormat.parse(strDate)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }



        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun String.toDate(
        dateFormat: String = Constants.KDateFormatter.serverDay,
        timeZone: TimeZone = TimeZone.getTimeZone("UTC")
    ): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)!!
    }

    private fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }
    @Ignore
    fun setDraggable() {
        isDraggable = status == AppointmentType.Approved
    }

    fun getService(context: Context, isShowService: Boolean): String {
        val data = if (App.instance.currentUser.accountType == UserType.Barber || isShowService) {
            services.map { it.title }.joinToString(", ") { it }
        }  else {
            ""
        }
        return data
    }

    fun getDate(): String {
        return if (officialDate != null) {
            val dateFormat =
                SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
            dateFormat.format(officialDate!!)
        } else {
            val dateFormat =
                SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
            dateFormat.format(preferredDate)
        }
    }

    fun getDefaultDate(): String {
        return if (officialDate != null) {
            val dateFormat =
                SimpleDateFormat(Constants.KDateFormatter.defaultDate, Locale.getDefault())
            dateFormat.format(officialDate!!)
        } else {
            val dateFormat =
                SimpleDateFormat(Constants.KDateFormatter.defaultDate, Locale.getDefault())
            dateFormat.format(preferredDate)
        }
    }


    fun getDaySms(): String {
        return if (officialDate != null) {
            val dateFormat =
                SimpleDateFormat(Constants.KDateFormatter.defaultDate, Locale.getDefault())
            val timeFormat = SimpleDateFormat(Constants.KDateFormatter.hourAM, Locale.getDefault())
            dateFormat.format(officialDate!!) + ", " + timeFormat.format(officialDate!!)
                .uppercase(Locale.getDefault())
        } else {
            val dateFormat =
                SimpleDateFormat(Constants.KDateFormatter.defaultDate, Locale.getDefault())
            val timeFormat = SimpleDateFormat(Constants.KDateFormatter.hourAM, Locale.getDefault())
            dateFormat.format(preferredDate) + ", " + timeFormat.format(preferredDate)
                .uppercase(Locale.getDefault())
        }
    }

    fun getDay(): String {
        return if (start_time_filter != null) {
            val dateFormat = SimpleDateFormat(Constants.KDateFormatter.hourAM, Locale.getDefault())
            dateFormat.format(start_time_filter!!)
        } else {
            val dateFormat = SimpleDateFormat(Constants.KDateFormatter.hourAM, Locale.getDefault())
            dateFormat.format(start_time_filter!!)
        }
    }



    fun getSlots():String{
        var slotsBooked=""
        if (timeslot!!.isNotEmpty()){
            val start_time= timeslot?.get(0)
            val end_time= timeslot?.get(timeslot!!.size-1)
            slotsBooked= start_time?.split("-".toRegex())?.first()+" to "+end_time?.split("-".toRegex())?.last()

        }
        return slotsBooked
    }
    override fun toString(): String {
        return "Appointment(id=$id, customerId=$customerId, contactId=$contactId, digitsCode=$digitsCode, barberId=$barberId, comment='$comment', duration=$duration, preferredDate=$preferredDate, officialDate=$officialDate, completedDate=$completedDate, strOfficialDate='$strOfficialDate', strPreferredDate='$strPreferredDate', username='$username', status=$status, services=$services, user=$user, isPaid=$isPaid, isSection=$isSection, cancellationFee=$cancellationFee, isDraggable=$isDraggable)"
    }
}
