package com.app.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class DateTransformation() : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return dateFilter(text)
    }
}
fun dateFilter(text: AnnotatedString): TransformedText {

    val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
    var out = ""
    var day = 0
    var month = 0
    var year = 0
    var dayString = ""
    var monthString = ""
    var yearString = ""

    for (i in trimmed.indices) {
        out += trimmed[i]
        if (i % 2 == 1 && i < 4) out += "/"
        if (i < 2) dayString += trimmed[i]
        if (i >= 2 && i < 4) monthString += trimmed[i]
        if (i >= 4) yearString += trimmed[i]
    }
    day = dayString.toIntOrNull() ?: 0
    month = monthString.toIntOrNull() ?: 0
    year = yearString.toIntOrNull() ?: 0

    if (day > 0 && month > 0) {
        val numDaysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
            else -> 0
        }
        if (day > numDaysInMonth) day = numDaysInMonth
        dayString = day.toString().padStart(2, '0')
        monthString = month.toString().padStart(2, '0')
        yearString = year.toString().padStart(4, '0')
        out = "$dayString/$monthString/$yearString"
    }

    val numberOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 1) return offset
            if (offset <= 3) return offset +1
            if (offset <= 8) return offset +2
            return 10
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <=2) return offset
            if (offset <=5) return offset -1
            if (offset <=10) return offset -2
            return 8
        }
    }

    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
}
//fun dateFilter(text: AnnotatedString): TransformedText {
//    val trimmed = if (text.text.length >= 8) text.text.substring(0..7) else text.text
//    var out = ""
//    for (i in trimmed.indices) {
//        out += trimmed[i]
//        if (i%2 == 1 && i < 4) {
//            out+="/"
//        }
//    }
//    if (out.length < 8) {
//        for (i in out.length until 8) {
//            if (i%2 == 1 && i < 4) {
//                out += "/"
//            }
//        }
//    }
//    val numberOffsetTranslator = object : OffsetMapping {
//        override fun originalToTransformed(offset: Int): Int {
//            if (offset <=1) {
//                return offset
//            }
//            if (offset <= 3){
//                return offset+1
//            }
//            if (offset <=8) {
//                return offset + 2
//            }
//            return 10
//        }
//
//        override fun transformedToOriginal(offset: Int): Int {
//            if (offset <=2) {
//                return offset
//            }
//            if (offset <= 5){
//                return offset - 1
//            }
//            if (offset <=10) {
//                return offset - 2
//            }
//            return 8
//        }
//
//    }
//    return TransformedText(AnnotatedString(out), numberOffsetTranslator)
//}
