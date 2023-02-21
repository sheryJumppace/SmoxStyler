package com.ibcemobile.smoxstyler.activities

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.bumptech.glide.Glide
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.Day
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.BuildConfig
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.CategorySelectorAdapter
import com.ibcemobile.smoxstyler.adapter.ServiceBookingAdapter
import com.ibcemobile.smoxstyler.adapter.TimeSlotAdapterNew
import com.ibcemobile.smoxstyler.data.BookingRepository
import com.ibcemobile.smoxstyler.databinding.ActivityBookAppointmentBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Category
import com.ibcemobile.smoxstyler.model.Service
import com.ibcemobile.smoxstyler.model.TimeSlotResult
import com.ibcemobile.smoxstyler.utils.PhoneUtil.GetCountryZipCode
import com.ibcemobile.smoxstyler.utils.currentDate
import com.ibcemobile.smoxstyler.utils.getCurrentStartTime
import com.ibcemobile.smoxstyler.utils.getIPAddress
import com.ibcemobile.smoxstyler.utils.showDialogBookedAppointment
import com.ibcemobile.smoxstyler.viewmodels.BookingViewModel
import com.ibcemobile.smoxstyler.viewmodels.BookingViewModelFactory
import org.joda.time.DateTime
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class BookAppointmentActivity : BaseActivity(), DatePickerListener,
    CategorySelectorAdapter.CategorySelectActions, TimeSlotAdapterNew.ItemClickListener,
    ServiceBookingAdapter.ItemClickListener {
    private lateinit var binding: ActivityBookAppointmentBinding
    private var servicesByCategory: ArrayList<Category> = ArrayList()
    private var itemsSelected: ArrayList<Category> = ArrayList()
    private var serviceSelectedList = arrayListOf<Service>()
    private var mainServiceList = arrayListOf<Service>()
    private lateinit var serviceAdapter: ServiceBookingAdapter
    private lateinit var bookingViewModel: BookingViewModel
    private var selectedContactName: String? = null
    var selectedContact: String? = null
    private val dateSelectCalender = Calendar.getInstance()
    var slotTime: TimeSlotResult? = null
    var selectedDate: String? = null
    var dateSelected = Date()
    private val slotAdapter = TimeSlotAdapterNew()
    private var timeSlotList = arrayListOf<TimeSlotResult>()
    private var userId: String? = "0"
    private var ids: String? = null
    private var isRegisterUser: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.fabAdd.setOnClickListener {
            pickFromContacts()
        }
        binding.datePicker.setListener(this).showTodayButton(false).setDays(30)
            .setDateSelectedColor(resources.getColor(R.color.light_yellow))
            .setTodayDateBackgroundColor(resources.getColor(R.color.light_yellow)).init()

//      try {
          var dateSelected: DateTime? = intent.getSerializableExtra(Constants.API.SELECTED_DATE) as DateTime
          binding.datePicker.setDate(dateSelected)
//      }catch (ex:Exception){
//          ex.printStackTrace()
//      }


        val bookingFactory = BookingViewModelFactory(BookingRepository.getInstance())
        bookingViewModel = ViewModelProvider(this, bookingFactory).get(BookingViewModel::class.java)


        val timeSelected = arrayListOf<String>()
        binding.btnNext.setOnClickListener {
            timeSelected.clear()
            for (slot in timeSlotList) {
                if (slot.isSelected) timeSelected.add(slot.timeslot)
            }
            var totalDuration = 0
            var totalAmount = 0f
            for (item in serviceSelectedList) {
                if (item.isSelected.get()) {
                    totalDuration += item.duration
                    totalAmount += item.price
                    ids = serviceSelectedList.map { it.id.toString() }.joinToString(",") { it }
                }
            }
            totalAmount *= 100
            if (totalDuration > 0 && timeSelected.isNotEmpty()) {
                bookAppointment(totalAmount, timeSelected, totalDuration.toString(), ids!!)
            }
        }

        initServiceObserver()
        initSlotObserver()

        bookingViewModel.contactUser.observe(this, androidx.lifecycle.Observer {
            Log.e(TAG, "onCreate: $it")
            if (it != null) {
                binding.txtNumber.text = it.phone.toString()
                binding.txtName.text = it.first_name + " " + it.last_name
                Glide.with(this@BookAppointmentActivity).load(it.image).placeholder(R.drawable.logo)
                    .into(binding.imageCircle)
                userId = it.id.toString()
            } else {
                isRegisterUser = false
                binding.txtNumber.text = selectedContact
                binding.txtName.text = selectedContactName
            }

        })

        bookingViewModel.fetchServices(this, app.currentUser.id.toString())
    }

    private fun initServiceObserver() {

        binding.rvService.layoutManager = LinearLayoutManager(this)
        serviceAdapter = ServiceBookingAdapter(false)
        serviceAdapter.setItemClickListener(this@BookAppointmentActivity)
        binding.rvService.adapter = serviceAdapter
        val categoryAdapter = CategorySelectorAdapter(
            this@BookAppointmentActivity, this@BookAppointmentActivity, false
        )
        binding.getCategory.layoutManager = LinearLayoutManager(
            this@BookAppointmentActivity, LinearLayoutManager.HORIZONTAL, false
        )
        binding.getCategory.adapter = categoryAdapter
        bookingViewModel.services.observe(this, androidx.lifecycle.Observer {
            itemsSelected.clear()
            servicesByCategory.addAll(it)
            categoryAdapter.doRefresh(servicesByCategory)
            getService(0)
        })
    }

    private fun initSlotObserver() {
        binding.rvTimeSlot.adapter = slotAdapter
        slotAdapter.setItemClickListener(this)
        bookingViewModel.timeSlotList.observe(this, androidx.lifecycle.Observer {
            progressHUD.dismiss()
            timeSlotList = it as ArrayList<TimeSlotResult>
            slotAdapter.submitList(it)
        })
    }

    private fun getTimeIn24(time: String): String {
        val date12Format = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date24Format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return date24Format.format(date12Format.parse(time)!!)
    }

    private fun bookAppointment(
        amount: Float, times: ArrayList<String>, duration: String, services: String
    ) {
        val serverDate = SimpleDateFormat(Constants.KDateFormatter.server, Locale.getDefault())
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "Select a contact for Book Appointment", Toast.LENGTH_LONG).show()
            return
        }
        if (ids.isNullOrEmpty()) {
            Toast.makeText(this, "Select a service", Toast.LENGTH_LONG).show()
            return
        }
        if (times.isNullOrEmpty()) {
            Toast.makeText(this, "Select a Time Slot for Book Appointment", Toast.LENGTH_LONG)
                .show()
            return
        }
        if (amount == 0f) {
            Toast.makeText(this, "Select a service", Toast.LENGTH_LONG).show()
            return
        }
        val arr = JsonArray()
        for (item in times) {
            arr.add(item)
        }
        val jsonObject = JsonObject()
        val postUrl = if (isRegisterUser) {
            jsonObject.addProperty("amount", amount)
            jsonObject.addProperty("only_date", selectedDate)
            jsonObject.addProperty("appointment_id", "-1")
            Constants.API.charge_link
        } else {
            jsonObject.addProperty("price", amount)
            jsonObject.addProperty("phone", binding.txtNumber.text.toString())
            jsonObject.addProperty("name", binding.txtName.text.toString())
            jsonObject.addProperty("image", "")
            jsonObject.addProperty("customer_id", "0")
            jsonObject.addProperty("comment", binding.etComment.text.toString())
            jsonObject.addProperty("date_only", selectedDate)
            Constants.API.create_appointment_by_contact
        }

        jsonObject.addProperty("barber_id", app.currentUser.id.toString())
        jsonObject.addProperty("user_id", userId)
        jsonObject.addProperty("date", selectedDate + " " + getTimeIn24(times[0].split("-")[0]))
        jsonObject.addProperty("utc_date", Constants.convertLocalToUTC(Date(), serverDate))
        jsonObject.addProperty("message", "Appointment Booked Successfully")
        jsonObject.add("timeslots", arr)
        jsonObject.addProperty("services", services)
        jsonObject.addProperty("duration", duration)
        jsonObject.addProperty("app_type", "Android Smox Pro")
        jsonObject.addProperty("app_version", "v-" + BuildConfig.VERSION_NAME)
        jsonObject.addProperty("device_date", currentDate())
        jsonObject.addProperty("user_timezone", TimeZone.getDefault().id)
        jsonObject.addProperty("user_ip", getIPAddress(true))
        progressHUD.show()
        // => {"amount":3000.0,"only_date":"2022-05-26","appointment_id":"-1","barber_id":"11","user_id":"618","date":"2022-05-26 16:20:00","utc_date":"2022-05-24 11:21:26","message":"Appointment Booked Successfully","timeslots":["04:20 PM-04:30 PM","04:30 PM-04:40 PM"],"services":"87","duration":"20","app_type":"Android Smox Pro","app_version":"v-1.7.4","device_date":"2022-05-24 11:21:26","user_ip":"10.117.245.210"}
        val params = JSONObject(jsonObject.toString())
        APIHandler(
            applicationContext,
            Request.Method.POST,
            postUrl,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    if (result.has("error")) {
                        if (!result.getBoolean("error")) {
                            val ids = result.getInt("result")
                            binding.btnNext.isClickable = false
                            binding.btnNext.isEnabled = false
                            showDialogBookedAppointment(
                                this@BookAppointmentActivity,
                                ids,
                                "Your Appointment has been schedule successfully",
                                R.drawable.ic_baseline_check_circle_24,
                                "Send payment link",
                                true
                            )
                        } else {
                            Toast.makeText(
                                applicationContext, result.getString("message"), Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext, error, Toast.LENGTH_LONG
                    ).show()
                }
            },
            "json"
        )
    }


    override fun onDateSelected(selectedd: DateTime) {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val localDateFormat =
            SimpleDateFormat(Constants.KDateFormatter.local_full_time, Locale.getDefault())
        val curDateFormat =
            SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
        val date = inputFormat.parse(selectedd.toString())
        val selectDate = outputFormat.format(date!!)
        val timeFormat = SimpleDateFormat(Constants.KDateFormatter.hourAM, Locale.getDefault())
        dateSelected = outputFormat.parse(selectDate)!!
        val currDate = outputFormat.parse(outputFormat.format(Date()))
        selectedDate = curDateFormat.format(dateSelected)
        dateSelectCalender.time = date
        //Log.e("TAG", "onDateSelected: formatted date : $selectDate $dateSelected $currDate")
        if (dateSelected >= currDate) {
            binding.datePicker.setDateSelectedTextColor(
                ContextCompat.getColor(
                    this, R.color.light_yellow
                )
            )
            var newStartTime = "-"
            var newEndTime = "-"
            if (sessionManager.userDataOpenDays == "") {
                Toast.makeText(
                    applicationContext, "Set your Open days", Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this, HourOperationActivity::class.java)
                startActivity(intent)
            } else {
                app.currentUser.openDays =
                    app.currentUser.getOpenDays(JSONObject(sessionManager.userDataOpenDays!!))
                val openDay =
                    app.currentUser.openDays[dateSelectCalender.get(Calendar.DAY_OF_WEEK) - 1]
                if (!openDay.isClosed) {
                    if (dateSelected == currDate) {
                        val barberStartDateTime =
                            localDateFormat.parse(selectDate + " " + openDay.startTime)
                        var barberEndTime =
                            localDateFormat.parse(selectDate + " " + openDay.endTime)
                        val cal = Calendar.getInstance()
                        cal.time = barberEndTime!!
                        cal.add(Calendar.MINUTE, -10)// close the store before 10 min
                        barberEndTime = cal.time
                        //val curTime = Date()
                        val timeonly = timeFormat.format(Date())
                        val curTime: Date? = localDateFormat.parse("$selectDate $timeonly")
                        if (curTime!! > barberStartDateTime && curTime < barberEndTime) {
                            newStartTime = getCurrentStartTime()
                            newEndTime = openDay.endTime
                        } else if (curTime <= barberStartDateTime) {
                            newStartTime = openDay.startTime
                            newEndTime = openDay.endTime
                        }
                    } else {
                        newStartTime = openDay.startTime
                        newEndTime = openDay.endTime
                    }
                }

                bookingViewModel.getTimeSlots(
                    applicationContext,
                    date,
                    newStartTime,
                    newEndTime,
                    barber_Id = app.currentUser.id.toString(),
                    "-1"
                )
            }

        } else {
            binding.datePicker.setDateSelectedTextColor(ContextCompat.getColor(this, R.color.black))
            binding.datePicker.setDate(DateTime())
            binding.datePicker.onDateSelected(Day(DateTime()))

            Toast.makeText(this, "Select valid date", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_CONTACT -> {
                    var cursor: Cursor? = null
                    try {
                        val locale: String = GetCountryZipCode(this)
                        val uri: Uri? = data?.data
                        cursor = contentResolver.query(uri!!, null, null, null, null)
                        cursor?.moveToFirst()
                        val phoneIndex: Int =
                            cursor!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        selectedContactName =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                        selectedContact = cursor.getString(phoneIndex)
                        if (selectedContact!!.startsWith("+")) {
                            val jsonObject = JsonObject()
                            jsonObject.addProperty("phone", selectedContact!!.replace(" ", ""))
                            bookingViewModel.getContactDetail(this, jsonObject)

                        } else {
                            val jsonObject = JsonObject()
                            jsonObject.addProperty(
                                "phone", "+" + locale + selectedContact!!.replace(" ", "")
                            )
                            bookingViewModel.getContactDetail(this, jsonObject)
                        }
                        cursor.close()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onItemClick(pos: Int) {
        getService(pos)
    }

    override fun onTimeClick(slot: TimeSlotResult) {
        slotTime = slot
    }

    private fun getService(position: Int) {
        mainServiceList.clear()
        serviceAdapter.submitList(servicesByCategory[position].services)
        mainServiceList.addAll(servicesByCategory[position].services)
        if (servicesByCategory[position].services.isNullOrEmpty()) {
            binding.tvNotFound.visibility = View.VISIBLE
        } else {
            binding.tvNotFound.visibility = View.GONE
        }
    }

    override fun onServiceItemClick(view: View, position: Int, service: Service) {
        var duration = 0
        val service = mainServiceList[position]
        val index = serviceSelectedList.indexOfFirst { it.id == service.id }

        if (index == -1) {
            serviceSelectedList.add(mainServiceList[position])
        } else serviceSelectedList.remove(service)


        for (item in serviceSelectedList) {

            if (item.isSelected.get()) {
                duration += item.duration
                // ids = serviceSelectedList.map { it.id.toString() }.joinToString(",") { it }
            }
        }
        var canSelectSlotCount = 0
        if (duration > 0) {
            canSelectSlotCount = duration / 10
            val oddDuration = duration % 10
            if (oddDuration != 0) {
                canSelectSlotCount++
            }
        }
        slotAdapter.canSelectSlotCount(canSelectSlotCount)
    }


    private fun pickFromContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(
                Manifest.permission.READ_CONTACTS,
                getString(R.string.permission_read_contacts_rationale),
                REQUEST_READ_CONTACTS_PERMISSION
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(intent, PICK_CONTACT)
        }
    }

    private fun requestPermission(permission: String, rationale: String, requestCode: Int) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showAlertDialog(
                getString(R.string.permission_title_rationale),
                rationale,
                DialogInterface.OnClickListener { _, _ ->
                    ActivityCompat.requestPermissions(
                        this, arrayOf(permission), requestCode
                    )
                },
                getString(R.string.ok),
                null,
                getString(R.string.cancel)
            )
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    companion object {
        const val TAG = "BookAppointmentActivity"
        private const val PICK_CONTACT = 101
        private const val REQUEST_READ_CONTACTS_PERMISSION = 102
    }
}