package com.app.data



//@Serialized
data class Event(
    val event_id : Int,
    var name : String,
    var desc : String,
    var points : Int,
    var location : String,
) {

}