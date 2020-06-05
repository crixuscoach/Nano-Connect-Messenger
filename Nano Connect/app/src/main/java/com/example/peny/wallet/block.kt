package com.example.peny.wallet

class Block (val type:String,
             val account:String,
             val previous:String,
             val representative:String,
             val balance:String,
             val link:String,
             val link_as_account:String,
             val signature:String
             )
class BlockReceive (val type:String,
             val account:String,
             val previous:String,
             val representative:String,
             val balance:String,
             val link:String,
             val signature:String
)
{}