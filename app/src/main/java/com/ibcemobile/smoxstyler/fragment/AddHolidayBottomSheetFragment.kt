package com.ibcemobile.smoxstyler.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.HourOperationActivity
import com.ibcemobile.smoxstyler.databinding.LayoutEditableBottomSheetBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Holiday
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AddHolidayBottomSheetFragment(private val holiday: Holiday?) : BaseFragment() {

    companion object {
        const val TAG = "AddHolidayBottomSheet"
    }

    private lateinit var binding: LayoutEditableBottomSheetBinding
    private var select_day: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutEditableBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imgBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.txtDate.setOnClickListener {
            openDatePicker(binding.txtDate)
        }

        if (holiday != null) {
            binding.edit.setText(holiday.title)
        }

        binding.btnUpdate.setOnClickListener {
            //handle click event
            if (TextUtils.isEmpty(binding.txtDate.text.toString())) {
                binding.txtDate.error = "Field is empty!"
                binding.txtDate.requestFocus()
            } else if (TextUtils.isEmpty(binding.edit.text.toString())) {
                binding.edit.error = "Field is empty!"
                binding.edit.requestFocus()
            } else {

                if (holiday == null) {
                    addHoliday(binding.txtDate.text.toString(), binding.edit.text.toString())
                } else {
                    holiday.id.let { it1 ->
                        editHoliday(
                            binding.txtDate.text.toString(), binding.edit.text.toString(),
                            it1
                        )
                    }
                }
            }
        }
    }

    private fun addHoliday(date: String, holiday: String) {
        val params = HashMap<String, String>()
        params["date"] = date
        params["day"] = select_day
        params["title"] = holiday
        progressHUD.show()
        APIHandler(
            requireActivity(),
            Request.Method.POST,
            Constants.API.add_holiday,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        result.getString("message"), Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(requireActivity(), HourOperationActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun editHoliday(date: String, holiday: String, ids: String) {
        val params = HashMap<String, String>()
        params["holiday_id"] = ids
        params["date"] = date
        params["day"] = select_day
        params["title"] = holiday
        progressHUD.show()
        APIHandler(
            requireActivity(),
            Request.Method.PUT,
            Constants.API.edit_holiday,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        result.getString("message"), Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(requireActivity(), HourOperationActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    /*  Toast.makeText(
                          requireActivity(),
                          error, Toast.LENGTH_LONG
                      ).show()*/
                }
            })
    }

    private fun openDatePicker(date_field: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        val datePicker = DatePickerDialog(
            requireActivity(), R.style.MyAlertDialogStyle,
            { view, year, month, dayOfMonth ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val sdfDay = SimpleDateFormat("EEEE", Locale.getDefault())
                calendar[year, month] = dayOfMonth
                val dateString = sdf.format(calendar.time)
                date_field.text = dateString// set the date
                Log.e(
                    TAG,
                    "openDatePicker: " + sdfDay.format(calendar.time).lowercase(Locale.getDefault())
                )
                select_day = sdfDay.format(calendar.time)
            }, year, month, day
        ) // set date picker to current date

        datePicker.show()

        datePicker.setOnCancelListener { dialog -> dialog.dismiss() }
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000

    }


}