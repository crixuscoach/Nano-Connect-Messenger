package com.example.peny.Verification

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.peny.GlobalConstants
import com.example.peny.R
import com.example.peny.User
import com.example.peny.messages.ChatActivity
import com.example.peny.utils.CountryToPhonePrefix
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.rotilho.jnano.commons.NanoAccounts
import com.rotilho.jnano.commons.NanoHelper
import com.rotilho.jnano.commons.NanoKeys
import com.rotilho.jnano.commons.NanoSeeds
import de.adorsys.android.securestoragelibrary.SecurePreferences
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_phone_verification.*
import org.bson.Document
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest

class VerifyPhone : AppCompatActivity() {

    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mAuth: FirebaseAuth
    private var permissions= arrayOf(android.Manifest.permission.WRITE_CONTACTS, android.Manifest.permission.READ_CONTACTS)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verification )
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val iso = telephonyManager.networkCountryIso
        val country_code= CountryToPhonePrefix(iso)
        editText2.setText(country_code)

        button_send_verification.setOnClickListener {
            val phoneNumber=country_code + phoneNumberVerification.text.toString()
            Log.d("Tag","Stisnut je gumb i broj je: $phoneNumber")
            verificationCallbacks()
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                callbacks) // OnVerificationStateChangedCallbacks
        }
    }

    private fun verificationCallbacks(){
        callbacks=object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    signIn(p0)
            }
            override fun onVerificationFailed(p0: FirebaseException) {
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            }
        }
    }
    private fun signIn(p0:PhoneAuthCredential){
        mAuth=FirebaseAuth.getInstance()
        mAuth.signInWithCredential(p0)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    saveToDatabase()
                    Log.d("Provjera","Uspjesni Sign in")
                    val user=it.result?.user
                    val intent= Intent(this, ChatActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)}
                else return@addOnCompleteListener
            }
    }

    private fun getPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions,1)
        }
    }
    private fun saveToDatabase()
    {
        val uid= FirebaseAuth.getInstance().uid ?:""
        val ref= FirebaseDatabase.getInstance().getReference("/users/$uid")
        val accountAddress= SecurePreferences.getStringValue(this, GlobalConstants.accountAddress,"").toString()
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val iso = telephonyManager.networkCountryIso
        val country_code= CountryToPhonePrefix(iso)
        editText2.setText(country_code)
        val phone_number=country_code + phoneNumberVerification.text.toString()
        val user= User(uid, phone_number , accountAddress,"")
        ref.setValue(user)      //saving to the database
            .addOnSuccessListener {
            }
        val uri = "mongodb://superset:superset1234@mongodb-shard-00-00-ad8uu.mongodb.net:27017,mongodb-shard-00-01-ad8uu.mongodb.net:27017,mongodb-shard-00-02-ad8uu.mongodb.net:27017/test?ssl=true&replicaSet=Mongodb-shard-0&authSource=admin&retryWrites=true&w=majority"
        val clientURI = MongoClientURI(uri)
        val mongoClient = MongoClient(clientURI)
        val mongoDatabase = mongoClient.getDatabase("nanotalk")
        val collection = mongoDatabase.getCollection("base")
        val document = Document("Account_address", "adresa1")
        val thread= ExampleThread(uri,accountAddress)
        thread.start()
            }
        }
internal class ExampleThread:Thread {
    var uri:String
    var account:String
    constructor(uri:String, account:String): super(){
        this.uri = uri
        this.account=account
    }
    public override fun run() {
        val uri =
            "mongodb://superset:superset1234@mongodb-shard-00-00-ad8uu.mongodb.net:27017,mongodb-shard-00-01-ad8uu.mongodb.net:27017,mongodb-shard-00-02-ad8uu.mongodb.net:27017/test?ssl=true&replicaSet=Mongodb-shard-0&authSource=admin&retryWrites=true&w=majority"
        val clientURI = MongoClientURI(uri)
        val mongoClient = MongoClient(clientURI)
        val uid= FirebaseAuth.getInstance().uid ?:""
        val mongoDatabase = mongoClient.getDatabase("nanotalk")
        val collection = mongoDatabase.getCollection("base")
        val document = Document("Account_address", account)
        document.append("token", uid)
        collection.insertOne(document)
    }
}