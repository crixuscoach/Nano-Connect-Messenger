package com.example.peny.wallet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper.UP
import com.example.peny.GlobalConstants
import com.example.peny.R
import com.example.peny.messages.NewMessageActivity.Companion.USER_KEY
import com.google.gson.Gson
import com.rotilho.jnano.commons.*
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_confirm_send.*
import kotlinx.android.synthetic.main.activity_send.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.math.RoundingMode
import kotlin.random.Random.Default.Companion

class ConfirmSend : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_send)

        val account= SecurePreferences.getStringValue(this, GlobalConstants.accountAddress,"").toString()
        val amount= SecurePreferences.getStringValue(this, "222","").toString()
        val address= SecurePreferences.getStringValue(this, "111","").toString()

        destinationAddress_fill.setText(address)
        amountToSend_fill.setText(amount)

        nanoConfirmTransaction.setOnClickListener {
            val bal=amountToSend_fill.text.toString()
            val sendTransaction=SendTransaction(bal,address)
            val accountInfo = AccountInfo(
                "account_info",
                "true",
                account
            )
            sendLogistics(accountInfo,sendTransaction!!)
            Toast.makeText(getApplicationContext(), "You confirmed your transaction", Toast.LENGTH_LONG).show()
                fun onActive(){
                    Toast.makeText(getApplicationContext(), "You confirmed your transaction", Toast.LENGTH_LONG).show()
                }

        }

    }

    private fun sendLogistics(account_info: AccountInfo, sendTransaction: SendTransaction) {
        val url = "https://app.natrium.io/api"
        val gson = Gson()
        val client = OkHttpClient()
        val json = gson.toJson(account_info)
        val private_key = SecurePreferences.getStringValue(this, GlobalConstants.privateKey, "").toString()
        Log.d("TAG",private_key)
        val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResp = JSONObject(response.body?.string().toString())
                val a = account_info.account
                val frontier = jsonResp.get("frontier").toString()
                val balance1 = jsonResp.get("balance")
                val balanceRaw = NanoAmount.ofRaw(balance1.toString())
                Log.d("TAG","Ukupni trenutni balance je $balanceRaw")
                val decrease= NanoAmount.ofNano(sendTransaction.amount.toString())
                var balance=NanoAmount.ofRaw("1")

                if (balanceRaw.divide(decrease,RoundingMode.UP).toString()=="1")
                      balance=NanoAmount.ofRaw("0")
                else  balance=balanceRaw.subtract(decrease)


                Log.d("TAG","Smanjivanje balanca je $decrease")
                Log.d("TAG","Konacni saldo $balance")
                val representative = jsonResp.get("representative").toString()
                val link = NanoHelper.toHex(NanoAccounts.toPublicKey(sendTransaction.address.toString()))

                val test= NanoHelper.toByteArray(private_key)
                Log.d("TAG","$test")

                val hash = NanoBlocks.hashStateBlock(
                    account_info.account,
                    frontier,
                    representative,
                    balance,
                    sendTransaction.address.toString()
                )



                Log.d("TAG","$hash")
                val signature = NanoSignatures.sign(NanoHelper.toByteArray(private_key), hash)
                val block = Block(
                    "state",
                    account_info.account,
                    frontier,
                    representative,
                    balance.toString(),
                    link,
                    sendTransaction.address.toString(),
                    signature
                )
                Log.d("TAG", "$signature")
                val process = publishProcess(
                    true,
                    "process",
                    true,
                    "send",
                    block
                )
                publishBlock(process)
            }
        })
    }

    private fun publishBlock(process: publishProcess) {
        val url = "https://app.natrium.io/api"
        val gson = Gson()
        val client = OkHttpClient()
        val json = gson.toJson(process)
        val body =json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val get_body = response.body?.string().toString()
                val json_Resp = JSONObject(get_body)
                Log.d("TAG", "Uspjelo je i $json_Resp")
                if (!json_Resp.has("error"))
                    runOnUiThread() {
                        Toast.makeText(
                            getApplicationContext(),
                            "You confirmed your transaction",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    finish()
            }
        })
    }
}
