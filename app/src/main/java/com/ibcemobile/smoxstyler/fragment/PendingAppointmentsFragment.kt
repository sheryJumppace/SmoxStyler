package com.ibcemobile.smoxstyler.fragment

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.AppointmentDetailActivity
import com.ibcemobile.smoxstyler.activities.EditAppointmentActivity
import com.ibcemobile.smoxstyler.activities.SubscriptionActivity
import com.ibcemobile.smoxstyler.adapter.AppointAdapter
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.databinding.AppointmentsFragmentBinding
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.type.AppointmentType
import com.ibcemobile.smoxstyler.utils.ACTION_DATE_CHANGE
import com.ibcemobile.smoxstyler.utils.APPOINTMENT_ID
import com.ibcemobile.smoxstyler.utils.SELECTED_DATE
import com.ibcemobile.smoxstyler.utils.SELECTED_DATE1
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModel
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModelFactory


class PendingAppointmentsFragment : BaseFragment() {
    lateinit var binding: AppointmentsFragmentBinding
    private var adapter = AppointAdapter()
    private lateinit var viewModel: AppointmentListViewModel
    var page: Int = 1
    var selectedDate: String? = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = AppointmentsFragmentBinding.inflate(inflater, container, false)




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = AppointmentListViewModelFactory(AppointmentRepository.getInstance())

        viewModel = ViewModelProvider(this, factory).get(AppointmentListViewModel::class.java)


        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            mMessageReceiver,
            IntentFilter(ACTION_DATE_CHANGE)
        )


        val refreshLayout = binding.swipeRefreshLayout
        refreshLayout.setOnRefreshListener {
            page = 1
            refreshLayout.isRefreshing = false
            viewModel.getAppointmentsByDate(
                requireActivity(),
                page,
                AppointmentType.Pending,
                selectedDate!!
            )
        }
        refreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )

        setAdapter()
        initObserver()

        if (arguments != null) {
            selectedDate = requireArguments().getString(SELECTED_DATE1)
            page = 1
            viewModel.getAppointmentsByDate(
                requireActivity(),
                page,
                AppointmentType.Pending,
                selectedDate!!
            )
        }
    }


    private fun initObserver() {
        viewModel.appointmentsByDatePend.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val appointmentSortedList: List<Appointment> = it.sortedBy { it.start_time_filter }
            adapter.submitList(appointmentSortedList)
            adapter.notifyDataSetChanged()
            page++
            if (it.isNotEmpty()) {
                binding.tvNotFound.visibility = View.GONE
            } else {
                binding.tvNotFound.visibility = View.VISIBLE
            }

        })

        updateObserver()

    }
    private fun updateObserver(){
        viewModel.updateAppointment.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                if (!it) {
                    if (!selectedDate.isNullOrEmpty()) {
                        page=1
                        viewModel.getAppointmentsByDate(
                            requireActivity(),
                            page,
                            AppointmentType.Pending,
                            selectedDate!!
                        )
                    }

                }
            }

        })
    }

    private fun setAdapter() {
        binding.appointList.layoutManager = LinearLayoutManager(requireActivity())
        binding.appointList.adapter = adapter

        adapter.setItemClickListener(object : AppointAdapter.ItemClickListener {
            override fun onItemClick(view: View, appointment: Appointment, position: Int) {
                when (view.id) {
                    R.id.txtComplete -> {
                        if (sessionManager.isSubscribed) {
                            showDialog("Are you sure want to Approve", appointment, position)
                        } else {
                            val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                            startActivity(intent)
                            requireActivity().overridePendingTransition(
                                R.anim.activity_enter,
                                R.anim.activity_exit
                            )
                        }
                    }
                    R.id.txtEdit -> {
                        if (sessionManager.isSubscribed) {

                            val intent = Intent(activity, EditAppointmentActivity::class.java)
                            intent.putExtra(APPOINTMENT_ID, appointment.id)
                            startActivity(intent)
                            activity!!.overridePendingTransition(
                                R.anim.activity_enter,
                                R.anim.activity_exit
                            )

                            //  showDialog("Are you sure want to Decline", appointment, position)
                        } else {
                            val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                            startActivity(intent)
                            requireActivity().overridePendingTransition(
                                R.anim.activity_enter,
                                R.anim.activity_exit
                            )
                        }
                    }
                    R.id.mainRow -> {
                        if (sessionManager.isSubscribed) {
                            val intent = Intent(activity, AppointmentDetailActivity::class.java)
                            intent.putExtra(APPOINTMENT_ID, appointment.id)
                            startActivity(intent)
                            activity!!.overridePendingTransition(
                                R.anim.activity_enter,
                                R.anim.activity_exit
                            )
                        }
                    }
                    else -> {
                        val intent = Intent(requireActivity(), SubscriptionActivity::class.java)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(
                            R.anim.activity_enter,
                            R.anim.activity_exit
                        )

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
                        AppointmentType.Pending,
                        selectedDate!!
                    )
                }
            }
        })

        if (!selectedDate.isNullOrEmpty())
        viewModel.getAppointmentsByDate(
            requireActivity(),
            page,
            AppointmentType.Pending,
            selectedDate!!
        )

    }


    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val mdate = intent.getStringExtra(SELECTED_DATE)
            selectedDate = mdate
            page = 1
            viewModel.getAppointmentsByDate(
                requireActivity(),
                page,
                AppointmentType.Pending,
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


    fun showDialog(mess: String, appointment: Appointment, position: Int) {

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.appointment_dailog_fragment)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)
        val txtDecline = dialog.findViewById<TextView>(R.id.txtDecline)
        val txtApprove = dialog.findViewById<TextView>(R.id.txtApprove)
        txtApprove.text = getString(R.string.txt_approved)
        txtMessage.text = mess
        txtDecline.setOnClickListener {
            viewModel.updateAppointment(requireActivity(), appointment, AppointmentType.Canceled)

            dialog.dismiss()
        }
        txtApprove.setOnClickListener {

            viewModel.updateAppointment(requireActivity(), appointment, AppointmentType.Approved)

            dialog.dismiss()
        }
        dialog.show()
    }




    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }

}