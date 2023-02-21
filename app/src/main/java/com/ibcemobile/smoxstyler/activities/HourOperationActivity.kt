package com.ibcemobile.smoxstyler.activities

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.ibcemobile.smoxstyler.adapter.OpenHourAdapter
import com.ibcemobile.smoxstyler.databinding.ActivityHourOperationBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Barber
import com.ibcemobile.smoxstyler.model.OpenDay
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.HourOperationOffDaysAdapter
import com.ibcemobile.smoxstyler.fragment.AddHolidayBottomSheetFragment
import com.ibcemobile.smoxstyler.model.Holiday


class HourOperationActivity : BaseActivity(), TimePickerDialog.OnTimeSetListener,HourOperationOffDaysAdapter.MyItemClickListener {
    private lateinit var binding: ActivityHourOperationBinding
    private lateinit var adapter: OpenHourAdapter
    private lateinit var holidayAdapter: HourOperationOffDaysAdapter
    private var hours = ArrayList<OpenDay>()
    val items:ArrayList<Holiday> = ArrayList()
    private var selectedIndex: Int = -1
    private var isStartTime: Boolean = false
    private lateinit var barber: Barber
    private lateinit var itemView: View

    companion object {
        const val TAG: String = "HourOperationActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHourOperationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        holidayAdapter= HourOperationOffDaysAdapter(this)
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.searchBar.setOnClickListener {
            updateOpenHours()
        }

        binding.imgAdd.setOnClickListener {
            val fragment: Fragment = AddHolidayBottomSheetFragment(holiday = null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment, "TAG")
                .addToBackStack(null).commit()
        }
        barber = app.currentUser
        adapter = OpenHourAdapter()
        adapter.setItemClickListener(object : OpenHourAdapter.ItemClickListener {
            override fun onDayChange(view: View, position: Int, isOpen: Boolean) {
                hours[position].isClosed = !isOpen
                if (isOpen){
                    hours[position].startTime="09:00 AM"
                    hours[position].endTime="05:00 PM"
                }

            }

            override fun onItemClick(view: View, position: Int, isStart: Boolean) {
                itemView = view
                itemView.isEnabled = false
                selectedIndex = position
                isStartTime = isStart
                openTimePicker(isStartTime)
            }
        })

        getHours()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        itemView.isEnabled = true
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, 0)
        cal.set(Calendar.MONTH, 0)
        cal.set(Calendar.DAY_OF_MONTH, 0)
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val formatter = SimpleDateFormat(Constants.KDateFormatter.hourAM, Locale.getDefault())
        val time = formatter.format(cal.time)

        val openDay = hours[selectedIndex]
        if (isStartTime) {
            openDay.startTime = time
            isStartTime=false
            openTimePicker(isStartTime)
        } else {
            openDay.endTime = time
        }
        adapter.submitList(hours)
        adapter.notifyDataSetChanged()

    }

    private fun openTimePicker(isStartTime:Boolean) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this@HourOperationActivity,
            R.style.themeOnverlay_timePicker,
            this@HourOperationActivity,
            hour,
            minute,
            false
        )
        if (isStartTime){
            timePickerDialog.setMessage("Start Time")
        }else{
            timePickerDialog.setMessage("End Time")
        }
        timePickerDialog.show()

        timePickerDialog.setOnDismissListener {
            itemView.isEnabled = true
        }
    }


    private fun getHours() {
        val params = HashMap<String, String>()
        progressHUD.show()
        APIHandler(
            applicationContext,
            Request.Method.GET,
            Constants.API.hours + "/" + barber.id.toString(),
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    items.clear()
                    if (result.has("result")) {
                        val data = result.getJSONObject("result")
                        hours = barber.getOpenDays(data)
                        sessionManager.userDataOpenDays = data.toString()
                        adapter.submitList(hours)
                        binding.rvOnDays.adapter = adapter

                    }
                    if(result.has("holidays")){
                        val jsonArray = result.getJSONArray("holidays")
                        for (i in 0 until jsonArray.length()) {
                            val json = jsonArray.getJSONObject(i)
                            val holiday = Holiday(json)
                            items.add(holiday)
                        }
                        binding.rvOffDays.layoutManager=LinearLayoutManager(this@HourOperationActivity)
                        holidayAdapter.saveData(items)
                        binding.rvOffDays.adapter=holidayAdapter


                    }
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                   /* Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()*/
                }
            })
    }

    private fun updateOpenHours() {
        val params = HashMap<String, String>()
        val jsonData = JSONObject(sessionManager.userDataOpenDays!!)
        hours.forEach {
            if(it.isClosed){
                params[it.getFieldNameOfDay()] = ""
            }else{
                params[it.getFieldNameOfDay()] = it.startTime+"-"+it.endTime
            }
            it.updateOpenDays(
                it.getFieldNameOfDay(),
                it.getHours().uppercase(Locale.getDefault()), jsonData
            )
        }
        sessionManager.userDataOpenDays = Gson().toJson(hours)
        progressHUD.show()
        APIHandler(
            applicationContext,
            Request.Method.POST,
            Constants.API.Update_hours,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                   // Log.e("Open days Data:- ", result.toString())
                    Toast.makeText(
                        applicationContext,
                        "Update successfully", Toast.LENGTH_LONG
                    ).show()

                    getHours()
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


    private fun deleteHolidays(ids:String) {
        val params = HashMap<String, String>()
        params["holiday_id"]=ids

        progressHUD.show()
        APIHandler(
            applicationContext,
            Request.Method.PUT,
            Constants.API.delete_holiday,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    barber.openDays = hours
                    Log.e("Open days Data:- ", result.toString())
                    finish()
                    val intent = Intent(this@HourOperationActivity, HourOperationActivity::class.java)
                    startActivity(intent)
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                  /*  Toast.makeText(
                        applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()*/
                }
            })
    }

    override fun edit(holiday: Holiday) {
        val fragment: Fragment = AddHolidayBottomSheetFragment(holiday)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, "TAG")
            .addToBackStack(null).commit()
    }

    override fun delete(holiday: Holiday) {
        val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        builder.setTitle("Warning!")
        builder.setMessage("Are you sure you want to delete this Holiday?")
        builder.setPositiveButton("Ok") { _, _ ->
            deleteHolidays(holiday.id)

        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}