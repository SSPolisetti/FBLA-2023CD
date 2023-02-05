package com.app.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.app.data.DbManager
import com.app.data.Student
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter


interface StudentListComponent {
    val model: MutableValue<StudentListModel>

    fun onStudentClicked(student: Student)

    fun onAddStudentSelected()

    fun onSearchClicked()

    fun onSortByChanged(orderBy: String)

    fun onSearchValueChange(searchTerm: String)

    fun onClearSearch()

    fun getReport()

    data class StudentListModel(
        val students : List<Student>,
        val searchTerm : String,
        val orderBy : String,
        val isUsingSearch : Boolean,
        val isLoading : Boolean
    )
}

class DefaultStudentListComponent(
    componentContext: ComponentContext,
    private val onStudentSelected : (student : Student) -> Unit,
    private val onAddStudentClicked : () -> Unit
) : StudentListComponent, ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        lifecycle.doOnCreate {
            loadStudents()
        }

        lifecycle.doOnDestroy {
            scope.cancel()
        }

        //When taken back off of backstack, reload data in case user made changes to events
        lifecycle.doOnResume {
            loadStudents()
        }
    }


    override val model: MutableValue<StudentListComponent.StudentListModel> =
        MutableValue(
            StudentListComponent.StudentListModel(
                students = emptyList<Student>(),
                searchTerm = "",
                orderBy = "last_name",
                isUsingSearch = false,
                isLoading = false
            )
        )

    private fun loadStudents() {


        scope.launch {

            //Update model to indicate data is being loaded through the UI
            model.value = model.value.copy(isLoading = true)

            //Use isUsingSearch to either search or simply load students
            val students = if (model.value.isUsingSearch) {
                DbManager.searchStudent(model.value.searchTerm, model.value.orderBy)
            } else {
                DbManager.loadStudents(model.value.orderBy)
            }

            //Update the model with the loaded student data
            model.value = model.value.copy(students = students, isLoading = false)


        }
    }

    override fun onSearchClicked() {
        model.value = model.value.copy(isUsingSearch = true)
        loadStudents()

    }

    override fun onStudentClicked(student: Student) {
        onStudentSelected(student)
    }

    override fun onClearSearch() {
        model.value = model.value.copy(searchTerm = "", isUsingSearch = false)
        loadStudents()
    }


    override fun onSearchValueChange(searchTerm: String) {
        model.value = model.value.copy(searchTerm = searchTerm)
    }

    override fun onSortByChanged(orderBy: String) {

        model.value = model.value.copy(orderBy = orderBy)
        loadStudents()
    }

    override fun getReport() {
        var students = emptyList<Student>()
        scope.launch {
            model.value = model.value.copy(isLoading = true)

            students = DbManager.loadStudents("grade")

            var file = System.getProperty("user.home") + "/Downloads/Student_Points_Report.csv"
            FileOutputStream(file).apply { writeCsv(students) }

        }
    }

    override fun onAddStudentSelected() {
        onAddStudentClicked()
    }
}

@Preview
@Composable
fun StudentListContent(component: StudentListComponent, modifier : Modifier = Modifier) {
    val studentListModel by component.model.subscribeAsState()

    Column {

        Row (
            modifier = Modifier.align(Alignment.End).padding(top = 10.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            /* OrderByDropdown*/
            var expanded by remember { mutableStateOf(false)}
            val items = mapOf("last_name" to "Alphabetical", "points" to "Points")
            var selectedText = items[studentListModel.orderBy].toString()
            Column() {
                OutlinedTextField(
                    value = selectedText,
                    onValueChange = {selectedText = it},
                    modifier = Modifier.width(175.dp),
                    readOnly = true,
                    label = {Text("Sort By")},
                    trailingIcon = {
                        Icon(Icons.Filled.ArrowDropDown,"", modifier = Modifier.clickable { expanded = true })
                    }

                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {expanded = false},
                    modifier = Modifier.width(175.dp)
                ) {

                    items.forEach { (t, u) ->
                        DropdownMenuItem(
                            onClick = {
                                component.onSortByChanged(t)
                                expanded = false
                            }
                        ) {
                            Text(u)
                        }

                    }
                }
            }

            /*Search Bar*/

            TextField(
                value = studentListModel.searchTerm,
                onValueChange = {component.onSearchValueChange(it)},
                singleLine = true,
                modifier = Modifier.height(50.dp).width(300.dp).padding(start = 10.dp, end = 10.dp),
                label = {Text("Search")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, autoCorrect = false, imeAction = ImeAction.Next)


            )

            /*Search Button*/
            Button(
                onClick = {
                    component.onSearchClicked()
                },
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text("Search")
            }

            /*Clear Search Button*/
            Button(
                onClick = {
                    component.onClearSearch()
                }
            ) {
                Text("Clear Search", textAlign = TextAlign.Center)
            }
        }

        //Display loading screen if data is being loaded
        if (studentListModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(color = Color.Gray),
                contentAlignment = Alignment.Center

            ) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
            }

        } else if (studentListModel.students.isNotEmpty()){ //Check student list is empty, if not display clickable students
            val stateVertical = rememberScrollState(0)
            Box(modifier = Modifier.height(425.dp).fillMaxWidth().verticalScroll(stateVertical)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    studentListModel.students.forEach {student ->

                        Card(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 5.dp, start = 3.dp, end = 3.dp)
                                .clickable(onClick = {
                                    component.onStudentClicked(student)
                                })
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(all = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,

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
                        Spacer(modifier = Modifier.weight(0.1f))
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).height(425.dp),
                    adapter = rememberScrollbarAdapter(stateVertical)
                )
            }

        } else { //If there are no student records, display message

            Box(
                modifier = Modifier.fillMaxWidth().height(425.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No Students Found")
            }

        }

        Spacer(modifier = Modifier.weight(0.1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            Button(
                onClick = {

                }
            ) {
                Text("Generate Report")
            }
            Spacer(modifier = Modifier.weight(0.3f))
            Button(
                onClick = {

                }
            ) {
                Text("Pick Winners")
            }
            Spacer(modifier = Modifier.weight(0.3f))
            Button(
                onClick = {
                    component.onAddStudentSelected()
                }
            ) {
                Text("Add Student")
            }
            Spacer(modifier = Modifier.weight(0.2f))
        }

    }
}




fun OutputStream.writeCsv(students : List<Student>) {
    val writer = bufferedWriter()
    writer.write("""Grade, "Name",""Points""")
    writer.newLine()
    students.forEach {
        val name = "${it.last_name}, ${it.first_name} ${it.middle_initial}"
        writer.write("${it.grade}, ${name}, ${it.points}")
        writer.newLine()
    }
}




