package com.app.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object DbManager  {


    //Initialize supabase client when called
     val client = createSupabaseClient(
        supabaseUrl = "https://lmditehrzsehcckcidfi.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImxtZGl0ZWhyenNlaGNja2NpZGZpIiwicm9sZSI6ImFub24iLCJpYXQiOjE2Njc1NzQ1OTUsImV4cCI6MTk4MzE1MDU5NX0.FNE5Ru4v4oUNLhozLMjpUSuyFGFzvsoodpgiR3Rh_zo"
    ) {
        install(Postgrest)
    }

    private val scope = CoroutineScope(Dispatchers.IO)



    suspend fun loadStudents() : List<Student> {
        val result = client.postgrest.rpc("load_students")
        return result.decodeList<Student>()

    }

    suspend fun loadEvents() : List<Event> {
        val result = client.postgrest.rpc("load_events")
        return result.decodeList<Event>()

    }

    suspend fun loadTypes() : List<EventType> {
        val result = client.postgrest.rpc("load_types")
        return result.decodeList<EventType>()
    }

    suspend fun addEvent(event: Event) {
        client.postgrest.rpc("insert_event", mapOf(
            "event_id" to event.event_id,
            "e_name" to event.name,
            "e_desc" to event.desc,
            "e_date" to event.date,
            "type id" to event.event_type,
            "e_location" to event.location
        ))
    }

    suspend fun addStudent(student: Student) {
        client.postgrest.rpc("insert_student", mapOf(
            "fname" to student.first_name,
            "lname" to student.last_name,
            "grade_num" to student.grade,
            "m_initial" to student.middle_initial
        ))
    }

    //Function to add student to attendance at a time
    suspend fun addAttendance(attendance: Attendance){
        client.postgrest.rpc("insert_attendance", mapOf(
            "e_id" to attendance.event_id,
            "s_id" to attendance.student_id
        ))

    }


    suspend fun deleteStudent(studentId : Long) {

    }


    suspend fun searchStudent(search : String, sortBy : String) : List<Student> {
        val result = client.postgrest.rpc("student_search", mapOf(
            "search" to search,
            "order_by" to sortBy
        ))
        return result.decodeList<Student>()
    }

    suspend fun searchEvent(search : String, sortBy : String) : List<Event> {
        val result = client.postgrest.rpc("event_search", mapOf(
            "search" to search,
            "order_by" to sortBy
        ))
        return result.decodeList<Event>()
    }


    suspend fun deleteEvent(eventId : Long) {

    }

    suspend fun editEvent(event : Event) {

    }

    suspend fun deletePrize(prize: Prize) {

    }

    suspend fun editPrize(prize: Prize) {

    }

    suspend fun addPrize(prize: Prize) {

    }

    suspend fun loadPrizes() {

    }


}