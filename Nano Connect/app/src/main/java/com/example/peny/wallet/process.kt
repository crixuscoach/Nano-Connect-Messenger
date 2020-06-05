package com.example.peny.wallet

class publishProcess(
    val do_work:Boolean,
    val action:String,
    val json_block:Boolean,
    val subtype:String,
    val block: Block
)

class publishProcessReceive(
    val do_work:Boolean,
    val action:String,
    val json_block:Boolean,
    val subtype:String,
    val block: BlockReceive
)