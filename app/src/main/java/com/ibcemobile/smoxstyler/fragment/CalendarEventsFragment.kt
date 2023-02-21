package com.ibcemobile.smoxstyler.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.google.gson.Gson
import com.ibcemobile.smoxstyler.App
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.AddEventActivity
import com.ibcemobile.smoxstyler.adapter.EventPostAdapter
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.databinding.FragmentEventsBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.manager.ObservingService
import com.ibcemobile.smoxstyler.model.Event
import com.ibcemobile.smoxstyler.utils.EVENTS
import com.ibcemobile.smoxstyler.utils.currentDate
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModel
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModelFactory
import org.json.JSONObject


class CalendarEventsFragment : BaseFragment(), EventPostAdapter.EventActions,
    CalenderFragment.EventDateChangeListener {
    private lateinit var binding: FragmentEventsBinding
    var eventsList: ArrayList<Event> = ArrayList()
    private var event: Event? = null
    private var pos = 0
    private lateinit var eventPostAdapter: EventPostAdapter
    private lateinit var viewModel: AppointmentListViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentEventsBinding.inflate(inflater, container, false)
        val factory = AppointmentListViewModelFactory(AppointmentRepository.getInstance())
        viewModel = ViewModelProvider(this, factory).get(AppointmentListViewModel::class.java)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CalenderFragment.eventDateClick(object : CalenderFragment.EventDateChangeListener {
            override fun ondateChanded(selectDate: String) {
                Log.e("selectDate", selectDate)
                if (selectDate.isEmpty()) {
                    viewModel.fetchList(requireActivity(), currentDate(), app.currentUser.id)
                } else {
                    viewModel.fetchList(requireActivity(), selectDate, app.currentUser.id)
                }
            }
        })
        val refreshLayout = binding.swipeRefreshLayout
        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = false
            viewModel.fetchList(requireActivity(), currentDate(), app.currentUser.id)
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

    private fun initAdapter(){
        eventPostAdapter = EventPostAdapter(this)
        binding.rvEvent.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvent.adapter = eventPostAdapter
    }
    private fun initObserver(){
        viewModel.events.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                eventsList.clear()
                eventsList.addAll(it)
                eventPostAdapter.submitList(eventsList)
                eventPostAdapter.notifyDataSetChanged()
                App.instance.unreadEvents = 0
                ObservingService.getInstance()
                    .postNotification(Constants.KLocalBroadCast.event, "")
                binding.tvNotFound.visibility = View.GONE
            } else {
                binding.tvNotFound.visibility = View.VISIBLE
                binding.tvNotFound.text = getString(R.string.no_events)
            }

        })
    }


    override fun onResume() {
        super.onResume()
        viewModel.fetchList(requireActivity(), currentDate(), app.currentUser.id)
    }


    override fun onDeleteClick(pos: Int) {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyAlertDialogStyle)
        builder.setTitle("Warning!")
        builder.setMessage("Are you sure you want to delete this event?")
        builder.setPositiveButton("Ok") { _, _ ->
            doRequestForDelete(pos)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    override fun onEditClick(event: Event, pos: Int) {
        this.event = event
        this.pos = pos
        val intent = Intent(activity, AddEventActivity::class.java)
        intent.putExtra(EVENTS, Gson().toJson(event))
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)

    }


    private fun doRequestForDelete(pos: Int) {
        progressHUD.show()
        val params = HashMap<String, String>()
        APIHandler(
            requireActivity(),
            Request.Method.DELETE,
            Constants.API.delete_event + "/" + eventsList[pos].id,
            params,
            object : APIHandler.NetworkListener {
                override fun onResult(result: JSONObject) {
                    progressHUD.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        result.getString("message"), Toast.LENGTH_LONG
                    ).show()
                    if (!(result.getBoolean("error"))) {
                        viewModel.fetchList(requireActivity(), currentDate(), app.currentUser.id)

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


    override fun ondateChanded(selectDate: String) {
        viewModel.fetchList(requireActivity(), selectDate, app.currentUser.id)
    }


}