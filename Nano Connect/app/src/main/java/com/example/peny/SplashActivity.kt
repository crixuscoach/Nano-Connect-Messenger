package com.example.peny

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Contacts.SettingsColumns.KEY
import de.adorsys.android.securestoragelibrary.SecurePreferences

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val private_key="111"
        SecurePreferences.setValue(this, private_key, "PLAIN_MESSAGE")
        // or
        val decryptedMessage = SecurePreferences.getStringValue(this, private_key, "")

    }
}
