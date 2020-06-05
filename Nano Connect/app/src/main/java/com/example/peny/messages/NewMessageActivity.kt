package com.example.peny.messages

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.peny.GlobalConstants
import com.example.peny.R
import com.example.peny.User
import com.example.peny.utils.CountryToPhonePrefix
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import de.adorsys.android.securestoragelibrary.SecurePreferences
import de.adorsys.android.securestoragelibrary.SecurePreferences.getStringValue
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {
    var contactList= mutableListOf<String>()
    private var permissions= arrayOf(android.Manifest.permission.WRITE_CONTACTS, android.Manifest.permission.READ_CONTACTS)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        getPermissions(permissions)
        supportActionBar?.title = "Select User"
        var phoneList = ArrayList<MutableList<String>>()
        var contactList = ArrayList<MutableList<String>>()

        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val iso = telephonyManager.networkCountryIso
        val country_code= CountryToPhonePrefix(iso).toString()
        fetchUsers(country_code)
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers(country_code:String) {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    Log.d("New Message", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null)
                        {if (checkPhoneBook(this@NewMessageActivity,user.phone_number,country_code))
                        {user.name=getStringValue(this@NewMessageActivity,"54456456456121238694898","").toString()
                            adapter.add(UserItem(user))
                            }
                        }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user.name)   //pointer on base
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }
                recycle_view_new_message.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }
        )
    }

    class UserItem(val user: User) : Item<ViewHolder>() {
        override fun bind(
            viewHolder: ViewHolder,
            position: Int
        ) {// will be called for each object later on
            viewHolder.itemView.username_text_view_new_mes.text = user.name
        }

        override fun getLayout(): Int {
            return R.layout.user_row_new_message //Row
        }
    }

    private fun checkPhoneBook(context:Context, number:String,ISOPrefix:String):Boolean {
        if (number != null)
        {
            val cr = context.getContentResolver()
            val curContacts = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC")
            if (curContacts != null) {
                while (curContacts.moveToNext()) {
                    val name= curContacts.getString(curContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    var contactNumber = curContacts.getString(curContacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    contactNumber = contactNumber.replace(" ", "")
                    contactNumber = contactNumber.replace("-", "")
                    contactNumber = contactNumber.replace("(", "")
                    contactNumber = contactNumber.replace(")", "")
                    if (contactNumber.substring(0, 1) == "0")
                        contactNumber = contactNumber.substring(1)
                    if ((contactNumber.get(0)).toString() != "+")
                        contactNumber = ISOPrefix + contactNumber
                    if (number == contactNumber) {
                        if (!contactList.contains(contactNumber)) {
                            SecurePreferences.setValue(this, "54456456456121238694898", name)
                            Log.d("TAG","$number, $contactNumber")
                            contactList.add(number)
                            return true
                        }
                    }
                }
            }
            return false
        }
        else
        {
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getPermissions(permissions: Array<String>)
    {
        requestPermissions(this.permissions,1)

    }
}
