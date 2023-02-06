package com.app.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

object DbManager  {


    //Initialize supabase client when called
     val client = createSupabaseClient(
        supabaseUrl = "https://lmditehrzsehcckcidfi.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxtZGl0ZWhyenNlaGNja2NpZGZpIiwicm9sZSI6ImFub24iLCJpYXQiOjE2Njc1NzQ1OTUsImV4cCI6MTk4MzE1MDU5NX0.FNE5Ru4v4oUNLhozLMjpUSuyFGFzvsoodpgiR3Rh_zo"
    ) {
        install(Postgrest)
    }


    suspend fun loadStudents(sortBy: String) : List<Student> {
        print("\"$sortBy\"")

        val result = client.postgrest.rpc("load_students", mapOf(
            "order_by" to sortBy
        ))
        println("complete")

        //decode list of students into student data class
        return result.decodeList(Json {
            ignoreUnknownKeys = true
        })

    }


    suspend fun loadEvents() : List<Event> {

        val result = client.postgrest.rpc("load_events")
        return result.decodeList(Json {
            ignoreUnknownKeys = true
        })

    }



    suspend fun loadTypes() : List<EventType> {
        val result = client.postgrest.rpc("load_types")
        return result.decodeList(Json {
            ignoreUnknownKeys = true
        })
    }



    suspend fun searchStudent(search : String, sortBy : String) : List<Student> {
        val result = client.postgrest.rpc("student_search", mapOf(
            "search" to search.replace("\\s".toRegex(), ""),
            "sortby" to sortBy
        ))
        return result.decodeList(Json {
            ignoreUnknownKeys = true
        })
    }

    suspend fun searchEvent(search : String) : List<Event> {
        val result = client.postgrest.rpc("event_search", mapOf(
            "search" to search.replace("\\s".toRegex(), "")
        ))
        return result.decodeList(Json {
            ignoreUnknownKeys = true
        })
    }

    suspend fun loadPrizes() : List<Prize> {
        val result = client.postgrest.rpc("load_prizes")
        return result.decodeList(Json {
            ignoreUnknownKeys = true
        })
    }

    suspend fun loadAttendedEvents(studentId : Int) : List<Attendance> {

        val result = client.postgrest.rpc("load_attended_events", mapOf(
            "s_id" to "$studentId"
        ))
        return result.decodeList(Json {
            ignoreUnknownKeys = true
        })

    }


    suspend fun addEvent(event: Event) {
        val result = client.postgrest.rpc("insert_event", mapOf(
            "e_name" to event.name,
            "e_desc" to event.desc,
            "e_date" to event.date,
            "type" to "${event.event_type}",
            "e_location" to event.location
        ))
        print("$result/n")
    }

    suspend fun addStudent(student: Student) {
        val result = client.postgrest.rpc("insert_student", mapOf(
            "fname" to student.first_name,
            "lname" to student.last_name,
            "grade_num" to "${student.grade}",
            "m_initial" to student.middle_initial
        ))
        print("$result/n")
    }



    suspend fun updateStudentAndAttendance(
        student : Student,
        isDeleting : Boolean,
        isAdding : Boolean,
        deleteStringArr : String,
        insertStringArr : String
    ) {

//        val studentId: Int = student.student_id
//        val fname : String = student.first_name
//        val lname : String = student.last_name
//        val mInitial : String = student.middle_initial
//        val grade : Int = student.grade
//        val isDelete : Boolean = attendanceUpdate.isDeleting
//        val isInsert : Boolean = attendanceUpdate.isAdding
//        val deleteString : String = attendanceUpdate.delete
//        val insertString : String = attendanceUpdate.insert

        val result = client.postgrest.rpc("update_student_and_attendance", mapOf(
            "s_id" to "${student.student_id}",
            "f_name" to student.first_name,
            "l_name" to student.last_name,
            "m_initial" to student.middle_initial,
            "grade" to "${student.grade}",
            "is_delete_attendance" to "$isDeleting",
            "is_insert_attendance" to "$isAdding",
            "delete_attendance" to deleteStringArr,
            "insert_attendance" to insertStringArr
        ))
        print("$result/n")
    }



    suspend fun deleteStudent(studentId : Int) {
        val result = client.postgrest.rpc("delete_student", mapOf(
            "s_id" to "$studentId"
        ))
        print("$result/n")
    }


    suspend fun deleteEvent(eventId : Int) {
        val result = client.postgrest.rpc("delete_event", mapOf(
            "e_id" to "$eventId"
        ))
        print("$result/n")
    }

    suspend fun editEvent(event : Event) {
        val result = client.postgrest.rpc("update_event", mapOf(
            "e_id" to "${event.event_id}",
            "e_name" to event.name,
            "e_desc" to event.desc,
            "e_date" to event.date,
            "e_type_id" to "${event.event_type}",
            "location" to event.location
        ))
        print("$result/n")

    }

    suspend fun deletePrize(prizeId : Int) {
        val result = client.postgrest.rpc("delete_prize", mapOf(
            "prize_id" to "$prizeId"
        ))
        print("$result/n")
    }

    suspend fun editPrize(prize: Prize) {
        val result = client.postgrest.rpc("update_prize", mapOf(
            "prize_id" to "${prize.prize_id}",
            "p_name" to prize.name,
            "min_points" to "${prize.min_point}",
            "type" to prize.type
        ))
        print("$result/n")
    }

    suspend fun addPrize(prize: Prize) {
        val result = client.postgrest.rpc("insert_prize", mapOf(
            "p_name" to prize.name,
            "min_points" to "${prize.min_point}",
            "type" to prize.type
        ))
        print("$result/n")
    }



}