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
import androidx.compose.runtime.internal.isLiveLiteralsEnabled
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
import com.app.data.Prize
import com.app.data.Student
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnResume
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.*
import kotlinx.datetime.LocalDate
import java.io.OutputStream
import java.io.FileOutputStream


interface StudentListComponent {
    val model: MutableValue<StudentListModel>

    fun onStudentClicked(student: Student)

    fun onAddStudentSelected()

    fun onSearchClicked()

    fun onSortByChanged(orderBy: String)

    fun onSearchValueChange(searchTerm: String)

    fun onClearSearch()

    fun onGetWinnersClicked()

    fun showGenerateMessage()

    fun closeGenerateMessage()

    fun showTopMessage()

    fun closeTopMessage()

    fun getReport()

    data class StudentListModel(
        val students : List<Student>,
        val searchTerm : String,
        val orderBy : String,
        val isUsingSearch : Boolean,
        val isLoading : Boolean,
        val showDialog : Boolean,
        val showTopDialog : Boolean,
        val maxWinner : Student,
        val ninthWinner : Student,
        val tenthWinner : Student,
        val eleventhWinner : Student,
        val twelfthWinner : Student,
        val prizes : List<Prize>
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
                isLoading = false,
                showDialog = false,
                showTopDialog = false,
                maxWinner = Student(0, "", "", 0, -1, ""),
                ninthWinner = Student(0, "", "", 0, -1, ""),
                tenthWinner = Student(0, "", "", 0, -1, ""),
                eleventhWinner = Student(0, "", "", 0, -1, ""),
                twelfthWinner = Student(0, "", "", 0, -1, ""),
                prizes = emptyList<Prize>()
            )
        )

    private fun loadStudents() {


        scope.launch {

            //Update model to indicate data is being loaded through the UI
            model.value = model.value.copy(isLoading = true)

            delay(30)
            //Use isUsingSearch to either search or simply load students
            val students = if (model.value.isUsingSearch) {
                DbManager.searchStudent(model.value.searchTerm, model.value.orderBy)
            } else {
                DbManager.loadStudents(model.value.orderBy)
            }

            val prizes = DbManager.loadPrizes()

            //Update the model with the loaded student data
            model.value = model.value.copy(students = students, prizes = prizes,isLoading = false)


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

    override fun onGetWinnersClicked() {
        var maxPointsWinner = Student(0, "", "", 0, -1, "")
        var ninthStudents : MutableList<Student> = mutableListOf<Student>()
        var tenthStudents : MutableList<Student> = mutableListOf<Student>()
        var eleventhStudents : MutableList<Student> = mutableListOf<Student>()
        var twelfthStudents : MutableList<Student> = mutableListOf<Student>()

        scope.launch {
            model.value = model.value.copy(isLoading = true)

            for (student in model.value.students) {
                if (student.grade == 9) {
                    ninthStudents.add(student)
                } else if(student.grade == 10) {
                    tenthStudents.add(student)
                } else if(student.grade == 11) {
                    eleventhStudents.add(student)
                } else {
                    twelfthStudents.add(student)
                }


                model.value = if (student.points > maxPointsWinner.points)
                 {
                    model.value.copy(maxWinner = student)
                } else {
                    model.value.copy(maxWinner = student)
                }
            }

            model.value = model.value.copy(
                ninthWinner = ninthStudents.random(),
                tenthWinner = tenthStudents.random(),
                eleventhWinner = eleventhStudents.random(),
                twelfthWinner = twelfthStudents.random()
            )

            var file = System.getProperty("user.home") + "/Downloads/Winners-${java.time.LocalDate.now()}_${java.time.LocalDate.EPOCH}.csv"
            FileOutputStream(file).apply { writeCsv(model.value.prizes, model.value.maxWinner, model.value.ninthWinner, model.value.tenthWinner, model.value.eleventhWinner, model.value.twelfthWinner) }

            model.value = model.value.copy(isLoading = false)

        }
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

            println(students.toString())

            var file = System.getProperty("user.home") + "/Downloads/Student_Points_Report-${java.time.LocalDate.now()}_${java.time.LocalDate.EPOCH}.csv"
            FileOutputStream(file).apply { writeCsv(students) }

            model.value = model.value.copy(isLoading = false)

        }


    }

    override fun onAddStudentSelected() {
        onAddStudentClicked()
    }

    override fun showGenerateMessage() {
        model.value = model.value.copy(showDialog = true)
    }
    override fun closeGenerateMessage() {
        model.value = model.value.copy(showDialog = false)
    }

    override fun showTopMessage(){
        model.value = model.value.copy(showTopDialog = true)
    }
    override fun closeTopMessage(){
        model.value = model.value.copy(showTopDialog = false)
    }
}

@Composable
fun StudentListContent(component: StudentListComponent, modifier : Modifier = Modifier) {
    val studentListModel by component.model.subscribeAsState()
    GenerateMessage(studentListModel.showDialog, component)
    TopMessage(studentListModel.showTopDialog, component)
    Column {

        //search row
        Row (
            modifier = Modifier.align(Alignment.End).padding(top = 10.dp, bottom = 15.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,

        ) {

            /* OrderByDropdown*/
            var expanded by remember { mutableStateOf(false)}
            val items = mapOf("last_name" to "Alphabetical", "points" to "Points")
            var selectedText = items[studentListModel.orderBy].toString()
            Column {
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
                modifier = Modifier.height(75.dp).width(300.dp).padding(start = 10.dp, end = 10.dp),
                label = {Text("Search")},
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    autoCorrect = false,
                    imeAction = ImeAction.Next)


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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 15.dp)
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

        Spacer(modifier = Modifier.weight(0.05f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            Button(
                onClick = {
                    component.getReport()
                    component.showGenerateMessage()
                }
            ) {
                Text("Generate Report")
            }
            Spacer(modifier = Modifier.weight(0.3f))
            Button(
                onClick = {
                    component.onGetWinnersClicked()
                    component.showTopMessage()

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

        Spacer(modifier = Modifier.weight(0.1f))

    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenerateMessage(openDialog: Boolean, component: StudentListComponent) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                component.closeGenerateMessage()
            },
            title = {
                Text("Report Generated")
            },
            text = {
                Text("Report has been generated and placed in downloads folder.")
            },
            buttons = {
                Button(
                    modifier = Modifier.width(500.dp).height(250.dp),
                    onClick = {
                        component.closeGenerateMessage()
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopMessage(openDialog: Boolean, component: StudentListComponent) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                component.closeTopMessage()
            },
            title = {
                Text("Top Scores Report Generated")
            },
            text = {
                Text("Report has been generated and placed in downloads folder.")
            },
            buttons = {
                Button(
                    modifier = Modifier.width(500.dp).height(250.dp),
                    onClick = {
                        component.closeTopMessage()
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
    }
}



fun OutputStream.writeCsv(students : List<Student>) {
    val writer = bufferedWriter()
    writer.write("""Grade,Name,Points""")
    writer.newLine()
    students.forEach {
        writer.write("${it.grade}, ${it.last_name} ${it.first_name} ${it.middle_initial}, ${it.points}")

        writer.newLine()
    }
    writer.flush()
    writer.close()
}


fun OutputStream.writeCsv(prizes : List<Prize>, max : Student, ninth : Student, tenth : Student, eleventh : Student, twelfth : Student) {
    val writer = bufferedWriter()
    writer.write("""Winner,Name,Points, Prize""")
    writer.newLine()
    var ninthPrize = Prize(0,"", -1, "")
    var tenthPrize = Prize(0,"", -1, "")
    var eleventhPrize = Prize(0,"", -1, "")
    var twelfthPrize = Prize(0,"", -1, "")
    var maxPrize = Prize(0, "", -1, "")
    for(prize in prizes) {
        if(prize.min_point <= ninth.points && prize.min_point > ninthPrize.min_point) {
            ninthPrize = prize
        }
        if(prize.min_point <= tenth.points && prize.min_point > tenthPrize.min_point) {
            tenthPrize = prize
        }
        if(prize.min_point <= eleventh.points && prize.min_point > eleventhPrize.min_point) {
            eleventhPrize = prize
        }
        if(prize.min_point <= twelfth.points && prize.min_point > twelfthPrize.min_point) {
            twelfthPrize = prize
        }
        if(prize.min_point <= max.points && prize.min_point > maxPrize.min_point) {
            maxPrize = prize
        }

    }


    writer.write("Grade 9 Winner,${ninth.last_name} ${ninth.first_name} ${ninth.middle_initial}, ${ninth.points}, ${ninthPrize.name}")
    writer.newLine()
    writer.write("Grade 10 Winner,${tenth.last_name} ${tenth.first_name} ${tenth.middle_initial}, ${tenth.points}, ${tenthPrize.name}")
    writer.newLine()
    writer.write("Grade 11 Winner,${eleventh.last_name} ${eleventh.first_name} ${eleventh.middle_initial}, ${eleventh.points}, ${eleventhPrize.name}")
    writer.newLine()
    writer.write("Grade 12 Winner,${twelfth.last_name} ${twelfth.first_name} ${twelfth.middle_initial}, ${twelfth.points}, ${twelfthPrize.name}")
    writer.newLine()
    writer.write("Most Points Winner,${max.last_name} ${max.first_name} ${max.middle_initial}, ${max.points}, ${maxPrize.name}")


    writer.flush()
    writer.close()




}



