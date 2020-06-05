package com.example.peny

class ChatMessage(val id:String, val text:String,val fromid:String, val toId:String, val timestamp:Long){
    constructor():this("","","","",-1)
}