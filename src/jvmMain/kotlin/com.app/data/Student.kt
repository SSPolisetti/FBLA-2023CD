package com.app.data

data class Student(
    var first_name : String,
    var last_name : String,
    var middle_initial: Char?,
    var grade : Int,
    var points : Int
        ) {
}