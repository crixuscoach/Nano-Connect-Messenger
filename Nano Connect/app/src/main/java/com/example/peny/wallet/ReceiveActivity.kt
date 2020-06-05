package com.example.peny.wallet

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.peny.GlobalConstants
import com.example.peny.R
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_generate_seed.*
import kotlinx.android.synthetic.main.activity_receive.*

class ReceiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)
        val account= SecurePreferences.getStringValue(this, GlobalConstants.accountAddress,"").toString()
        accountReceive12.setText(account).toString()
        copy_receive.setOnClickListener {
            val myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val text = accountReceive12.getText().toString()
            val myClip = ClipData.newPlainText("text", text)
            myClipboard.setPrimaryClip(myClip)
            Toast.makeText(getApplicationContext(), "Address copied", Toast.LENGTH_LONG).show()
        }
        GenerateQRFunction(account)
    }
    private fun GenerateQRFunction(account:String) {
        try
        {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(account, BarcodeFormat.QR_CODE, 800, 800)
            QrCodeImage.setImageBitmap(bitmap)
        }
        catch (e:Exception) {
        }
    }
}
