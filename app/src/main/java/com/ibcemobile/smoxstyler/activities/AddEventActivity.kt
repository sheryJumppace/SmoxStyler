package com.ibcemobile.smoxstyler.activities

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.android.volley.Request
import com.ibcemobile.smoxstyler.databinding.ActivityAddEventBinding
import com.ibcemobile.smoxstyler.manager.APIHandler
import com.ibcemobile.smoxstyler.manager.Constants
import com.ibcemobile.smoxstyler.utils.ImageUploadUtils
import com.ibcemobile.smoxstyler.utils.UploadImages
import org.json.JSONObject
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.data.EventRepository
import com.ibcemobile.smoxstyler.model.Event
import com.ibcemobile.smoxstyler.utils.EVENTS
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModel
import com.ibcemobile.smoxstyler.viewmodels.AppointmentListViewModelFactory
import com.ibcemobile.smoxstyler.viewmodels.EventListViewModel
import com.ibcemobile.smoxstyler.viewmodels.EventListViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AddEventActivity : BaseImagePickerActivity(), UploadImages {
    private lateinit var binding: ActivityAddEventBinding
    private var imageURL:String= String()
    private var eventId:String= ""
    private lateinit var viewModel: EventListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = EventListViewModelFactory(EventRepository.getInstance())
        viewModel = ViewModelProvider(this, factory).get(EventListViewModel::class.java)
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
        binding.image.setOnClickListener {
            didOpenPhotoOption()
        }
        binding.fabAdd.setOnClickListener {
            didOpenPhotoOption()
        }
        val dateFormat=SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        if (intent.hasExtra(EVENTS)){
            val gson = Gson()
            val service = gson.fromJson<Event>(intent.getStringExtra(EVENTS), Event::class.java)
            eventId=service.id.toString()
            Glide.with(this)
                .load(service.image)
                .into(binding.image)

            imageURL=service.image
            binding.etEventName.setText(service.name)
            binding.etEventDes.setText(service.event)
            binding.txtStartDate.setText(Constants.formatDate(dateFormat.format(service.startAt)))
            binding.txtEndDate.setText(Constants.formatDate(dateFormat.format(service.endAt)))
            binding.btnPost.text="Update"
            binding.txtTitleBar.text="Edit Event"
            binding.btnPost.setOnClickListener {
                validEvent(true)
            }

        }else{
            binding.txtTitleBar.text="Add Event"
            binding.btnPost.setOnClickListener {
                validEvent(false)
            }

        }

        binding.imgStartDate.setOnClickListener {
            openDatePickerDialog(binding.txtStartDate)
        }

        binding.imgEndDate.setOnClickListener {
            openDatePickerDialog(binding.txtEndDate)
        }
        viewModel.isUpdate.observe(this, androidx.lifecycle.Observer {
            if (it){
                finish()
            }
        })
    }

    private fun validEvent(isUpdate:Boolean) {
        when {
            TextUtils.isEmpty(imageURL) -> {
                Toast.makeText(this,"Select Event picture",Toast.LENGTH_LONG).show()
            }
            TextUtils.isEmpty(binding.etEventName.text.toString()) -> {
                binding.etEventName.error="Event Name"
                binding.etEventName.requestFocus()
            }
            TextUtils.isEmpty(binding.etEventDes.text.toString()) -> {
                binding.etEventDes.error="Event Description"
                binding.etEventDes.requestFocus()
            }
            TextUtils.isEmpty(binding.txtStartDate.text.toString()) -> {
                binding.txtStartDate.error="Select Start Date"
                binding.txtStartDate.requestFocus()
            }
            TextUtils.isEmpty(binding.txtEndDate.text.toString()) -> {
                binding.txtEndDate.error="Select End Date"
                binding.txtEndDate.requestFocus()
            }
            else -> {

                val event=Event()
                event.image=imageURL
                event.event=binding.etEventName.text.toString()
                event.message=binding.etEventDes.text.toString()
                event.start_date=binding.txtStartDate.text.toString()
                event.end_date=binding.txtEndDate.text.toString()

                if (isUpdate){
                    viewModel.updateEvent(this,event)
                }else{
                    viewModel.postEvent(this,event)
                }
            }
        }
    }

    private fun openDatePickerDialog(textView: TextView) {
        val myCalendar = Calendar.getInstance()
        val date =
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                textView.text = Constants.formatDate(sdf.format(myCalendar.time))
            }
        DatePickerDialog(
            this,R.style.MyAlertDialogStyle, date,
            myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH]
        ).show()

    }




    override fun upload(imageUrl: String) {
        imageURL=imageUrl
    }

    override fun onError() {

    }

    override fun didSelectPhoto(uri: Uri) {
        super.didSelectPhoto(uri)
        binding.image.setImageURI(uri)
        val imageUploadUtils = ImageUploadUtils()
        imageUploadUtils.onUpload(this,
            uri.path!!,
            this)
    }
}