package com.app.data

data class Prize (
    val prize_id : Int = -1,
    var name : String,
    var min_point: Int,
    var is_won: Boolean,
    var type : String
        )