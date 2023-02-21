package com.ibcemobile.smoxstyler.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.FundsAdapter
import com.ibcemobile.smoxstyler.databinding.FundsFragmentBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Funds
import com.kal.rackmonthpicker.RackMonthPicker
import org.joda.time.DateTime
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FundsFragment : BaseFragment() {

    private lateinit var binding: FundsFragmentBinding
    private var fundsList = ArrayList<Funds>()
    private val fundsAdapter = FundsAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FundsFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateTime = DateTime(System.currentTimeMillis())
        val firstDayOfMonthTimestamp = dateTime.withDayOfMonth(1).millis
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val viewFormat = SimpleDateFormat("MMMM", Locale.getDefault())

        val firstDate = dateFormat.format(Date(firstDayOfMonthTimestamp))

        binding.txtDate.text = viewFormat.format(Date(firstDayOfMonthTimestamp)).toString()

        fetchFunds(firstDate)

        binding.txtDate.setOnClickListener {
            openDatePickerDialog(binding.txtDate)
        }

    }
    private fun openDatePickerDialog(textView: TextView) {
        RackMonthPicker(requireActivity())
            .setColorTheme(R.style.MyAlertDialogStyle)
            .setLocale(Locale.ENGLISH)
            .setPositiveButton { month, startDate, _, year, it ->
                textView.text = Constants.getMonthName(month)

                fetchFunds("$year-$month-01")
            }
            .setNegativeButton {
                it.dismiss()
            }.show()

    }

    private fun fetchFunds(date: String) {
        val params = HashMap<String, String>()
        params["barber_id"] = app.currentUser.id.toString()
        params["date"] = date
        progressHUD.show()
        APIHandler(
            requireActivity(),
            Request.Method.POST,
            Constants.API.transaction_list,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    fundsList.clear()
                    var totalMonthInCome = 0f
                    if (result.getString("result") != null && result.getString("result") != "null") {
                        val jsonArray = result.getJSONArray("result")
                        for (i in 0 until jsonArray.length()) {
                            val json = jsonArray.getJSONObject(i)
                            val funds = Funds(json)
                            fundsList.addAll(listOf(funds))
                            totalMonthInCome += funds.price.toFloat()
                        }
                        val total=result.getInt("total_earning")
                        binding.txtTotal.text = "$$total.00"

                        binding.rvFunds.layoutManager = LinearLayoutManager(requireActivity())
                        binding.rvFunds.adapter = fundsAdapter
                        binding.rvFunds.hasFixedSize()

                        fundsAdapter.submitList(fundsList)

                        if (fundsList.size > 0) {
                            binding.tvNotFound.visibility = View.GONE
                        } else {
                            binding.tvNotFound.visibility = View.VISIBLE
                            binding.txtTotal.text = "$0.00"

                        }
                    } else {
                        binding.tvNotFound.visibility = View.VISIBLE
                        binding.txtTotal.text = "$0.00"

                    }

                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        requireActivity().applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }



}