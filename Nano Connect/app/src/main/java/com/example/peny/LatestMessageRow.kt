package com.example.peny

import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){
    var chatPartnerUser:User?=null

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.latest_message.text= chatMessage.text
        val chatPartnerId:String

        if (chatMessage.fromid== FirebaseAuth.getInstance().uid){
            chatPartnerId=chatMessage.toId
        }
        else chatPartnerId=chatMessage.fromid
        val ref= FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser=p0.getValue((User::class.java))
            //    val name= getName(chatPartnerUser?.phone_number.toString(),"")
                viewHolder.itemView.latest_message_user.text=chatPartnerUser?.name
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    private fun getName(context: Context,number:String,ISOPrefix:String):String
    {
            val cr = context.getContentResolver()
            val curContacts = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC")
            if (curContacts != null) {
                while (curContacts.moveToNext()) {
                    val name= curContacts.getString(curContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    var contactNumber = curContacts.getString(curContacts.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER))
                    contactNumber = contactNumber.replace(" ", "")
                    contactNumber = contactNumber.replace("-", "")
                    contactNumber = contactNumber.replace("(", "")
                    contactNumber = contactNumber.replace(")", "")
                    if (contactNumber.substring(0, 1) == "0")
                        contactNumber = contactNumber.substring(1)
                    if ((contactNumber.get(0)).toString() != "+")
                        contactNumber = ISOPrefix + contactNumber
                    if (number == contactNumber) {
                        return name
                        }
                    }
                }
        return ""
    }
}