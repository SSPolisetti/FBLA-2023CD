package com.app.data


data class Event(
    val event_id : Int = -1,
    var name : String,
    var desc : String,
    var date : java.time.LocalDate,
    var event_type : Int = 0,
    var location : String,
)