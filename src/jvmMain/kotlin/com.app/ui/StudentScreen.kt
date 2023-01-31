package com.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.Screen.StudentsListScreen
import com.app.data.Event
import com.app.data.Student
import com.app.nav.NavController


@Composable
fun StudentScreen(navController: NavController, student: Student) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = {
                navController.navigateTo(StudentsListScreen.name, null)
            },
            modifier = Modifier.align(Alignment.Start).padding(10.dp)
        ) {
            Text("Exit")
        }
        StudentInfo(student)

    }
}

@Composable
fun AttendList(eventList : List<Event>) {
    val stateVertical = rememberScrollState(0)
    Box(
        modifier = Modifier.fillMaxSize().verticalScroll(stateVertical)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }

}



@Composable
fun StudentInfo(student: Student) {

    var firstName by remember { mutableStateOf(student.first_name) }
    var lastName by remember { mutableStateOf(student.last_name) }
    var mInitial by remember { mutableStateOf(student.middle_initial) }
    var grade by remember { mutableStateOf(student.grade.toString()) }
    var totalPoints by remember { mutableStateOf(student.points.toString()) }

    Box (
        modifier = Modifier.fillMaxSize()
            ){

        Row {
            TextField(
                value = lastName,
                onValueChange = {
                    if (it.length <= 20) {
                        lastName = it
                    }
                },
                label = { Text("Last Name") },
                singleLine = true
            )

            TextField(
                value = firstName,
                onValueChange = {
                    if (it.length <= 20) {
                        firstName = it
                    }
                },
                label = { Text("First Name") },
                singleLine = true
            )

            TextField(
                value = mInitial,
                onValueChange = {
                    if (it.length <= 1) {
                        mInitial = it
                    }
                },
                label = { Text("Middle Initial") },
                singleLine = true
            )


        }
        Row {
            var expanded by remember { mutableStateOf(false) }
            val items = listOf("9", "10", "11", "12")
            var selectedIndex by remember { mutableStateOf(items.indexOf(grade)) }
            Box(
                modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
            ) {
                Text(
                    items[selectedIndex],
                    modifier = Modifier.fillMaxWidth().clickable(onClick = { expanded = false })
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded = false},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.forEachIndexed { index, s ->
                        DropdownMenuItem(onClick = {
                            selectedIndex = index
                            expanded = false
                            grade = items[index]
                        }) {
                            Text(s)
                        }
                    }
                }
            }
            Text(totalPoints)
        }

    }
}



