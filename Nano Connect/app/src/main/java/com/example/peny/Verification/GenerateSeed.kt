package com.example.peny.Verification

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.peny.GlobalConstants
import com.example.peny.R
import com.example.peny.messages.ChatActivity
import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.commons.NanoKeys
import com.rotilho.jnano.commons.NanoSeeds
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_generate_seed.*

class GenerateSeed : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate_seed)

        val seed= NanoSeeds.generateSeed()
        val privateKey= NanoKeys.createPrivateKey(seed,0)
        val publicKey= NanoKeys.createPublicKey(privateKey)
        val seed1= NanoHelper.toHex(seed)
        val priv2= NanoHelper.toHex(privateKey)
        val pub1= NanoHelper.toHex(publicKey)
        seed_notifier.setText(seed1)
        val account=NanoAccounts.createAccount(publicKey)

        button_copy.setOnClickListener {
            val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val text = seed_notifier.getText().toString()
            val myClip = ClipData.newPlainText("text", text)
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(getApplicationContext(), "Seed Copied", Toast.LENGTH_LONG).show()
        }
        next_button.setOnClickListener {
            SecurePreferences.setValue(this, GlobalConstants.seed, seed1)
            SecurePreferences.setValue(this, GlobalConstants.privateKey, priv2)
            SecurePreferences.setValue(this, GlobalConstants.publicKey, pub1)
            SecurePreferences.setValue(this, GlobalConstants.accountAddress, account)

            intent= Intent(this,VerifyPhone::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }
}

