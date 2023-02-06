package com.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Prize (
    @SerialName("id") val prize_id : Int = -1,
    @SerialName("name") var name : String,
    @SerialName("point_threshold") var min_point: Int,
    @SerialName("type") var type : String
        )