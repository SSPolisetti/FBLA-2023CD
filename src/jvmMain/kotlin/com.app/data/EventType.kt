package com.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventType (
    @SerialName("id") val typeId : Int,
    @SerialName("type_name") val typeName : String,
    @SerialName("points_attend") val typePoints : Int
        )