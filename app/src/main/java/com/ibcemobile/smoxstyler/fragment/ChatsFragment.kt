package com.ibcemobile.smoxstyler.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.*
import com.ibcemobile.smoxstyler.*
import com.ibcemobile.smoxstyler.activities.SingleChatActivity
import com.ibcemobile.smoxstyler.adapter.ChatsListAdapter
import com.ibcemobile.smoxstyler.databinding.ChatFragmentBinding
import com.ibcemobile.smoxstyler.model.ChatUsers

class ChatsFragment : BaseFragment(), ChatsListAdapter.MyItemClickListener {
    private var binding: ChatFragmentBinding? = null
    lateinit var dbRef: DatabaseReference
    lateinit var chatRoomEvent:ValueEventListener
    var chatUserList: ArrayList<ChatUsers>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatFragmentBinding.inflate(inflater, container, false)

        binding!!.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        receiveUsers()
                    }
                    1 -> {
                    }
                    2 -> {
                    }
                    3 -> {
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })

        chatUserList = ArrayList()
        dbRef = FirebaseDatabase.getInstance().reference.child("chatRoom")
        receiveUsers()

        return binding!!.root
    }


    private fun receiveUsers() {
        Log.e("TAG", "calling receiveUsers")
        chatRoomEvent=object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("", "firebase onDataChange: ")
                if (dataSnapshot.exists()) {
                    binding!!.txtError.visibility = View.GONE
                    chatUserList!!.clear()
                    for (snapshot in dataSnapshot.children) {
                        val barberId: String = snapshot.child("barber_id").getValue(String::class.java)!!
                        if (barberId == app.currentUser.id.toString()) {
                            val chat: ChatUsers? = snapshot.getValue(ChatUsers::class.java)
                            chatUserList!!.add(chat!!)
                        }
                    }
                    val linearLayoutManager = LinearLayoutManager(
                        App.instance
                    )
                    binding!!.rvChat.layoutManager = linearLayoutManager
                    val chatUserAdapter = ChatsListAdapter(App.instance,this@ChatsFragment)
                    binding!!.rvChat.adapter = chatUserAdapter
                    chatUserAdapter.setData(chatUserList!!)
                    if (chatUserList!!.size > 0) {
                        binding!!.txtError.visibility = View.GONE
                        binding!!.rvChat.visibility = View.VISIBLE
                    } else {
                        binding!!.txtError.visibility = View.VISIBLE
                        binding!!.rvChat.visibility = View.GONE
                    }
                } else {
                    binding!!.txtError.visibility = View.VISIBLE
                    binding!!.rvChat.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("", "onCancelled: " + error.details)
            }

        }
    }


    override fun onResume() {
        super.onResume()
        dbRef.addValueEventListener(chatRoomEvent)

    }

    override fun onPause() {
        super.onPause()
        dbRef.removeEventListener(chatRoomEvent)
    }
    override fun clicked(users: ChatUsers) {
        val intent = Intent(requireActivity(), SingleChatActivity::class.java)
        intent.putExtra(CHAT_ROOM, users.chat_room_id)
        intent.putExtra(USER_ID, users.user_id)
        intent.putExtra(USER_NAME, users.user_name)
        intent.putExtra(CHAT_ROOM_ID, users.id)
        if (users.sender_id!!.toInt()==app.currentUser.id){
            intent.putExtra(MESSAGE_COUNT, users.message_count)
        }else{
            intent.putExtra(MESSAGE_COUNT, "0")
        }
        startActivity(intent)
    }
}