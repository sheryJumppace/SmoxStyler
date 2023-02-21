package com.ibcemobile.smoxstyler.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.ibcemobile.smoxstyler.MainActivity
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.ServiceBookingAdapter
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.databinding.ActivityPaymentBinding
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.viewmodels.AppointmentViewModel
import com.ibcemobile.smoxstyler.viewmodels.AppointmentViewModelFactory
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity(), DatePickerListener {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var viewModel: AppointmentViewModel
    private lateinit var appointment: Appointment
    private lateinit var adapter: ServiceBookingAdapter
    private var dynamicLinkUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.datePicker
            .setListener(this)
            .showTodayButton(false)
            .setDays(30)
            .setOffset(3)
            .setDateSelectedColor(resources.getColor(R.color.light_yellow))
            .setTodayDateBackgroundColor(resources.getColor(R.color.light_yellow))
            .init()

        binding.datePicker.setDate(DateTime().plusDays(0))

        binding.imgBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(
                R.anim.activity_enter,
                R.anim.activity_exit
            )
            finish()

        }
        binding.imgFb.setOnClickListener {
            openFb()
        }
        binding.imgText.setOnClickListener {
            openShare()
        }
        binding.imgWhatsApp.setOnClickListener {
            openWhatsapp()
        }
        binding.imgMail.setOnClickListener {
            openMail()
        }

        val id = intent.getIntExtra("appoint_id", 0)
        createDynamicLink(id)
        val factory = AppointmentViewModelFactory(AppointmentRepository.getInstance(), id)
        viewModel = ViewModelProvider(this, factory).get(AppointmentViewModel::class.java)
        viewModel.getAppointmentDetail(this)
        viewModel.appointmentDetail.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                appointment = it
                //Log.e("Appointment_Detail", appointment.toString())
                binding.serviceList.adapter = adapter
                adapter.submitList(appointment.services)
                binding.txtName.text = appointment.user.name
                binding.txtContact.text = appointment.user.phone
                binding.txtTimeSlot.text = appointment.getSlots()
                binding.txtDate.text = appointment.appointment_date
                binding.txtTotal.text = "$" + appointment.price.toString() + "0"
                //binding.txtComment.text=appointment.comment
                Glide.with(this)
                    .load(appointment.image)
                    .placeholder(R.drawable.small_placeholder)
                    .into(binding.imageCircle)


            }
        })
        adapter = ServiceBookingAdapter(true)
        binding.serviceList.layoutManager = LinearLayoutManager(this)

        binding.imgBack.setOnClickListener {
            onBackPressed()
        }


    }


    private fun createDynamicLink(id: Int) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://www.smox.page.link/open?appointmentId=$id"))
            .setDomainUriPrefix("https://smox.page.link") // Open links with this app on Android
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("com.smox.smoxuser").build()
            ) // Open links with com.example.ios on iOS
            .setIosParameters(DynamicLink.IosParameters.Builder("com.smox.smoxuser").build())
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle("Appointment Booked for " + binding.txtDate.text.toString())
                    .setDescription(binding.txtName.text.toString() + " Booked your appointment please Checkout here...")
                    .setImageUrl(Uri.parse("https://i.ibb.co/qy04HPC/Smox-Trimsetters-atom-July-15-2021.png"))
                    .build()
            )
            .buildShortDynamicLink()
            .addOnSuccessListener { shortDynamicLink ->
                dynamicLinkUri = shortDynamicLink.shortLink.toString()
                Log.e("AAA", "test1 success $dynamicLinkUri")

                //shareIntent(mInvitationUrl)
            }
            .addOnFailureListener {
                //Log.e("AAA", "test1 fail")
                it.printStackTrace()
            }
    }


    private fun openShare() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Please checkout the IBCMobile payment link $dynamicLinkUri"
        )
        startActivity(Intent.createChooser(sharingIntent, "Share using"))
    }

    private fun openFb() {
        val message = "Text I want to share."
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(
            Intent.createChooser(
                share,
                "Please checkout the IBCMobile payment link $dynamicLinkUri"
            )
        )
    }

    private fun openMail() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_EMAIL, "")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Please checkout the IBCMobile payment link $dynamicLinkUri"
        )
        startActivity(Intent.createChooser(intent, "Send Email"))

    }

    private fun openWhatsapp() {
        val whatsappIntent = Intent(Intent.ACTION_SEND)
        whatsappIntent.type = "text/plain"
        whatsappIntent.setPackage("com.whatsapp")
        whatsappIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Please checkout the IBCMobile payment link $dynamicLinkUri"
        )
        try {
            startActivity(whatsappIntent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDateSelected(dateSelected: DateTime) {
        Log.e(
            "HorizontalPicker",
            "Selected date is " + getFormattedDateFromTimestamp(dateSelected.millis)
        )
    }

    companion object {
        fun getFormattedDateFromTimestamp(timestampInMilliSeconds: Long): String {
            val date = Date()
            date.time = timestampInMilliSeconds
            return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.activity_enter,
            R.anim.activity_exit
        )
        finish()
    }
}