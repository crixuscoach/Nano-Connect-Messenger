package com.example.peny.messages

import android.util.Log
import com.example.peny.GlobalConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import de.adorsys.android.securestoragelibrary.SecurePreferences


class MyFirebaseInstanceIDService: FirebaseMessagingService()
{

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("TAG","Refreshed token, $p0")
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val accountAddress = SecurePreferences.getStringValue(this, GlobalConstants.accountAddress, "").toString()
    }
}


