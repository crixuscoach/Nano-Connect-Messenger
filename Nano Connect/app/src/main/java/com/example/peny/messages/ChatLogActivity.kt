package com.example.peny.messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.peny.ChatMessage
import com.example.peny.GlobalConstants
import com.example.peny.R
import com.example.peny.User
import com.example.peny.wallet.ConfirmSend
import com.example.peny.wallet.SendTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.main_program.*
import kotlinx.android.synthetic.main.main_program.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG="Chat Log"
    }

    val adapter=GroupAdapter<ViewHolder>()
    val toUser:User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        chat_recycler.adapter=adapter

        //val username= intent.getStringExtra(NewMessageActivity.USER_KEY) //Setting user name
        val user= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title=user?.name   //Insert user name
        SecurePreferences.setValue(this, "111", user?.public_address.toString())
        SecurePreferences.setValue(this, "222", "0")
        listenForMessages()

        send_button.setOnClickListener {
            Log.d(TAG,"Attempt to send messageg${FirebaseAuth.getInstance().uid}, ${FirebaseAuth.getInstance().uid.toString()}")
            performSendMessage()
        }
    }
    private fun listenForMessages(){
        val fromid= FirebaseAuth.getInstance().uid
        val user= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId=user?.uid
        Log.d(TAG,"Nalazimo se u listen for messages $fromid,       $toId")
        val ref=FirebaseDatabase.getInstance().getReference("/user-messages/$fromid/$toId")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.d(TAG,"$fromid,       $toId")
                val chatMessage= p0.getValue(ChatMessage::class.java)
                if (chatMessage!=null) {
                    Log.d(TAG, chatMessage.text)
                    if (chatMessage.fromid==FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(chatMessage.text))
                        Log.d(TAG,"CHAT FROM ITEM message s ovog ID ${chatMessage.fromid},   na ovaj    ${FirebaseAuth.getInstance().uid}")
                        Log.d(TAG,"CHAT FROM ITEM message s ovog ID $fromid,   na ovaj    $toId")
                    }
                    else adapter.add(ChatToItem(chatMessage.text))
                        Log.d(TAG,"CHAT TO ITEM chhat message s ovog ID $fromid,   na ovaj    $toId")

                    Log.d(TAG,"Pozicioniranje")
                    chat_recycler.scrollToPosition(adapter.itemCount-1)
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }

    private fun performSendMessage(){
        val fromid=FirebaseAuth.getInstance().uid
        val user= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId=user?.uid
//      val reference= FirebaseDatabase.getInstance().getReference("/user-messages").push()
        val reference= FirebaseDatabase.getInstance().getReference("/user-messages/$fromid/$toId").push()
        val toReference= FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromid").push()

        val text= edit_text_chat.text.toString()

        if (fromid==null) return
        if (toId==null) return
        val time= System.currentTimeMillis()*1000

        val chatMessage= ChatMessage(reference.key!!,text,fromid,toId,time)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message ${reference.key}, ${toId.toString()}, $ ")
                edit_text_chat.text.clear()
                chat_recycler.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)

        val latestMessageReference=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromid/$toId")
        latestMessageReference.setValue(chatMessage)

        val latestMessageToRef=FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromid")
        latestMessageToRef.setValue(chatMessage)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menu_new_message ->{
                val intent = Intent(this, ConfirmSend::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

}

class ChatFromItem (val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text=text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem (val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text=text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}