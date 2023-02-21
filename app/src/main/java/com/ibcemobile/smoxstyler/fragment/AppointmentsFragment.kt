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
import com.ibcemobile.smoxstyler.utils.*
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModel
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModelFactory
class AppointmentsFragment : BaseFragment() {
    private lateinit var binding: AppointmentsFragmentBinding
    private var adapter = AppointAdapter()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var viewModel: AppointmentListViewModel
    var page: Int = 1
    var selectdate: String? = null
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

        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(
            mMessageReceiver,
            IntentFilter(ACTION_DATE_CHANGE)
        )
        val factory = AppointmentListViewModelFactory(AppointmentRepository.getInstance())
        viewModel = ViewModelProvider(this, factory).get(AppointmentListViewModel::class.java)
        layoutManager = LinearLayoutManager(requireActivity())


        val refreshLayout = binding.swipeRefreshLayout
        refreshLayout.setOnRefreshListener {
            page = 1
            refreshLayout.isRefreshing = false
            viewModel.getAppointmentsByDate(
                requireActivity(),
                page,
                AppointmentType.Approved,
                selectdate!!
            )

        }


        refreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark
        )




        initAdapter()
        initObservers()

        if (arguments != null) {
            selectdate = requireArguments().getString(SELECTED_DATE1)
            page = 1
            viewModel.getAppointmentsByDate(
                requireActivity(),
                page,
                AppointmentType.Approved,
                selectdate!!
            )

        }
    }


    private fun initAdapter() {
        binding.appointList.layoutManager = layoutManager
        binding.appointList.adapter = adapter
        adapter.setItemClickListener(object : AppointAdapter.ItemClickListener {
            override fun onItemClick(view: View, appointment: Appointment, position: Int) {
                when (view.id) {
                    R.id.txtEdit -> {
                        val intent = Intent(activity, EditAppointmentActivity::class.java)
                        intent.putExtra(APPOINTMENT_ID, appointment.id)
                        intent.putExtra(U_IMAGE, appointment.user.image)
                        startActivity(intent)
                    }
                    R.id.txtComplete -> {
                        if (sessionManager.isSubscribed) {
                            showDialog("Are you sure want to Complete", appointment)
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

                    if (!selectdate.isNullOrEmpty()) {
                        viewModel.getAppointmentsByDate(
                            requireActivity(),
                            page,
                            AppointmentType.Approved,
                            selectdate!!
                        )
                    }
                }
            }
        })
    }

    private fun initObservers() {
        viewModel.appointmentsByDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
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

        updateObserver()
    }

    private fun updateObserver(){
        viewModel.updateAppointment.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            page = 1
            if(it!=null){
                if (!it){
                    if (!selectdate.isNullOrEmpty()){
                        viewModel.getAppointmentsByDate(
                            requireActivity(),
                            page,
                            AppointmentType.Approved,
                            selectdate!!
                        )
                    }


                }
            }
        })
    }
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val mdate = intent.getStringExtra(SELECTED_DATE)
            selectdate = mdate
            page = 1
            viewModel.getAppointmentsByDate(
                requireActivity(),
                page,
                AppointmentType.Approved,
                selectdate!!
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

    fun showDialog(mess: String, appointment: Appointment) {

        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.appointment_dailog_fragment)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtMessage = dialog.findViewById<TextView>(R.id.txtMessage)
        val txtDecline = dialog.findViewById<TextView>(R.id.txtDecline)
        val txtApprove = dialog.findViewById<TextView>(R.id.txtApprove)
        txtMessage.text = mess
        txtDecline.setOnClickListener {


            viewModel.updateAppointment(requireActivity(), appointment, AppointmentType.Canceled)

            dialog.dismiss()
        }
        txtApprove.setOnClickListener {

            viewModel.updateAppointment(requireActivity(), appointment, AppointmentType.Completed)
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }



}