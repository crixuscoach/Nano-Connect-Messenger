package com.example.peny.wallet

import android.os.Parcelable
import com.rotilho.jnano.commons.NanoAmount
import kotlinx.android.parcel.Parcelize

class AccountInfo(val action:String,
                  val representative:String,
                  val account:String)

class PendingInfo( val action:String,
                   val account:String,
                   val count:Int,
                   val sorting:Boolean,
                   val include_only_confirmed: Boolean
)
@Parcelize
class SendTransaction(val amount:String,
                      val address:String): Parcelable {
    constructor(): this ("","")
}
