package com.app.ui

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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.data.*
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnResume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

interface StudentDetailsComponent {
    val model : MutableValue<StudentDetailsModel>

    fun addAttendance(eventId : Int)

    fun removeAttendance(eventId : Int)

    fun saveChanges()

    fun deleteStudent()

    fun onBackButtonClicked()

    fun onFirstNameChanged(fname : String)

    fun onLastNameChanged(lname : String)

    fun onMiddleInitialChanged(mInitial : String)

    fun onGradeChanged(grade : Int)

    fun revertAttendance(eventId : Int)

    fun firstNameCheck(isValid : Boolean)

    fun lastNameCheck(isValid : Boolean)

    fun showErrorMessage()

    fun closeErrorMessage()


    data class StudentDetailsModel(
        val student : Student,
        val events : List<Event>,
        val attendedEvents : List<Attendance>,
        val isLoading : Boolean,
        val removeAttendance: MutableSet<Int>,
        val addAttendance : MutableSet<Int>,
        val isFNameValid : Boolean,
        val isLNameValid : Boolean,
        val showDialog : Boolean
    )
}


class DefaultStudentDetailsComponent(
    componentContext: ComponentContext,
    student: Student,
    private val onFinished : () -> Unit
) : StudentDetailsComponent, ComponentContext by componentContext {


    init {
        lifecycle.doOnCreate {
            loadEvents()
        }


        //When taken back off of backstack, reload data in case user made changes to events
        lifecycle.doOnResume {
            loadEvents()
        }
    }


    private val scope = CoroutineScope(Dispatchers.Main)

    override val model: MutableValue<StudentDetailsComponent.StudentDetailsModel> =
        MutableValue(
            StudentDetailsComponent.StudentDetailsModel(
                student = student,
                events = emptyList<Event>(),
                attendedEvents = emptyList<Attendance>(),
                isLoading = false,
                removeAttendance = mutableSetOf<Int>(),
                addAttendance = mutableSetOf<Int>(),
                isFNameValid = true,
                isLNameValid = true,
                showDialog = false
            )
        )

    private fun loadEvents() {
       scope.launch {

           model.value = model.value.copy(isLoading = true)

           val events = DbManager.loadEvents()
           val attendedEvents = DbManager.loadAttendedEvents(model.value.student.student_id)

           model.value = model.value.copy(events = events, attendedEvents = attendedEvents, isLoading = false)

       }
    }

    override fun addAttendance(eventId: Int) {
        model.value.addAttendance.add(eventId)
        model.value.removeAttendance.remove(eventId)
    }

    override fun removeAttendance(eventId : Int) {
        model.value.removeAttendance.add(eventId)
        model.value.addAttendance.remove(eventId)
    }

    override fun revertAttendance(eventId: Int) {
        model.value.removeAttendance.remove(eventId)
        model.value.addAttendance.remove(eventId)
    }

    override fun saveChanges() {

        if (model.value.isLNameValid && model.value.isFNameValid) {
            scope.launch {

                model.value = model.value.copy(isLoading = true)

                DbManager.updateStudentAndAttendance(
                    model.value.student,
                    model.value.removeAttendance.isNotEmpty(),
                    model.value.addAttendance.isNotEmpty(),
                    model.value.removeAttendance.joinToString(separator = ","),
                    model.value.addAttendance.joinToString(separator = ",")
                )

                model.value = model.value.copy(isLoading = false)
            }
            loadEvents()
        }


    }

    override fun deleteStudent() {
        scope.launch {

            model.value = model.value.copy(isLoading = true)

            DbManager.deleteStudent(model.value.student.student_id)

            model.value = model.value.copy(isLoading = true)
        }
        onFinished()
    }


    override fun onBackButtonClicked() {
        onFinished()
    }

    override fun onFirstNameChanged(fname: String) {
        model.value = model.value.copy(
            student = model.value.student.copy(first_name = fname)
        )
    }

    override fun onLastNameChanged(lname: String) {
        model.value = model.value.copy(
            student = model.value.student.copy(last_name = lname)
        )
    }

    override fun onMiddleInitialChanged(mInitial: String) {
        model.value = model.value.copy(
            student = model.value.student.copy(middle_initial = mInitial)
        )
    }

    override fun onGradeChanged(grade: Int) {
        model.value = model.value.copy(
            student = model.value.student.copy(grade = grade)
        )
    }

    override fun firstNameCheck(isValid : Boolean) {
        model.value = model.value.copy(isFNameValid = isValid)
    }


    override fun lastNameCheck(isValid : Boolean) {
        model.value = model.value.copy(isLNameValid = isValid)
    }

    override fun showErrorMessage() {
        model.value = model.value.copy(showDialog = true)
    }

    override fun closeErrorMessage() {
        model.value = model.value.copy(showDialog = false)
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErrorMessage(openDialog: Boolean, component: StudentDetailsComponent) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                component.closeErrorMessage()
            },
            title = {
                Text("Invalid Input")
            },
            text = {
                Text("First name and last name are required fields")
            },
            buttons = {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        component.closeErrorMessage()
                    }
                ) {
                    Text("Dismiss")
                }
            },
            modifier = Modifier.width(500.dp).height(250.dp)


        )
    }
}



@Composable
fun StudentDetailsContent(component : StudentDetailsComponent, modifier: Modifier = Modifier) {
    val studentDetailsModel by component.model.subscribeAsState()

    ErrorMessage(studentDetailsModel.showDialog, component)


    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            Button(
                onClick = {
                    component.onBackButtonClicked()
                },
            ) {
                Text("Exit")
            }
            Spacer(modifier = Modifier.weight(0.3f))
            Button(onClick = {
                if (studentDetailsModel.isFNameValid && studentDetailsModel.isLNameValid) {
                    component.saveChanges()
                } else {
                    component.showErrorMessage()
                }
            }) {
                Text("Save Changes")
            }
            Spacer(modifier = Modifier.weight(0.3f))
            Button(onClick = {
                component.deleteStudent()
            }) {
                Text("Delete Student")
            }
            Spacer(modifier = Modifier.weight(0.2f))

        }

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(top = 15.dp, bottom = 15.dp, start = 3.dp, end = 3.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
                    ) {

                Spacer(modifier = Modifier.weight(0.1f))
                OutlinedTextField(
                    value = studentDetailsModel.student.last_name,
                    onValueChange = {
                        if (it.length < 20) {
                            component.onLastNameChanged(it)
                            component.lastNameCheck(true)
                        }
                        if (it.isEmpty()){
                            component.lastNameCheck(false)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    label = {Text("Last Name")},
                    modifier = Modifier.width(150.dp),
                    colors = if (studentDetailsModel.isLNameValid) {
                        TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high),
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                        )
                    } else {
                        TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.error,
                            unfocusedBorderColor = MaterialTheme.colors.error
                        )
                    }
                )

                Spacer(modifier = Modifier.weight(0.01f))

                OutlinedTextField(
                    value = studentDetailsModel.student.first_name,
                    onValueChange = {
                        if (it.length < 20) {
                            component.onFirstNameChanged(it)
                            component.firstNameCheck(true)
                        }
                        if (it.isEmpty())  component.firstNameCheck(false)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    label = {Text("First Name")},
                    modifier = Modifier.width(150.dp),
                    colors  = if (studentDetailsModel.isFNameValid) {
                        TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high),
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                        )
                    } else {
                        TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.error,
                            unfocusedBorderColor = MaterialTheme.colors.error
                        )
                    }
                )
                Spacer(modifier = Modifier.weight(0.05f))
                Text(",")
                Spacer(modifier = Modifier.weight(0.05f))
                OutlinedTextField(
                    value = studentDetailsModel.student.middle_initial,
                    onValueChange = {
                        if (it.length < 2) component.onMiddleInitialChanged(it)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    label = {Text("M.I")},
                    modifier = Modifier.width(60.dp)
                )
                Spacer(modifier = Modifier.weight(0.1f))

                var expanded by remember { mutableStateOf(false) }
                val items = mapOf(9 to "9", 10 to "10", 11 to "11", 12 to "12")
                var selectedGrade = items[studentDetailsModel.student.grade].toString()
                Column() {
                    OutlinedTextField(
                        value = selectedGrade,
                        onValueChange = {selectedGrade = it},
                        modifier = Modifier.width(150.dp),
                        readOnly = true,
                        label = {Text("Grade")},
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown,"", modifier = Modifier.clickable { expanded = true })
                        },


                        )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {expanded = false},
                        modifier = Modifier.width(175.dp)
                    ) {
                        items.forEach { (t, u) ->
                            DropdownMenuItem(
                                onClick = {
                                    component.onGradeChanged(t)
                                    expanded = false
                                }
                            ) {
                                Text(u)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                Text(
                    "Points: ${studentDetailsModel.student.points}"
                )

                Spacer(modifier = Modifier.weight(0.1f))
            }

        }

        Divider(modifier = Modifier.fillMaxWidth().padding(start = 3.dp, end = 3.dp))
        Spacer(modifier = Modifier.weight(0.1f))

        /* Update Attendance Section */
        if (studentDetailsModel.events.isNotEmpty()) {
            val stateVertical = rememberScrollState(0)
            Box(
                modifier = Modifier.height(400.dp).fillMaxWidth().verticalScroll(stateVertical)
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    studentDetailsModel.events.forEach { event:Event ->
                        val initialState = studentDetailsModel.attendedEvents.contains(
                            Attendance(event.event_id, studentDetailsModel.student.student_id))
                        var checkBoxState by remember { mutableStateOf(initialState)}
                        Card {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    event.name,
                                )
                                Spacer(modifier = Modifier.weight(0.1f))
                                Text(
                                    pgDateToDate(event.date, isEditing = false)
                                )

                                Spacer(modifier = Modifier.weight(0.1f))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text("Attended Event: ")
                                    Checkbox(
                                        checked = checkBoxState,
                                        onCheckedChange = {
                                            if (checkBoxState != initialState) {
                                                component.revertAttendance(event.event_id)
                                            } else if(checkBoxState) {
                                                component.removeAttendance(event.event_id)
                                            } else  {
                                                component.addAttendance(event.event_id)
                                            }
                                            checkBoxState = !checkBoxState
                                        },

                                        )
                                }

                            }



                        }
                    }

                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).height(400.dp),
                    adapter = rememberScrollbarAdapter(stateVertical)
                )

            }


        } else {
            Column {
                Text("No Events Available")
            }
        }

    }


}

fun pgDateToDate(date : String, isEditing : Boolean) : String {

    val dateList = date.split("-")
    return if (isEditing) {
        "${dateList[1]}${dateList[2]}${dateList[0]}"

    } else {
        "${dateList[1]}/${dateList[2]}/${dateList[0]}"
    }

}
