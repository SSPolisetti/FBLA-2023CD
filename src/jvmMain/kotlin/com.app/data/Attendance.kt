package com.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attendance (
    val event_id:  Int,
    val student_id : Int
        )