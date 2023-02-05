package com.app.data

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer


@Serializable
data class Student(
    @SerialName("id") val student_id : Int,
    @SerialName("first_name" )val first_name : String,
    @SerialName("last_name" )val last_name : String,
    @SerialName("grade") val grade : Int,
    @SerialName("points") val points : Int,
    @SerialName("middle_initial") val middle_initial: String
        )