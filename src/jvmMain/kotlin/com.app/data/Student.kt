package com.app.data

data class Student(
    val student_id : Int = -1,
    var first_name : String,
    var last_name : String,
    var middle_initial: String,
    var grade : Int,
    var points : Int,
        )