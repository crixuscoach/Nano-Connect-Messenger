package com.example.peny.wallet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import com.example.peny.GlobalConstants
import com.example.peny.R
import com.example.peny.messages.ChatLogActivity
import com.example.peny.messages.NewMessageActivity
import com.rotilho.jnano.commons.NanoAmount
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_send.*

class SendActivity : AppCompatActivity() {
    companion object{
        val USER_KEY="USER_KEY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        nanoSendAmount.setOnClickListener {
            val amount=amountToSend.text.toString()
            val balanceSend= NanoAmount.ofNano(amount)
            val address=destinationAddress.text.toString()
            val sendTransaction=SendTransaction(amount,address)
            SecurePreferences.setValue(this, "111", address)
            SecurePreferences.setValue(this, "222", amount)
            val intent = Intent(this, ConfirmSend::class.java)
            startActivity(intent)
            finish()
        }
    }
}
