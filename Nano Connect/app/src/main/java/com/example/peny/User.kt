package com.example.peny

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (val uid: String, val phone_number: String, val public_address: String, var name:String ):Parcelable{
    constructor(): this ("","","","")
}
