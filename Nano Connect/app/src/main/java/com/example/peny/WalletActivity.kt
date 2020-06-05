package com.example.peny

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.peny.wallet.*
import com.google.gson.Gson
import com.rotilho.jnano.commons.*
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_wallet.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.math.BigInteger

class WalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        val account= SecurePreferences.getStringValue(this, GlobalConstants.accountAddress,"").toString()
        button_recieve.setOnClickListener {
            intent= Intent(this, ReceiveActivity::class.java)
            startActivity(intent)
        }
        button_send.setOnClickListener {
            intent= Intent(this, SendActivity::class.java)
            startActivity(intent)
        }
        val accountInfo = AccountInfo(
                "account_info",
                "true",
                account
        )

        getBalance(accountInfo)

        val pendingInfo = PendingInfo(
                "pending",
                account,
                1,
                true,
                true
        )
            //sendLogistics(accountInfo,destination_account)
        receiveLogistics(accountInfo, pendingInfo)
    }

    private fun receiveLogistics(accountInfo: AccountInfo, pendingInfo: PendingInfo) {
        val url = "https://app.natrium.io/api"
        val gson = Gson()
        val client = OkHttpClient()
        val json = gson.toJson(pendingInfo)
        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response) {
                val jsonResp = JSONObject(response.body?.string().toString())
                val resp = jsonResp.get("blocks").toString()
                if (resp=="")
                    return
                val link4=resp.replace("}","")
                val link5=link4.replace("{","")
                val yourArray: List<String> = link5.split(":")
                val link3=yourArray[0].toString()
                val link= link3.replace("\"", "").toString()

                val sendBal=yourArray[1].toString()
                val sendBalance=sendBal.replace("\"", "").toString()
                val balancePending= NanoAmount.ofRaw(sendBalance.toString())
                receiveTransaction(accountInfo,pendingInfo,link,balancePending)
            }
        })
    }

    private fun receiveTransaction(accountInfo: AccountInfo,pendingInfo: PendingInfo,link:String,sendBalance:NanoAmount)
    {
        val url = "https://app.natrium.io/api"
        val gson = Gson()
        val client = OkHttpClient()
        val json = gson.toJson(accountInfo)
        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response) {
                val jsonResp = JSONObject(response.body?.string().toString())

                if (jsonResp.has("error")) {
                    if (jsonResp.get("error") == "Account not found") {
                        val representative="nano_1natrium1o3z5519ifou7xii8crpxpk8y65qmkih8e8bpsjri651oza8imdd"
                        val hash = NanoBlocks.hashStateBlock(
                            accountInfo.account,
                            "0",
                            representative,
                            sendBalance,
                            link
                        )
                        val private_key= SecurePreferences.getStringValue(this@WalletActivity, GlobalConstants.privateKey,"").toString()
                        val signature = NanoSignatures.sign(NanoHelper.toByteArray(private_key), hash)
                        val block = BlockReceive(
                            "state",
                            accountInfo.account,
                            "0",
                            representative,
                            sendBalance.toString(),
                            link,
                            signature
                        )
                        val process = publishProcessReceive(
                            true,
                            "process",
                            true,
                            "open",
                            block)
                        Log.d("TAG","$process")
                        publishBlockReceive(process)
                    }
                }
                else {
                    val frontier = jsonResp.get("frontier").toString()
                    val balance = jsonResp.get("balance")
                    val balanceRaw = NanoAmount.ofRaw(balance.toString())
                    val representative = jsonResp.get("representative").toString()
                    val private_key= SecurePreferences.getStringValue(this@WalletActivity, GlobalConstants.privateKey,"").toString()
                    val totalBalance = balanceRaw.add(sendBalance)
                    val hash = NanoBlocks.hashStateBlock(
                        accountInfo.account,
                        frontier,
                        representative,
                        totalBalance,
                        link
                    )
                    val signature = NanoSignatures.sign(NanoHelper.toByteArray(private_key), hash)
                    val block = BlockReceive(
                        "state",
                        accountInfo.account,
                        frontier,
                        representative,
                        totalBalance.toString(),
                        link,
                        signature
                    )
                    Log.d("TAG", "$signature")
                    val process = publishProcessReceive(
                        true,
                        "process",
                        true,
                        "receive",
                        block
                    )
                    publishBlockReceive(process)
                }
            }
        })
    }
    public fun publishBlockReceive(process:publishProcessReceive){
        val url="https://app.natrium.io/api"
        val gson= Gson()
        val client= OkHttpClient()
        val json=gson.toJson(process)
        Log.d("TAG","$process")
        val body= json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        Log.d("TAG","$json")
        val request= Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response) {
                val get_body= response.body?.string().toString()
                val json_Resp= JSONObject(get_body)
                runOnUiThread() {
                    Toast.makeText(
                        getApplicationContext(),
                        "You just received some NANO",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun getBalance(accountInfo: AccountInfo)
    {
        val url = "https://app.natrium.io/api"
        val gson = Gson()
        val client = OkHttpClient()
        val json = gson.toJson(accountInfo)
        val body =
            json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }
            override fun onResponse(call: Call, response: Response) {
                val jsonResp = JSONObject(response.body?.string().toString())
                if (jsonResp.has("error")) {
                    SecurePreferences.setValue(this@WalletActivity,"777777777777777" ,"0" )
                    runOnUiThread {
                        balanceOfNano.setText("0")
                    }
                    return
                }
                else
                {
                    val balance = jsonResp.get("balance").toString()
                    val Nano=NanoAmount.ofRaw(balance).toNano().toString()
                    SecurePreferences.setValue(this@WalletActivity,"777777777777777" ,Nano )

                    runOnUiThread {
                        balanceOfNano.setText(Nano)
                    }
                }
            }})
    }
}
interface GlobalConstants {
    companion object {
        val seed="382795447a0d94430098087385de537b"
        val privateKey="4c6d74b10030a66fb48a058228436721"
        val publicKey="80d148e5e17e4b8bb6aa166f5aa4860d"
        val accountAddress="cac8d984352b3f61a9051a302d5999f3"
        val password="06846bd1abdb6b21a15e4368ad79bb48"
    }
}






