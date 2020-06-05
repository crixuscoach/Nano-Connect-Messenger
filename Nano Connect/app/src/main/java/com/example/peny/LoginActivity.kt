package com.example.peny

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.peny.messages.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button.setOnClickListener {

            val email = email_text_login.text.toString()
            val password=password_text_login.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(){
                    if (it.isSuccessful) {
                        Log.d("LoginActivity", "Uspješno je ulogiran korisnik")
                        val intent = Intent(this, ChatActivity::class.java)
                        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    else {
                        Log.d("LoginActivity", "Wrong data for log in")
                        Toast.makeText(this, "Please insert correct e-mail and password",Toast.LENGTH_LONG).show()
                    }
                }
        }

        back_to_registration.setOnClickListener {
            finish()
        }

    }
}