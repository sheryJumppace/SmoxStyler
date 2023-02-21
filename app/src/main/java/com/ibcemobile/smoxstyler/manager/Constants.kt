package com.ibcemobile.smoxstyler.manager

import android.webkit.URLUtil
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Constants {

    object KUrl {
        private const val hosting = "http://smoxtrimsetters.com/"
        const val server = "https://smox.jumppace.com/api/v6/" //test
//        const val server = "https://smox.jumppace.com/api/v4.2/" //test
//        const val server = "https://smoxtrimsetters.com/api/v5/" //test
        //const val server = "https://smoxtrimsetters.com/api/v4.1/" //Live
        //const val server = "https://smoxtrimsetters.com/api/v4/" //Live
        //const val server = hosting + "smox-restapi/"
        const val image = hosting + "image/users/"
        const val product = hosting + "image/products/"
        const val terms = hosting + "terms.html"
        const val report = hosting + "api/content/reports.html"
        const val contact = hosting + "api/content/contact.html"
    }


    fun downloadUrl(fileName: String): String {
        return if (URLUtil.isValidUrl(fileName)) {
            fileName
        } else KUrl.image + fileName
    }

    fun downloadUrlOfProduct(fileName: String): String {
        return if (URLUtil.isValidUrl(fileName)) {
            fileName
        } else KUrl.product + fileName
    }
    object API {
        const val signUp = "register"
        const val loginWithSocial = "login_with_social_account"
        const val login = "login"
        const val forgot = "forgot"
        const val appointment = "appointment"
        const val appointment_by_filter = "appointment_by_filter"
        const val appointment_by_date = "appointment_by_date"
        const val appointment_status = "appointment_status"
        const val update_appointment = "update_appointment"
        const val update_business_name = "update_business_name"
        const val phone = "phone"
        const val password = "password"
        const val onboarding_link = "onboarding_link"
        const val onboarding_status = "onboarding_status"
        const val delete_event = "event_delete"
        const val get_category = "get_category"

        const val upload_logo = "upload_logo"
        const val save_key = "update_stripe_detail"
        const val service = "service"
        const val rearrange_service = "rearrange_service"
        const val add_service = "add_service"
        const val fb_Link = "facebook_link"
        const val whatsapp_link = "whatsapp_link"
        const val email_link = "email_link"
        const val user_bio = "user_bio"
        const val address = "address"
        const val review = "review"
        const val appointment_timeslots = "appointment_timeslots"
        const val charge_link = "charge_link"
        const val create_appointment_by_contact = "create_appointment_by_contact"
        const val logout = "logout"
        const val contactus = "contact_us"
        const val BUCKET_NAME = "smox"
        const val AWS_URL = "https://smox.s3.us-east-2.amazonaws.com/"
        const val barbers = "barbers"
        const val barber_address = "barber_address"
        const val user_device = "user_device"
        const val product = "product_new"
        //new product api
        const val category = "product_categories"
        const val hours = "hours_list"
        const val Update_hours = "hours"
        const val delete_holiday = "delete_holiday"
        const val about_us = "about_us"
        const val edit_holiday = "edit_holiday"
        const val add_holiday = "add_holiday"
        const val upnext_option = "upnext_option"
        const val upnext_time = "upnext_time"
        const val send_payment_request = "send_payment_request"
        const val cancellation_fee = "cancellation_fee"
        const val stripe_connect_account = "stripe_connect_account"
        const val transaction_list = "transaction_list"
        const val orders = "orders"
        const val mark_delivered = "mark_delivered"
        const val add_subscription = "add_subscription"
        const val cancelSubscription = "cancelSubscription"
        const val upload_profile = "upload_profile"
        const val PRODUCT = "MyProduct"
        const val SELECTED_DATE = "selectedDate"

        const val PAYMENT_INTENT = "paymentIntent"
        const val PAY_STATUS = "payStatus"
        const val PAY_MESSAGE = "payMessage"
    }

    object KStripe {

        //const val publishableKey: String = "pk_test_5asA0dnWs7gY6uxE0BuDwJFB"  // Company's Stripe Ac. Test Key
        //const val publishableKey: String = "pk_test_9wyRe5j6MuA1kwWFsMJ08CEI"  // Company's Stripe Ac. Test Key
        const val publishableKey: String = "pk_test_R8FVqnYwrx1Ytp7NRn5mjCvp00V9AQp9HS"  // Client's Stripe Ac. Test Key
        const val secretKey: String = "sk_test_mJkKySZpMj4zqYo7wPcGw4qD00tuCf6sae"  // Company's Stripe Ac. Test Key
        //var publishableKey: String = "pk_live_mMJiIOrh3B0TB4jOHIvnbs6o00YgTkisAO"
        //const val secretKey: String = "sk_live_HCBemrxRjDNm4IZX8McC3ikg00JimN8sHJ" //Client's account key

    }


    object KDateFormatter {
        const val local_full_time: String = "dd-MM-yyyy hh:mm a"
        const val local: String = "yyyy-MM-dd hh:mm a"
        const val local_full: String = "dd MMM yyyy HH:mm a"
        const val server: String = "yyyy-MM-dd HH:mm:ss"
        const val serverDay: String = "yyyy-MM-dd"
        const val defaultDate: String = "E, MMM d"
        const val full: String = "E, MMMM d, yyyy"
        const val displayFull: String = "MMM d, yyyy"
        const val displayFullTime: String = "MMM dd, yyyy hh:mm a"
        const val display: String = "MMM d"
        const val hourAM: String = "hh:mm a"
        const val defaultFormat: String = "dd-MM-yyyy"
        const val hourOnly: String = "hha"
        const val hourOnlySpace: String = "hh a"
        const val hourDetail: String = "MMM d, hh:mm a"
        const val second: String = "HH:mm:ss"
        const val event: String = "E. MM-dd-yyyy"

    }

    object KLocalBroadCast {
        const val event: String = "event"
    }

    const val PLACE_KEY_1 = "AIzaSyAc"
    const val PLACE_KEY_2 = "T5skfyEGHN3Mn"
    const val PLACE_KEY_3 = "5h3yubSzqx"
    const val PLACE_KEY_4 = "TYO41lUc"

    fun convertToUTC(localDate: String, formatter: SimpleDateFormat): String {
        val conOfficialDate: Date = formatter.parse(localDate)!!
        formatter.timeZone = TimeZone.getTimeZone("UTC")

        return formatter.format(conOfficialDate)
    }

    fun convertLocalToUTC(localDate: Date, formatter: SimpleDateFormat): String {
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        return formatter.format(localDate)
    }

    @Throws(ParseException::class)
    fun formatDate(
        date: String?
    ): String? {
        val initDate = SimpleDateFormat(KDateFormatter.serverDay, Locale.getDefault()).parse(date!!)
        val formatter = SimpleDateFormat(KDateFormatter.displayFull, Locale.getDefault())
        return formatter.format(initDate!!)
    }


    fun mFormats(date: String): String {
        val initDate = SimpleDateFormat(KDateFormatter.server, Locale.getDefault()).parse(date)
        val formatter = SimpleDateFormat(KDateFormatter.local_full, Locale.getDefault())
        return formatter.format(initDate!!)
    }

    fun mFormatsTo(date: String): String {
        val initDate = SimpleDateFormat(KDateFormatter.server, Locale.getDefault()).parse(date)
        val formatter = SimpleDateFormat(KDateFormatter.displayFull, Locale.getDefault())
        return formatter.format(initDate!!)
    }


    fun getMonthName(i: Int): String {
        var monthName = ""
        when (i) {
            1 -> {
                monthName = "January"
            }
            2 -> {
                monthName = "February"
            }
            3 -> {
                monthName = "March"
            }
            4 -> {
                monthName = "April"
            }
            5 -> {
                monthName = "May"
            }
            6 -> {
                monthName = "June"
            }
            7 -> {
                monthName = "July"
            }
            8 -> {
                monthName = "August"
            }
            9 -> {
                monthName = "September"
            }
            10 -> {
                monthName = "October"
            }
            11 -> {
                monthName = "November"
            }
            12 -> {
                monthName = "December"
            }
            else -> {
                monthName = "January"
            }
        }
        return monthName

    }


}


