package com.ibcemobile.smoxstyler.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.AppointmentDetailActivity
import com.ibcemobile.smoxstyler.adapter.AppointmentAdapter
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.databinding.AppointmentsFragmentBinding
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.type.AppointmentType
import com.ibcemobile.smoxstyler.utils.APPOINTMENT_ID
import com.ibcemobile.smoxstyler.utils.currentDate
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModel
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class CalendarAppointmentsFragment : BaseFragment() {
    private lateinit var binding: AppointmentsFragmentBinding
    private var adapter = AppointmentAdapter()
    private lateinit var viewModel: AppointmentListViewModel
    private var selectedDate: String? = null

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
        val refreshLayout = binding.swipeRefreshLayout
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            if (!selectedDate.isNullOrEmpty()) {
                viewModel.fetchList(requireActivity(), selectedDate!!, app.currentUser.id)
            }
        }
        refreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )

        CalenderFragment.calDateClick(object : CalenderFragment.CalDateChangeListener {
            override fun ondateChanded(selectDate: String) {
                Log.e("selectDate", selectDate)
                selectedDate = selectDate
                viewModel.fetchList(requireActivity(), selectedDate!!, app.currentUser.id)
            }
        })


        initAdapter()
        initObserver()

        viewModel.fetchList(requireActivity(), currentDate(), app.currentUser.id)


    }
    private fun initAdapter(){

        binding.appointList.layoutManager = LinearLayoutManager(requireActivity())
        binding.appointList.hasFixedSize()
        binding.appointList.adapter = adapter
        adapter.setItemClickListener(object : AppointmentAdapter.ItemClickListener {
            override fun onItemClick(view: View, appointment: Appointment, position: Int) {
                if (view.id == R.id.imgDelete) {
                    showDeleteConfirmDialog(appointment, position)
                } else {
                    val intent = Intent(activity, AppointmentDetailActivity::class.java)
                    intent.putExtra(APPOINTMENT_ID, appointment.id)
                    startActivity(intent)
                    activity!!.overridePendingTransition(
                        R.anim.activity_enter,
                        R.anim.activity_exit
                    )
                }

            }
        })

    }

   private fun initObserver(){
        viewModel.appointments.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val appointmentSortedList: List<Appointment> = it.sortedBy { it.start_time_filter }
                adapter.submitList(appointmentSortedList)
                adapter.notifyDataSetChanged()

                if (it.isEmpty()) {
                    binding.tvNotFound.visibility = View.VISIBLE
                } else {
                    binding.tvNotFound.visibility = View.GONE
                }
            }

        })

        viewModel.updateAppointment.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it!=null){
                if (!it){
                    if (!selectedDate.isNullOrEmpty()) {
                        viewModel.fetchList(requireActivity(), selectedDate!!, app.currentUser.id)
                    }

                }
            }

        })

    }

    private fun showDeleteConfirmDialog(appointment: Appointment, position: Int) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle("Delete Appointment")
        builder.setMessage("Are you sure you want to delete the selected appointment?")
        builder.setPositiveButton("Delete") { _, _ ->
            viewModel.updateAppointment(requireActivity(), appointment, AppointmentType.Canceled)
        }
        builder.setNegativeButton("Cancel") { _, _ ->

        }
        builder.show()
    }


}