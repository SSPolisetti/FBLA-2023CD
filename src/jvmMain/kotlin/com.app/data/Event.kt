package com.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    @SerialName("id") val event_id : Int = -1,
    @SerialName("event_name") val name : String,
    @SerialName("event_description") val desc : String,
    @SerialName("date") val date : String,
    @SerialName("event_type_id") val event_type : Int = 0,
    @SerialName("location") val location : String,
)