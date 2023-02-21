package com.ibcemobile.smoxstyler.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.ibcemobile.smoxstyler.*
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.ServiceBookingAdapter
import com.ibcemobile.smoxstyler.adapter.loadCircleImage
import com.ibcemobile.smoxstyler.data.AppointmentRepository
import com.ibcemobile.smoxstyler.databinding.ActivityAppointmentDetailBinding
import com.ibcemobile.smoxstyler.model.Appointment
import com.ibcemobile.smoxstyler.model.ChatUsers
import com.ibcemobile.smoxstyler.model.type.AppointmentType
import com.ibcemobile.smoxstyler.utils.APPOINTMENT_ID
import com.ibcemobile.smoxstyler.utils.FROM_LAST
import com.ibcemobile.smoxstyler.viewmodels.AppointmentViewModel
import com.ibcemobile.smoxstyler.viewmodels.AppointmentViewModelFactory


class AppointmentDetailActivity : BaseActivity() {


    private lateinit var viewModel: AppointmentViewModel
    private lateinit var adapter: ServiceBookingAdapter
    private lateinit var appointment: Appointment
    private lateinit var binding: ActivityAppointmentDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id = intent.getIntExtra(APPOINTMENT_ID, 0)
        val factory = AppointmentViewModelFactory(AppointmentRepository.getInstance(), id)
        viewModel = ViewModelProvider(this, factory).get(AppointmentViewModel::class.java)


        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.getIntExtra(FROM_LAST, 0) == 3) {
            binding.txtCancel.visibility = View.GONE
        }else if (intent.getIntExtra(FROM_LAST, 0) == 2){
            binding.txtCancel.visibility = View.VISIBLE
            initCancelCompleteButton(getString(R.string.txt_approved))
        }else{
            initCancelCompleteButton(getString(R.string.txt_complete))
        }

        initObserver()
        viewModel.getAppointmentDetail(this)
        setServiceAdapter()

    }

    private fun initCancelCompleteButton(string: String) {
        binding.btnComplete.text = string
        binding.btnComplete.setOnClickListener {
            viewModel.updateAppointment.observe(this, Observer {
                if (it != null) {
                    if (!it)
                        finish()
                }
            })
            viewModel.updateAppointment(this, appointment, AppointmentType.Completed)

        }
        binding.btnCancel.setOnClickListener {

            viewModel.updateAppointment.observe(this, Observer {
                if (it != null) {
                    if (!it)
                        finish()
                }
            })
            viewModel.updateAppointment(this, appointment, AppointmentType.Canceled)

        }
        binding.fabChat.setOnClickListener {
            if (appointment.customerId == 0 || appointment.customerId.toString().isEmpty()) {
                showAlertDialog(getString(R.string.no_customer_registered))
            } else{
               val chatRoom = if(appointment.barberId < appointment.customerId){
                    appointment.barberId.toString()+appointment.customerId.toString()
                }else{
                    appointment.customerId.toString()+appointment.barberId.toString()
                }
                createChatRoom(chatRoom)
            }
        }
    }

    private fun initObserver() {
        viewModel.appointmentDetail.observe(this) {
            if (it != null) {
                appointment = it
                adapter.submitList(appointment.services)
                binding.txtName.text = appointment.user.firstName
                binding.txtContact.text = appointment.user.phone
                binding.txtTimeSlot.text = appointment.getSlots()
                binding.txtDate.text = appointment.appointment_date
                binding.txtTotal.text = getString(R.string.format_price,appointment.price.toDouble())
                loadCircleImage(
                    binding.imageCircle,
                    appointment.user.image,
                    resources.getDrawable(R.drawable.small_placeholder),
                    true
                )
            }
        }



    }

    private fun setServiceAdapter() {
        binding.serviceList.layoutManager = LinearLayoutManager(this)
        adapter = ServiceBookingAdapter(true)
        binding.serviceList.adapter = adapter
        binding.txtContact.setOnClickListener {
            contactWithPhone(binding.txtContact.text.toString())
        }
    }

    private fun contactWithPhone(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
        startActivity(intent)
    }

    private fun updateProfile(chatRoomId:String,receiver_profile: String?) {

        val database = FirebaseDatabase.getInstance().reference.child(CHAT_ROOM_TABLE).child(chatRoomId)
        database.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val hashMap = java.util.HashMap<String, Any>()
                hashMap["receiver_profile"] = receiver_profile.toString()
                FirebaseDatabase.getInstance().getReference(CHAT_ROOM_TABLE).child(chatRoomId).updateChildren(hashMap)
            }
        }
    }

    private fun createChatRoom(chat_room: String?) {
        val dbRef = FirebaseDatabase.getInstance().reference.child(CHAT_ROOM)
        val query: Query = dbRef
            .orderByChild(CHATROOM_ID)
            .equalTo(chat_room)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.childrenCount > 0) {
                    var chatKey = ""
                    for (snapshot in dataSnapshot.children) {
                        chatKey = snapshot.child("id").getValue(String::class.java)!!
                    }
                    updateProfile(chatKey,appointment.user.image)
                    val intent =
                        Intent(this@AppointmentDetailActivity, SingleChatActivity::class.java)
                    intent.putExtra(CHAT_ROOM, chat_room)
                    intent.putExtra(USER_ID, appointment.customerId.toString())
                    intent.putExtra(USER_NAME, appointment.user.name)
                    intent.putExtra(CHAT_ROOM_ID, chatKey)
                    intent.putExtra(MESSAGE_COUNT, "0")
                    startActivity(intent)

                } else {
                    val msgId = dbRef.push().key
                    val chatUsers=ChatUsers()
                    chatUsers.id=msgId.toString()
                    chatUsers.last_message="Hay there! Connect with me."
                    chatUsers.last_message_time=System.currentTimeMillis().toString()
                    chatUsers.message_count="0"
                    chatUsers.chat_room_id=chat_room
                    chatUsers.user_name=appointment.user.name
                    chatUsers.barber_name=app.currentUser.name
                    chatUsers.user_id=appointment.customerId.toString()
                    chatUsers.barber_id=appointment.barberId.toString()
                    chatUsers.sender_id=appointment.barberId.toString()
                    chatUsers.receiver_profile=appointment.user.image
                    chatUsers.sender_profile=app.currentUser.image
                    dbRef.child(msgId!!).setValue(chatUsers)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val intent = Intent(
                                    this@AppointmentDetailActivity,
                                    SingleChatActivity::class.java
                                )
                                intent.putExtra(CHAT_ROOM, chat_room)
                                intent.putExtra(USER_ID, appointment.customerId.toString())
                                intent.putExtra(USER_NAME, appointment.user.name)
                                intent.putExtra(CHAT_ROOM_ID, msgId.toString())
                                intent.putExtra(MESSAGE_COUNT, "0")
                                startActivity(intent)

                            } else {
                                Log.d("TAG", task.exception?.message!!)
                            }
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


    }
}





