package com.example.peny.messages

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.system.StructTimespec
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.peny.*
import com.example.peny.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.main_program.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
private var permissions= arrayOf(android.Manifest.permission.WRITE_CONTACTS, android.Manifest.permission.READ_CONTACTS)

@RequiresApi(Build.VERSION_CODES.M)
class ChatActivity:AppCompatActivity() {
    private var permissions= arrayOf(android.Manifest.permission.WRITE_CONTACTS, android.Manifest.permission.READ_CONTACTS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_program)
        getPermissions(permissions)
        recycler_view_latest_messages.adapter=adapter
        val account= SecurePreferences.getStringValue(this, GlobalConstants.accountAddress,"")

        recycler_view_latest_messages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))

        newMessagesButton.setOnClickListener {
            val intent=Intent(this, NewMessageActivity::class.java)
            startActivity(intent)
        }

        adapter.setOnItemClickListener { item, view ->
            val intent=Intent(this,ChatLogActivity::class.java)
            val row=item as LatestMessageRow
            intent.putExtra(NewMessageActivity.USER_KEY,row.chatPartnerUser)
            startActivity(intent)
        }
        val uid= FirebaseAuth.getInstance().uid ?:""
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")

        listenForLatestMessages()
        verifyUserIsLoggedIn()

        button.setOnClickListener {
            val intent=Intent(this, WalletActivity::class.java)
            startActivity(intent)
        }

    }

    val latestMessagesMap=HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun  listenForLatestMessages()
    {
        val fromId= FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage=p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!]=chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage=p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!]=chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    val adapter= GroupAdapter<ViewHolder>()

    private fun verifyUserIsLoggedIn(){
        val uid=FirebaseAuth.getInstance().uid
        if (uid== null){
            val intent= Intent(this, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getPermissions(permissions: Array<String>)
    {
        requestPermissions(this.permissions,1)
    }
}


