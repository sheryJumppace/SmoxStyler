package com.ibcemobile.smoxstyler.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.github.jhonnyx2012.horizontalpicker.Day
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.MainActivity
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.ServiceBookingAdapter
import com.ibcemobile.smoxstyler.adapter.TimeSlotAdapterNew
import com.ibcemobile.smoxstyler.adapter.loadCircleImage
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.databinding.ActivityEditAppointmentBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.*
import com.ibcemobile.smoxstyler.utils.APPOINTMENT_ID
import com.ibcemobile.smoxstyler.utils.getCurrentStartTime
import com.ibcemobile.smoxstyler.viewmodels.*
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Arun
 */
class EditAppointmentActivity : BaseActivity(), DatePickerListener,
    TimeSlotAdapterNew.ItemClickListener {
    private lateinit var binding: ActivityEditAppointmentBinding
    private lateinit var viewModel: AppointmentViewModel
    private lateinit var adapter: ServiceBookingAdapter
    private lateinit var appointment: Appointment
    private lateinit var slotViewModel: TimeSlotListViewModel
    private val dateSelectCalender = Calendar.getInstance()
    lateinit var barber: Barber
    var slotTime: TimeSlotResult? = null
    var selectedDate: String = ""
    var dateSelected = Date()
    private var userId: String = ""
    private var appointmentId: Int? = null
    private val slotAdapter = TimeSlotAdapterNew()
    var timeSlotList = arrayListOf<TimeSlotResult>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        binding.datePicker
            .setListener(this)
            .showTodayButton(false)
            .setDays(30)
            .setOffset(3)
            .setDateSelectedColor(resources.getColor(R.color.light_yellow))
            .setTodayDateBackgroundColor(resources.getColor(R.color.light_yellow))
            .init()
        binding.datePicker.setDate(DateTime(DateTimeZone.getDefault()).plusDays(0))


        appointmentId = intent.getIntExtra(APPOINTMENT_ID, 0)


        val factory = AppointmentViewModelFactory(
            AppointmentRepository.getInstance(),
            appointmentId!!
        )

        viewModel = ViewModelProvider(this, factory).get(AppointmentViewModel::class.java)
        viewModel.getAppointmentDetail(this)
        viewModel.appointmentDetail.observe(this) {
            if (it != null) {
                appointment = it
                //Log.e("Appointment_Detail", appointment.user.toString())
                binding.serviceList.adapter = adapter
                adapter.submitList(appointment.services)
                userId = appointment.user.id.toString()
                var duration = 0
                for (item in appointment.services) {
                    duration += item.duration
                }
                var canSelectSlotCount = 0
                if (duration > 0) {
                    canSelectSlotCount = duration / 10
                    val oddDuration=duration%10
                    if (oddDuration!=0){
                        canSelectSlotCount++
                    }
                }
                slotAdapter.canSelectSlotCount(canSelectSlotCount)

                binding.txtName.text = appointment.user.firstName
                binding.txtContact.text = appointment.user.phone

                loadCircleImage(
                    binding.imageCircle,
                    appointment.user.image,
                    resources.getDrawable(R.drawable.small_placeholder),
                    true
                )
            }
        }
        adapter = ServiceBookingAdapter(true)
        binding.serviceList.layoutManager = LinearLayoutManager(this)


        val timeSelected = arrayListOf<String>()

        binding.btnNext.setOnClickListener {
            timeSelected.clear()
            for (slot in timeSlotList) {
                if (slot.isSelected)
                    timeSelected.add(slot.timeslot)

            }
            updateAppointment(timeSelected)
        }


        barber = app.currentUser
        binding.rvTimeSlot.adapter = slotAdapter
        val modelFactory = TimeSlotListViewModelFactory(barber)
        slotViewModel =
            ViewModelProviders.of(this, modelFactory).get(TimeSlotListViewModel::class.java)
        slotViewModel.timeSlotList.observe(this, androidx.lifecycle.Observer {
            progressHUD.dismiss()
            timeSlotList = it as ArrayList<TimeSlotResult>
            slotAdapter.setItemClickListener(this)
            slotAdapter.submitList(it)
        })
    }

    private fun updateAppointment(times: ArrayList<String>) {
        val arr = JsonArray()
        for (item in times) {
            arr.add(item)
        }
        val dateFormat = SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
        val serverDate = SimpleDateFormat(Constants.KDateFormatter.server, Locale.getDefault())
        if (times.isNullOrEmpty()) {
            Toast.makeText(this, "Select a Time Slot for Book Appointment", Toast.LENGTH_LONG)
                .show()
            return
        }

        val jsonObject = JsonObject()
        jsonObject.add("timeslots", arr)
        jsonObject.addProperty("barber_id", app.currentUser.id.toString())
        jsonObject.addProperty("appointment_id", appointmentId.toString())
        jsonObject.addProperty("user_id", userId)
        jsonObject.addProperty("date", selectedDate)
        progressHUD.show()
        val params = JSONObject(jsonObject.toString())

        APIHandler(
            applicationContext,
            Request.Method.POST,
            Constants.API.update_appointment,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    //Log.e("TAG", "result: $result")
                    progressHUD.dismiss()

                    if (result.has("error")) {
                        if (!result.getBoolean("error")) {

                            //val ids = result.getInt("result")
                            showDialog(
                                1,
                                "Your Appointment has been Reschedule successfully",
                                R.drawable.ic_baseline_check_circle_24,
                                "Back to Home",
                                false
                            )
                        }

                    }
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            }, "json"
        )
    }

    fun showDialog(ids: Int, mess: String, id: Int, btmText: String, isClick: Boolean) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.message_dailog_fragment)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)
        val txtApprove = dialog.findViewById<TextView>(R.id.txtBtn)
        txtApprove.text = btmText
        val img = dialog.findViewById<ImageView>(R.id.imgMessage)
        img.setImageResource(id)
        txtMessage.text = mess

        txtApprove.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onTimeClick(slot: TimeSlotResult) {
        slotTime = slot
    }

    override fun onDateSelected(dateSelectedd: DateTime) {
        //Log.e("TAG", "onDateSelected: $dateSelectedd")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val localDateFormat =
            SimpleDateFormat(Constants.KDateFormatter.local_full_time, Locale.getDefault())
        val curDateFormat = SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
        val date = inputFormat.parse(dateSelectedd.toString())
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
                    this,
                    R.color.light_yellow
                )
            )

            var newStartTime = "-"
            var newEndTime = "-"
            if (sessionManager.userDataOpenDays == "") {
                Toast.makeText(
                    applicationContext,
                    "Set your Open days", Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this, HourOperationActivity::class.java)
                startActivity(intent)
            } else {
                barber.openDays = barber.getOpenDays(JSONObject(sessionManager.userDataOpenDays!!))
                val openDay = barber.openDays[dateSelectCalender.get(Calendar.DAY_OF_WEEK) - 1]
                if (!openDay.isClosed) {

                    if (dateSelected == currDate) {


                        val barberStartDateTime =
                            localDateFormat.parse(selectDate + " " + openDay.startTime)
                        val barberEndTime =
                            localDateFormat.parse(selectDate + " " + openDay.endTime)
                        //val curTime = Date()

                        val timeonly = timeFormat.format(Date())
                        val curTime: Date? = localDateFormat.parse("$selectDate $timeonly")
                        Log.e("TAG", "onDateSelected: $barberStartDateTime $barberEndTime $curTime")
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
                // progressHUD.show()

                slotViewModel.fetchList(applicationContext, date, newStartTime, newEndTime, appointmentId!!)
            }

        } else {
            binding.datePicker.setDateSelectedTextColor(ContextCompat.getColor(this, R.color.black))
            binding.datePicker.setDate(DateTime())
            binding.datePicker.onDateSelected(Day(DateTime()))


            Toast.makeText(this, "Select valid date", Toast.LENGTH_SHORT).show()
        }

    }



}