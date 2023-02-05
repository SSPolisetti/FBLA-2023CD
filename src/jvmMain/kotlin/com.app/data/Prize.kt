package com.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Prize (
    val prize_id : Int = -1,
    var name : String,
    var min_point: Int,
    var is_won: Boolean,
    var type : String
        )