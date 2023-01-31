package com.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import com.app.data.Student
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import com.app.data.DbManager

@Composable
fun StudentListScreen(onStudentSelected: (student: Student) -> Unit) {
        val scope = CoroutineScope(Dispatchers.IO)
        var isLoading by remember { mutableStateOf(false)}
        var searchState by remember { mutableStateOf("")}
        var useSearch by remember { mutableStateOf(false)}
        var sortBy by remember { mutableStateOf("last_name")}
        var studentList by remember { mutableStateOf(emptyList<Student>())}
        Column {

            Row {
                SortDropdown {
                    sortBy = it
                }
                Search(searchState, {searchState = it}, {useSearch = it})
                Button(
                    onClick = {
                        useSearch = false
                        searchState = ""
                    }
                ) {
                    Text("Clear Search")
                }
            }


            if (useSearch) {
                scope.launch {
                    isLoading = true
                    studentList = DbManager.searchStudent(searchState, sortBy)
                    isLoading = false
                }
            } else {
                scope.launch {
                    isLoading = true
                    studentList = DbManager.loadStudents()
                    isLoading = false
                }
            }
            if (isLoading) {
                LoadingAnimation()
            } else {
                StudentList(studentList, onStudentSelected)
                }
            }

}


//Scrollable composable containing page of student boxes
@Composable
fun StudentList(students: List<Student>, onStudentSelected: (student: Student) -> Unit) {

    val stateVertical = rememberScrollState(0)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(stateVertical)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            students.forEach { student ->
                StudentBox(student, onStudentSelected)
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }

}

@Composable
fun Search(searchState: String,setSearchState: (String) -> Unit, setUseSearch: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        TextField(
            value = searchState,
            onValueChange = {
                setSearchState(it)
                            },
            singleLine = true
        )
        Spacer(modifier = Modifier.weight(0.1f))
        Button(onClick = {
            setUseSearch(true)
        }) {
            Text("Search")
        }
    }

}

//

//Composable function to display name, grade, and points of student and is clickable to navigate to student
@Composable
fun StudentBox(student: Student, onStudentSelected: (student:Student) -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 5.dp, start = 3.dp, end = 3.dp)
            .clickable(onClick = {
            onStudentSelected(student)
        })
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            var name = student.last_name + ", " + student.first_name
            if (student.middle_initial != "") {
                name += " " + student.middle_initial
            }
            Text("Name: $name")
            Spacer(modifier = Modifier.weight(1f))
            Text("Grade: " + student.grade.toString())
            Spacer(modifier = Modifier.weight(0.5f))
            Text("Points: " + student.points.toString())
        }
    }
}


@Composable
fun SortDropdown(setSortBy: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false)}
    val items = listOf("Alphabetical", "Points")
    val tables = listOf("last_name", "points")
    var selectedIndex by remember { mutableStateOf(0)}
    Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
        Text(
            items[selectedIndex],
            modifier = Modifier.fillMaxWidth().clickable(onClick = { expanded = true})
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                    setSortBy(tables[index])
                }) {
                    Text(s)
                }
            }
        }

    }

}

@Composable
fun LoadingAnimation() {
    Box(
        modifier = Modifier.fillMaxSize().background(color = Color.Gray)
    ) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
    }
}


//@Composable
//fun FilterChecklist() {
//    Column (
//        modifier = Modifier.fillMaxHeight(),
//
//            ) {
//        Text(
//            "Grades",
//            modifier = Modifier.align(Alignment.Start)
//        )
//
//        Row(
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ) {
//            var grade9 by remember { mutableStateOf(true)}
//            Checkbox(
//                checked = grade9,
//                onCheckedChange = {grade9 = it}
//            )
//        }
//
//        Row (
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ){
//            var grade10 by remember { mutableStateOf(true)}
//            Checkbox(
//                checked = grade10,
//                onCheckedChange = {grade10 = it}
//            )
//        }
//
//        Row(
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ) {
//            var grade11 by remember { mutableStateOf(true)}
//            Checkbox(
//                checked = grade11,
//                onCheckedChange = {grade11 = it}
//            )
//        }
//
//        Row (
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        ) {
//            var grade12 by remember { mutableStateOf(true)}
//            Checkbox(
//                checked = grade12,
//                onCheckedChange = {grade12 = it}
//            )
//        }
//    }
//}




