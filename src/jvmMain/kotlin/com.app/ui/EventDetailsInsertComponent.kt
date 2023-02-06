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
import com.app.data.Event
import com.app.data.EventType
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface EventDetailsInsertComponent {
    val model : MutableValue<EventDetailsInsertModel>

    fun insertEvent()

    fun onCancelClicked()

    fun onNameChanged(name : String)

    fun onDescChanged(desc : String)

    fun onDateChanged(date : String)

    fun onLocationChanged(location : String)

    fun onEventTypeChanged(eventType: EventType)

    fun nameCheck(isValid : Boolean)

    fun dateCheck(isValid : Boolean)

    fun locationCheck(isValid : Boolean)

    fun showErrorMessage()

    fun closeErrorMessage()


    data class EventDetailsInsertModel(
        val name : String,
        val description : String,
        val s_date : String,
        val type_id : Int,
        val location : String,
        val types : List<EventType>,
        val isLoading : Boolean,
        val isDateValid : Boolean,
        val isNameValid : Boolean,
        val isLocationValid : Boolean,
        val showDialog : Boolean
    )
}


class DefaultEventDetailsInsertComponent(
    componentContext: ComponentContext,
    private val onFinished : () -> Unit,
    types : List<EventType>
) : EventDetailsInsertComponent {
    private val scope = CoroutineScope(Dispatchers.Main)


    override val model : MutableValue<EventDetailsInsertComponent.EventDetailsInsertModel> =
        MutableValue(
            EventDetailsInsertComponent.EventDetailsInsertModel(
                name = "",
                description = "",
                s_date = "",
                type_id = 1,
                location = "",
                types = types,
                isLoading = false,
                isDateValid = true,
                isNameValid = true,
                isLocationValid = true,
                showDialog = false
            )
        )

    override fun onCancelClicked() {
        onFinished()
    }

    override fun onNameChanged(name: String) {
        model.value = model.value.copy(name = name)
    }

    override fun onDescChanged(desc: String) {
        model.value = model.value.copy(description = desc)
    }

    override fun onDateChanged(date: String) {
        model.value = model.value.copy(s_date = date)
    }

    override fun onLocationChanged(location: String) {
        model.value = model.value.copy(location = location)
    }

    override fun onEventTypeChanged(eventType: EventType) {
        model.value = model.value.copy(type_id = eventType.typeId)
    }

    override fun nameCheck(isValid: Boolean) {
        model.value = model.value.copy(isNameValid = isValid)
    }

    override fun dateCheck(isValid: Boolean) {
        model.value = model.value.copy(isDateValid = isValid)
    }

    override fun locationCheck(isValid: Boolean) {
        model.value = model.value.copy(isLocationValid = isValid)
    }

    override fun insertEvent() {

        scope.launch {

            DbManager.addEvent(
                Event(
                    name = model.value.name,
                    desc = model.value.description,
                    date = dateToPgDate(model.value.s_date),
                    event_type = model.value.type_id,
                    location = model.value.location
                )
            )


        }

        onFinished()
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
fun ErrorMessage(openDialog: Boolean, component: EventDetailsInsertComponent) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                component.closeErrorMessage()
            },
            title = {
                Text("Invalid Input")
            },
            text = {
                Text("Name, date, and location are required fields. Date must be a valid date")
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
            }
        )
    }
}


@Composable
fun EventDetailsInsertContent(component: EventDetailsInsertComponent) {
    val eventDetailsInsertModel by component.model.subscribeAsState()

    ErrorMessage(eventDetailsInsertModel.showDialog, component)


    Column {
        Row {
            Spacer(modifier = Modifier.weight(0.3f))
            Button(
                onClick = {
                    component.onCancelClicked()
                },
            ) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.weight(0.4f))
            Button(onClick = {
                component.insertEvent()
            }) {
                Text("Add Event")
            }
            Spacer(modifier = Modifier.weight(0.3f))
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 20.dp, bottom = 20.dp, start = 10.dp, end = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Column(
                    modifier = Modifier.width(200.dp)
                ) {

                    //Name
                    OutlinedTextField(
                        value = eventDetailsInsertModel.name,
                        onValueChange = {
                            if(it.length < 25) {
                                component.onNameChanged(it)
                                component.nameCheck(true)
                            }
                            if(it.isEmpty()) {
                                 component.nameCheck(false)
                            }

                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Ascii,
                            autoCorrect = false,
                            imeAction = ImeAction.Next
                        ),
                        label = {Text("Name")},
                        modifier = Modifier.width(200.dp),
                        colors = if (eventDetailsInsertModel.isNameValid) {
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
                    Spacer(modifier = Modifier.weight(0.1f))

                    //Description
                    OutlinedTextField(
                        value = eventDetailsInsertModel.description,
                        onValueChange = {
                            component.onDescChanged(it)
                        },
                        singleLine = false,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            autoCorrect = false,
                            imeAction = ImeAction.Next
                        ),
                        maxLines = 5,
                        label = {Text("Description")}

                    )

                }


                Spacer(modifier = Modifier.weight(0.3f))


                Column(
                    modifier = Modifier.width(200.dp)
                ) {

                    //Date
                    OutlinedTextField(
                        value = eventDetailsInsertModel.s_date,
                        onValueChange = {


                            if (it.length <= 8) {
                                component.onDateChanged(it)

                                println(eventDetailsInsertModel.s_date)
                            }
                            try {
                                component.dateCheck(true)
                                val testDate = dateToPgDate(it)
                            } catch (E: Exception) {
                                component.dateCheck(false)
                            }

                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            autoCorrect = false,
                            imeAction = ImeAction.Next
                        ),
                        visualTransformation = DateTransformation(),
                        label = {Text("Date (MM/DD/YYYY)")},
                        colors = if (eventDetailsInsertModel.isDateValid) {
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

                    Spacer(modifier = Modifier.weight(0.1f))

                    //Location
                    OutlinedTextField(
                        value = eventDetailsInsertModel.location,
                        onValueChange = {
                            if (it.length < 50) {
                                component.onLocationChanged(it)
                                component.locationCheck((true))
                            }
                            if (it.isEmpty()){
                                component.locationCheck((false))
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            autoCorrect = false,
                            imeAction = ImeAction.Next
                        ),
                        label = {Text("Location")},
                        colors = if (eventDetailsInsertModel.isLocationValid) {
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
                }

                Spacer(modifier = Modifier.weight(0.1f))

                //Type and points dropdown
                var expanded by remember { mutableStateOf(false) }
                var eventType = EventType(0,"", 0)

                for (type in eventDetailsInsertModel.types) {
                    if (eventDetailsInsertModel.type_id == type.typeId) {
                        eventType = type
                    }
                }
                var selectedType = "${eventType.typeName} : ${eventType.typePoints} Points"
                Column() {

                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {selectedType = it},
                        modifier = Modifier.width(200.dp),
                        readOnly = true,
                        label = {Text("Type : Points")},
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown,"", modifier = Modifier.clickable { expanded = true })
                        },


                        )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {expanded = false},
                        modifier = Modifier.width(300.dp)
                    ) {
                        eventDetailsInsertModel.types.forEach { type ->
                            DropdownMenuItem(
                                onClick = {
                                    component.onEventTypeChanged(type)
                                    expanded = false
                                }
                            ) {
                                Text("${type.typeName} : ${type.typePoints} Points")
                            }
                        }
                    }
                }
            }
        }

    }
}



