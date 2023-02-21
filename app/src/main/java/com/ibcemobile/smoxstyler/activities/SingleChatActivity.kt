package com.ibcemobile.smoxstyler.activities

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.R
import com.google.gson.JsonObject
import com.ibcemobile.smoxstyler.*
import com.ibcemobile.smoxstyler.adapter.MessageAdapter
import com.ibcemobile.smoxstyler.databinding.ActivitySingleChatBinding
import com.ibcemobile.smoxstyler.model.Chats
import com.ibcemobile.smoxstyler.utils.ImageUploadUtils
import com.ibcemobile.smoxstyler.utils.UploadImages
import com.ibcemobile.smoxstyler.utils.getSmsTodayYestFromMilli
import com.ibcemobile.smoxstyler.viewmodels.UserViewModel
import kotlinx.coroutines.delay


class SingleChatActivity : BaseImagePickerActivity(), UploadImages {
    private var chatRoom:String?=null
    private var chatRoomId:String?=null
    private var userId :String?=null
    private var isOnline :Boolean?=false
    private var messageUnseenCount = 0
    lateinit var binding: ActivitySingleChatBinding
    lateinit var userViewModel: UserViewModel
    lateinit var dbRef: DatabaseReference
    var chatList: ArrayList<Chats>? = null
    lateinit var messageAdapter: MessageAdapter
    lateinit var chatChildEvent: ChildEventListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbRef = FirebaseDatabase.getInstance().reference.child(CHATS_TABLE)
        chatList = ArrayList()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        userViewModel= ViewModelProvider(this).get(UserViewModel::class.java)
        binding.etTypeHere.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                Handler(mainLooper).postDelayed({
                    updateStatus("online")
                },1500)

            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s != "") {
                    updateStatus("typing...")
                }
            }
        })
        binding.sendButton.setOnClickListener {
            if (binding.etTypeHere.text.toString().isNotEmpty()) {
                sendMessage(binding.etTypeHere.text.toString(), MESSAGE_TYPE_TEXT)
                binding.etTypeHere.setText("")
            }
        }
        binding.addImage.setOnClickListener {
            didOpenPhotoOption()
        }

        if (intent.getStringExtra(CHAT_ROOM) != null) {
            chatRoom = intent.getStringExtra(CHAT_ROOM).toString()
            chatRoomId = intent.getStringExtra(CHAT_ROOM_ID).toString()
            userId = intent.getStringExtra(USER_ID).toString()
            binding.username.text = intent.getStringExtra(USER_NAME).toString()
            messageUnseenCount= intent.getStringExtra(MESSAGE_COUNT)!!.toInt()
        }

        getUserStatus()
        setChatAdapter()
        getNewMessageEvent()
        moreOption()
    }

    private fun setChatAdapter() {
        binding.rvChat.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        binding.rvChat.layoutManager = linearLayoutManager
        messageAdapter = MessageAdapter(this@SingleChatActivity, chatList!!)
        binding.rvChat.adapter = messageAdapter
    }


    private fun getNewMessageEvent() {
        chatChildEvent = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chat: Chats? = snapshot.getValue(Chats::class.java)
                messageAdapter.addItem(chat)
                binding.rvChat.scrollToPosition(chatList?.size!!-1)
                updateMessageSeen(snapshot, chat!!)
                updateLastMessageCount("0")

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chat: Chats? = snapshot.getValue(Chats::class.java)
                //Log.e(TAG, "onChildChanged: "+chat?.is_seen +" message "+chat?.message)
                val pos=chatList?.indexOfFirst { it.id==chat?.id}
                chatList?.get(pos!!)?.is_seen =chat?.is_seen
                messageAdapter.notifyItemChanged(pos!!)


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
    }

    private fun sendMessage(message: String?, message_type: Int?) {
        val msgId = dbRef.child(chatRoom!!).push().key
        val chats=Chats()
        chats.id=msgId
        chats.sender_id=app.currentUser.id.toString()
        chats.receiver_id=userId
        chats.message=message
        chats.message_type=message_type!!
        chats.msg_time=System.currentTimeMillis()
        chats.is_seen=isOnline
        chats.sender_name=app.currentUser.name
        chats.sender_profile=app.currentUser.imageUrl
        dbRef.child(chatRoom!!).child(msgId!!).setValue(chats)
        messageUnseenCount++
        updateLastMessage(messageUnseenCount.toString(),message!!,System.currentTimeMillis().toString())
        //sendNotification(message)

    }

    private fun updateLastMessage(messageCount:String,message: String,lastMessageTime:String) {
        val database = FirebaseDatabase.getInstance().reference.child(CHAT_ROOM_TABLE).child(chatRoomId!!)
        database.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val hashMap = java.util.HashMap<String, Any>()
                hashMap["message_count"] = messageCount
                hashMap["last_message"] = message
                hashMap["last_message_time"] = lastMessageTime
                hashMap["sender_id"] = app.currentUser.id.toString()
                FirebaseDatabase.getInstance().getReference(CHAT_ROOM_TABLE).child(chatRoomId!!).updateChildren(hashMap)
            }
        }
    }
    private fun updateLastMessageCount(messageCount:String) {
        val database = FirebaseDatabase.getInstance().reference.child(CHAT_ROOM_TABLE).child(chatRoomId!!)
        database.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val hashMap = java.util.HashMap<String, Any>()
                hashMap["message_count"] = messageCount
                FirebaseDatabase.getInstance().getReference(CHAT_ROOM_TABLE).child(chatRoomId!!).updateChildren(hashMap)
            }
        }
    }

    private fun receiveAllMessage() {
        dbRef.child(chatRoom!!).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (task.result.exists()) {
                    chatList!!.clear()
                    for (snapshot in task.result.children) {
                        val chat: Chats? = snapshot.getValue(Chats::class.java)
                        updateMessageSeen(snapshot, chat!!)
                        chatList!!.add(chat)

                    }
                    messageAdapter.notifyDataSetChanged()
                    updateLastMessageCount("0")

                }
            }
        }
    }

    private fun updateMessageSeen(snapshot: DataSnapshot, chat: Chats) {
        if (chat.receiver_id == app.currentUser.id.toString()) {
            val hashMap = java.util.HashMap<String, Any>()
            hashMap["is_seen"] = true
            snapshot.ref.updateChildren(hashMap)
        }
    }

    override fun didSelectPhoto(uri: Uri) {
        super.didSelectPhoto(uri)
        progressHUD.show()
        val imageUploadUtils = ImageUploadUtils()
        imageUploadUtils.onUpload(
            this@SingleChatActivity,
            uri.path!!,
            this
        )
    }

    private fun updateStatus(status: String) {
        val reference =
            FirebaseDatabase.getInstance().getReference(USER_STATUS_TABLE).child(chatRoom!!)
                .child(app.currentUser.id.toString())
        val hashMap = java.util.HashMap<String, Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap)
    }


    private fun blockUser() {

    }

    override fun onResume() {
        super.onResume()
        updateStatus("online")
        receiveAllMessage()
        registerAllEvents()

    }

    override fun onPause() {
        super.onPause()
        updateStatus(System.currentTimeMillis().toString())
        unregisterAllEvents()
    }

    private fun unregisterAllEvents() {
        dbRef.child(chatRoom!!).removeEventListener(chatChildEvent)
    }

    private fun registerAllEvents() {
        dbRef.child(chatRoom!!).addChildEventListener(chatChildEvent)
    }

    companion object {
        const val TAG = "SingleChatActivity"
    }

    override fun upload(imageUrl: String) {
        progressHUD.dismiss()
        sendMessage(imageUrl, MESSAGE_TYPE_IMAGE)
    }

    override fun onError() {

    }
    /**
     * Get user Typing..,Online,Offline status
     */
    private fun getUserStatus() {
        val database = FirebaseDatabase.getInstance().getReference(USER_STATUS_TABLE)
        database.child(chatRoom!!).child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val status = dataSnapshot.child("status").getValue(
                            String::class.java
                        )
                        if (status!="online" && status!="typing..." && status!="last seen recently"){
                            binding.onlineStatus.text = getSmsTodayYestFromMilli(status!!.toLong())
                        }else{
                            binding.onlineStatus.text = status
                        }
                        isOnline = status=="online"

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }


    private fun sendNotification(message: String?){
        val jsonObject=JsonObject()
        jsonObject.addProperty("sender_id",app.currentUser.id)
        jsonObject.addProperty("receiver",userId)
        jsonObject.addProperty("title",app.currentUser.name)
        jsonObject.addProperty("message",message)
        userViewModel.sendNotification(this,jsonObject)
    }


    private fun moreOption() {
        binding.moreOption.setOnClickListener { view: View? ->
            val popup =
                PopupMenu(this, view!!, Gravity.START)
            popup.menuInflater.inflate(com.ibcemobile.smoxstyler.R.menu.chat_menu, popup.menu)
            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener { item: MenuItem ->
                if (item.title == "Block") {
                    blockUser()
                } else if (item.title == "Report") {
                    /*val bottomSheetAddressFragment = BottomSheetBlockDialog()
                    bottomSheetAddressFragment.setCancelable(true)
                    bottomSheetAddressFragment.show(
                        supportFragmentManager,
                        TAG
                    )*/
                }
                true
            }
            popup.show()
        }
    }
}