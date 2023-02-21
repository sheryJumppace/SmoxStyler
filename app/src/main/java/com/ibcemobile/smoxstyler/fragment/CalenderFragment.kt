package com.ibcemobile.smoxstyler.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.google.android.material.tabs.TabLayout
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.AddEventActivity
import com.ibcemobile.smoxstyler.activities.BookAppointmentActivity
import com.ibcemobile.smoxstyler.activities.SubscriptionActivity
import com.ibcemobile.smoxstyler.databinding.CalenderFragmentBinding
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Event
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CalenderFragment : BaseFragment(), DatePickerListener {

    private lateinit var binding: CalenderFragmentBinding
    var items: ArrayList<Event> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = CalenderFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.datePicker.setListener(this).showTodayButton(false).setDays(30)
            .setDateSelectedColor(resources.getColor(R.color.light_yellow))
            .setTodayDateBackgroundColor(resources.getColor(R.color.light_yellow)).init()
        binding.datePicker.setDate(DateTime(DateTimeZone.getDefault()).plusDays(0))

        val mPagerAdapter = ViewPagerAdapter(requireActivity().supportFragmentManager)
        binding.tabLayout.setupWithViewPager(binding.viewpager)

        val calFragment = CalendarAppointmentsFragment()
        mPagerAdapter.addFrag(calFragment, "Appointments")
        mPagerAdapter.addFrag(CalendarEventsFragment(), "Events")

        binding.viewpager.adapter = mPagerAdapter
        binding.fabAddBooking.setOnClickListener {
            if (app.currentUser.isSubscribed) {
//                val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
//                var date = outputFormat.format(Date());
                val intent = Intent(requireActivity(), BookAppointmentActivity::class.java)
                intent.putExtra(Constants.API.SELECTED_DATE,DateTime())
                startActivity(intent)
            } else {
                val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                startActivity(intent)
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    binding.fabAddBooking.setImageResource(R.drawable.ic_fab_add)

                    binding.fabAddBooking.setOnClickListener {
                        if (app.currentUser.isSubscribed) {
                            val intent =
                                Intent(requireActivity(), BookAppointmentActivity::class.java)
                            intent.putExtra(Constants.API.SELECTED_DATE,DateTime())


                            startActivity(intent)
                        } else {
                            val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    binding.fabAddBooking.setImageResource(R.drawable.ic_add_event_icon)
                    binding.fabAddBooking.setOnClickListener {
                        if (app.currentUser.isSubscribed) {
                            val intent = Intent(requireActivity(), AddEventActivity::class.java)
                            startActivity(intent)
                            requireActivity().overridePendingTransition(
                                R.anim.activity_enter, R.anim.activity_exit
                            )

                        } else {
                            val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                            startActivity(intent)
                            requireActivity().overridePendingTransition(
                                R.anim.activity_enter, R.anim.activity_exit
                            )

                        }

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    class ViewPagerAdapter(manager: FragmentManager?) : FragmentStatePagerAdapter(manager!!) {
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

        fun addFrag(fragment: Fragment) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add("")
        }

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }
    }


    override fun onDateSelected(dateSelected: DateTime) {
        calDateChangeListener.ondateChanded(getFormattedDateFromTimestamp(dateSelected.millis))
        eventDateChangeListener.ondateChanded(getFormattedDateFromTimestamp(dateSelected.millis))
    }

    companion object {
        lateinit var calDateChangeListener: CalDateChangeListener
        lateinit var eventDateChangeListener: EventDateChangeListener
        fun getFormattedDateFromTimestamp(timestampInMilliSeconds: Long): String {
            val date = Date()
            date.time = timestampInMilliSeconds
            return SimpleDateFormat(Constants.KDateFormatter.serverDay, Locale.getDefault()).format(
                date
            )
        }

        fun calDateClick(dateChangeListener: CalDateChangeListener) {
            this.calDateChangeListener = dateChangeListener
        }

        fun eventDateClick(dateChangeListener: EventDateChangeListener) {
            this.eventDateChangeListener = dateChangeListener
        }
    }

    interface CalDateChangeListener {
        fun ondateChanded(selectDate: String)
    }

    interface EventDateChangeListener {
        fun ondateChanded(selectDate: String)
    }


}



