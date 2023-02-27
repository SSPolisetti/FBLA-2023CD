package com.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.data.DbManager
import com.app.data.Student
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


interface StudentDetailsInsertComponent {
        val model : MutableValue<StudentDetailsInsertModel>

        fun insertStudent()

        fun onCancelClicked()

        fun onFirstNameChanged(fName : String)

        fun onLastNameChanged(lName : String)

        fun onMiddleInitialChanged(mInitial: String)

        fun onGradeChanged(grade: Int)


        data class StudentDetailsInsertModel(
            val fName : String,
            val lName : String,
            val mInitial : String,
            val grade : Int,
            val isLocked : Boolean
        )


}




class DefaultStudentDetailsInsertComponent(
    componentContext: ComponentContext,
    private val onFinished : () -> Unit
) : StudentDetailsInsertComponent {
    private val scope = CoroutineScope(Dispatchers.Main)

    override val model: MutableValue<StudentDetailsInsertComponent.StudentDetailsInsertModel> =
        MutableValue(
            StudentDetailsInsertComponent.StudentDetailsInsertModel(
                fName = "",
                lName = "",
                mInitial = "",
                grade = 9,
                isLocked = false,
            )
        )



    override fun insertStudent() {

        scope.launch {

            DbManager.addStudent(
                Student(
                    student_id = 0,
                    first_name = model.value.fName,
                    last_name = model.value.lName,
                    grade = model.value.grade,
                    points = 0,
                    middle_initial = model.value.mInitial
                )
            )
        }
        onFinished()
    }

    override fun onCancelClicked() {
        onFinished()
    }

    override fun onFirstNameChanged(fname : String) {
        model.value = model.value.copy(fName = fname)
    }

    override fun onLastNameChanged(lName: String) {
        model.value = model.value.copy(lName = lName)
    }

    override fun onMiddleInitialChanged(mInitial: String) {
        model.value = model.value.copy(mInitial = mInitial)
    }

    override fun onGradeChanged(grade: Int) {
        model.value = model.value.copy(grade = grade)
    }


}


@Composable
fun StudentDetailsInsertContent(component: StudentDetailsInsertComponent) {
    val studentDetailsInsertModel by component.model.subscribeAsState()

    Column {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            Button(
                onClick = {
                    component.onCancelClicked()
                },
            ) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.weight(0.6f))

            Button(onClick = {
                if (!studentDetailsInsertModel.isLocked) {
                    component.insertStudent()
                }
            }) {
                Text("Add Student")
            }

            Spacer(modifier = Modifier.weight(0.2f))

        }
        /* TextFields and dropdown for inputting student information*/
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 15.dp, bottom = 15.dp, start = 3.dp, end = 3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Spacer(modifier = Modifier.weight(0.1f))
                OutlinedTextField(
                    value = studentDetailsInsertModel.lName,
                    onValueChange = {
                        if (it.length < 20) component.onLastNameChanged(it)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text("Last Name") },
                    modifier = Modifier.width(150.dp)
                )

                Spacer(modifier = Modifier.weight(0.01f))

                OutlinedTextField(
                    value = studentDetailsInsertModel.fName,
                    onValueChange = {
                        if (it.length < 20) component.onFirstNameChanged(it)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text("First Name") },
                    modifier = Modifier.width(150.dp)
                )

                Spacer(modifier = Modifier.weight(0.05f))

                Text(",")

                Spacer(modifier = Modifier.weight(0.05f))
                OutlinedTextField(
                    value = studentDetailsInsertModel.mInitial,
                    onValueChange = {
                        if (it.length < 2) component.onMiddleInitialChanged(it)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text("M.I") },
                    modifier = Modifier.width(60.dp)
                )

                Spacer(modifier = Modifier.weight(0.1f))


                /* GradeDropdown*/
                var expanded by remember { mutableStateOf(false) }
                val items = mapOf(9 to "9", 10 to "10", 11 to "11", 12 to "12")
                var selectedGrade = items[studentDetailsInsertModel.grade].toString()
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




            }
        }
    }
}