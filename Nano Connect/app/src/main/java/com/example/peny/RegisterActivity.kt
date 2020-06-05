package com.example.peny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.peny.Verification.GenerateSeed
import com.example.peny.Verification.VerifyPhone
import com.example.peny.messages.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoKeys
import com.rotilho.jnano.commons.NanoSeeds
import kotlinx.android.synthetic.main.activity_main.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        next_button_1.setOnClickListener {
            Log.d("MainActivity", "Gumb stisnut")    //launch the login activity
            val intent = Intent(this, GenerateSeed::class.java)
            startActivity(intent)
        }

    }
}


