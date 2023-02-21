package com.ibcemobile.smoxstyler.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper.getMainLooper
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Request
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.BookAppointmentActivity
import com.ibcemobile.smoxstyler.activities.SubscriptionActivity
import com.ibcemobile.smoxstyler.activities.UpNextOptionsActivity
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.data.BarberRepository
import com.ibcemobile.smoxstyler.databinding.UpNextFragmentBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.Barber
import com.ibcemobile.smoxstyler.model.Category
import com.ibcemobile.smoxstyler.model.type.AppointmentType
import com.ibcemobile.smoxstyler.model.type.UpNextStatus
import com.ibcemobile.smoxstyler.utils.ACTION_DATE_CHANGE
import com.ibcemobile.smoxstyler.utils.SELECTED_DATE
import com.ibcemobile.smoxstyler.utils.SELECTED_DATE1
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModel
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModelFactory
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class UpNextFragment : BaseFragment(), DatePickerListener {
    private lateinit var binding: UpNextFragmentBinding
    private val UPNEXT_OPTION_REQUEST = 100
    private var isStartHours = false
    private lateinit var viewModel: AppointmentListViewModel
    private lateinit var barber: Barber
    private var countTime: Long = 0
    private var countUpdateTime: Long = 0
    private var upNextStatus = ""
    private var upNexts = ArrayList<Appointment>()
    private var approved = ArrayList<Appointment>()
    var items: ArrayList<Category> = ArrayList()
    var selectedDate: String = ""
    lateinit var dateSelectedd: DateTime

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UpNextFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.datePicker
            .setListener(this)
            .showTodayButton(false)
            .setDays(30)
            .setDateSelectedColor(resources.getColor(R.color.light_yellow))
            .setTodayDateBackgroundColor(resources.getColor(R.color.light_yellow))
            .init()
        binding.datePicker.setDate(DateTime(DateTimeZone.getDefault()).plusDays(0))
        initViewPager()
        setCurrentTime()
        initViewModel()
        initObserver()

    }

    private fun initViewModel() {
        val factory = AppointmentListViewModelFactory(AppointmentRepository.getInstance())
        viewModel = ViewModelProvider(this, factory).get(AppointmentListViewModel::class.java)
        barber = BarberRepository.getInstance().getBarber(app.currentUser.id) ?: app.currentUser

        binding.txtCountTimer.setOnClickListener {
            val intent = Intent(activity, UpNextOptionsActivity::class.java)
            startActivityForResult(intent, UPNEXT_OPTION_REQUEST)
            requireActivity().overridePendingTransition(
                R.anim.activity_enter,
                R.anim.activity_exit
            )
        }
    }

    private fun initViewPager() {
        val mPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        mPagerAdapter.addFrag(AppointmentsFragment(), "Appointments")
        mPagerAdapter.addFrag(PendingAppointmentsFragment(), "Pending")
        mPagerAdapter.addFrag(LastClientFragment(), "Last Client")
        binding.tabLayout.setupWithViewPager(binding.viewpager)
        binding.viewpager.adapter = mPagerAdapter
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.isSelected) {
                    val bundle = Bundle()
                    bundle.putString(SELECTED_DATE1, selectedDate)
                    mPagerAdapter.getItem(tab.position).arguments = bundle

                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

    private fun initObserver() {
        viewModel.appointments.observe(
            viewLifecycleOwner
        ) { appointments ->
            if (appointments != null) {
                upNexts.clear()
                upNexts.addAll(appointments)
                approved = upNexts.filter {
                    it.status.toString().equals(AppointmentType.Approved.name, false)
                } as ArrayList<Appointment>
                binding.txtWaitList.text =
                    if (approved.isEmpty()) "N/A" else approved.size.toString()
                var lastClientName = "N/A"
                if (approved.size > 0) {
                    lastClientName = approved[0].user.firstName.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }
                }
                if (!TextUtils.isEmpty(lastClientName)) {
                    binding.txtUpNext.text = lastClientName
                } else {
                    binding.txtUpNext.text = "N/A"
                }

                viewModel.staus.value?.apply {
                    upNextStatus = this
                }
                calcStartCountTime()
            }
        }

        binding.fabAddBooking.setOnClickListener {
            if (sessionManager.isSubscribed) {


                Log.d("++--++","191 UpNextFragement dateSelectedd : $dateSelectedd")

                val intent = Intent(requireActivity(), BookAppointmentActivity::class.java)
                intent.putExtra(Constants.API.SELECTED_DATE,dateSelectedd)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.activity_enter,
                    R.anim.activity_exit
                )
            } else {
                val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(
                    R.anim.activity_enter,
                    R.anim.activity_exit
                )
            }
        }

        subscribeUi(selectedDate)
    }

    override fun onDateSelected(dateSelected: DateTime) {

        Log.d("++--++","191 dateSelected : $dateSelected")

        dateSelectedd=dateSelected
        selectedDate = getFormattedDateFromTimestamp(dateSelected.millis)
        val intent = Intent(ACTION_DATE_CHANGE)
        intent.putExtra(SELECTED_DATE, selectedDate)
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent)
        subscribeUi(selectedDate)

    }


    private fun getFormattedDateFromTimestamp(timestampInMilliSeconds: Long): String {
        val date = Date()
        date.time = timestampInMilliSeconds
        return SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault()).format(
            date
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPNEXT_OPTION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                data?.apply {
                    val s: String = when (val status = getStringExtra("status")) {
                        UpNextStatus.Automatically.name -> ""
                        UpNextStatus.Reset.name -> ""
                        else -> {
                            status!!
                        }
                    }
                    updateCountTime(s)
                }
            }
        }
    }


    private fun subscribeUi(dateSelected: String) {
        if (dateSelected.isEmpty()) {
            val dateFormat = SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
            with(viewModel) {
                fetchList(
                    requireActivity(),
                    Constants.convertLocalToUTC(Date(), dateFormat),
                    barberId = barber.id
                )
            }
        } else {
            with(viewModel) {
                fetchList(
                    requireActivity(),
                    dateSelected,
                    barberId = barber.id
                )
            }
        }


    }

    private fun updateWaitTime(countDown: Long = 0): Unit {
        val formatter = SimpleDateFormat(Constants.KDateFormatter.second, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("GMT")
        binding.txtCountTimer.text = formatter.format(Date(countDown * 1000))
    }

    private fun calcStartCountTime() {
        var nWait = 0 //  Number of the waiting list
        var isOverlay = false // If service is processing now,  true else false
        var t: Long = 0
        if (!isStartHours) {
            updateWaitTime()
        } else
            for (a in approved) {
                a.officialDate?.apply {
                    if (this.time < (Date().time + 3600000)) {
                        val durationInSec = a.duration * 60
                        val diffInSec = TimeUnit.MILLISECONDS.toSeconds(Date().time - this.time)
                        if (durationInSec > diffInSec) {
                            t += durationInSec
                            if (diffInSec > 0 && !isOverlay) {
                                isOverlay = true
                                t -= diffInSec
                            }
                            nWait++
                        }
                    }
                }
            }

        if (countUpdateTime == t) {
            countTime = 0
            updateWaitTime()
        } else {
            countUpdateTime = t
            countTime = t
        }
        if (upNextStatus == UpNextStatus.Paused.name) {
            if (isStartHours && approved.size > 0 && Date().time > approved[0].officialDate!!.time) {
                countTime = t
                updateWaitTime(t)
            }
            binding.txtCountTimer.text = if (nWait == 0) "N/A" else (nWait).toString()

        }
    }

    private fun getUpNext(): Appointment? {
        for (a in approved) {
            if (a.officialDate != null) {
                if (Date().time < a.officialDate!!.time) {
                    return a
                }
            }
        }
        return null
    }

    private fun updateCountTime(status: String) {
        val params = HashMap<String, String>()
        params["status"] = status

        val dateFormat = SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault())
        //val date = dateFormat.format(Date())
        val date = Constants.convertLocalToUTC(Date(), dateFormat)
        params["date"] = date

        progressHUD.show()

        APIHandler(
            requireContext(),
            Request.Method.PUT,
            Constants.API.upnext_time,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    upNextStatus = status
                }

                override fun onFail(error: String?) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        context!!.applicationContext,
                        error, Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


    private fun cancelSubscription(isCancel: String, userID: String) {
        val params = HashMap<String, String>()
        params["userId"] = userID
        params["isCancel"] = isCancel

        APIHandler(
            requireActivity(),
            Request.Method.POST,
            Constants.API.cancelSubscription,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    Log.e("TAG", "onResult: $result")
                    val error = result.getBoolean("error")
                    if (!error) {
                        val subsCancelResult = result.getBoolean("result")
                        sessionManager.isSubscribed = subsCancelResult
                    }

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


    class ViewPagerAdapter(manager: FragmentManager) :
        FragmentStatePagerAdapter(manager) {
        private val mFragmentList: MutableList<Fragment> = ArrayList()
        private val mFragmentTitleList: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }


        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
    }

    private fun setCurrentTime() {
        val someHandler = Handler(getMainLooper())
        someHandler.postDelayed(object : Runnable {
            override fun run() {
                binding.txtTime.text = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
                someHandler.postDelayed(this, 1000)
            }
        }, 10)
    }
}

