package com.ibcemobile.smoxstyler.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.AppointmentDetailActivity
import com.ibcemobile.smoxstyler.activities.SubscriptionActivity
import com.ibcemobile.smoxstyler.adapter.AppointAdapter
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.databinding.AppointmentsFragmentBinding
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.type.AppointmentType
import com.ibcemobile.smoxstyler.utils.*
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModel
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModelFactory


class LastClientFragment : BaseFragment() {
    private lateinit var binding: AppointmentsFragmentBinding
    private var adapter = AppointAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    var page: Int = 1
    private lateinit var viewModel: AppointmentListViewModel

    var selectedDate: String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = AppointmentsFragmentBinding.inflate(inflater, container, false)

        val factory = AppointmentListViewModelFactory(AppointmentRepository.getInstance())
        viewModel = ViewModelProvider(this, factory).get(AppointmentListViewModel::class.java)


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            mMessageReceiver,
            IntentFilter(ACTION_DATE_CHANGE)
        )



        if (arguments != null) {
            selectedDate = requireArguments().getString(SELECTED_DATE1)
            page = 1
            viewModel.getAppointmentsByDate(
                requireActivity(),
                page,
                AppointmentType.Approved,
                selectedDate!!
            )
        }


        val refreshLayout = binding.swipeRefreshLayout
        refreshLayout.setOnRefreshListener {
            page = 1
            refreshLayout.isRefreshing = false
            if (selectedDate.isNullOrEmpty())
            viewModel.getAppointmentsByDate(
                requireActivity(),
                page,
                AppointmentType.Completed,
                selectedDate!!
            )
        }
        refreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )
        initAdapter()
        initObserver()


    }

    private fun initObserver() {
        viewModel.appointmentsByDateLast.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it!=null){
            val appointmentSortedList: List<Appointment> = it.sortedBy { it.start_time_filter }
            adapter.submitList(appointmentSortedList)
            adapter.notifyDataSetChanged()
            page++
            if (it.isNotEmpty()) {
                binding.tvNotFound.visibility = View.GONE
            } else {
                binding.tvNotFound.visibility = View.VISIBLE
            }

            }
        })
    }

    private fun initAdapter() {
        layoutManager = LinearLayoutManager(requireContext())
        binding.appointList.layoutManager = layoutManager
        binding.appointList.adapter = adapter
        adapter.setItemClickListener(object : AppointAdapter.ItemClickListener {
            override fun onItemClick(view: View, appointment: Appointment, position: Int) {
                when (view.id) {
                    R.id.mainRow -> {
                        if (sessionManager.isSubscribed) {
                            val intent = Intent(activity, AppointmentDetailActivity::class.java)
                            intent.putExtra(APPOINTMENT_ID, appointment.id)
                            intent.putExtra(FROM_LAST, 3)
                            startActivity(intent)
                            activity!!.overridePendingTransition(
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
                }

            }
        })

        binding.appointList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLastItemDisplaying(binding.appointList) && adapter.itemCount >= 50) {
                    page++
                    viewModel.getAppointmentsByDate(
                        requireActivity(),
                        page,
                        AppointmentType.Completed,
                        selectedDate!!
                    )
                }
            }
        })


    }


    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val mdate = intent.getStringExtra(SELECTED_DATE)
            selectedDate = mdate
            page = 1
            viewModel.getAppointmentsByDate(
                requireActivity(),
                page,
                AppointmentType.Completed,
                selectedDate!!
            )

        }
    }

    private fun isLastItemDisplaying(recyclerView: RecyclerView): Boolean {
        if (recyclerView.adapter!!.itemCount != 0) {
            val lastVisibleItemPosition =
                (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.adapter!!.itemCount - 1)
                return true
        }
        return false
    }


    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }




}