package com.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import com.arkivanov.essenty.lifecycle.doOnCreate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface EventDetailsComponent {
    val model: MutableValue<EventDetailsModel>

    fun onBackButtonClicked()

    fun onNameChanged(name : String)

    fun onDescChanged(desc : String)

    fun onDateChanged(date : String)

    fun onLocationChanged(location : String)

    fun onEventTypeChanged(eventType: EventType)

    fun deleteEvent()

    fun showErrorMessage()

    fun closeErrorMessage()

    fun saveChanges()

    fun dateCheck(isValid: Boolean)


    fun locationCheck(isValid : Boolean)

    fun nameCheck(isValid : Boolean)


    data class EventDetailsModel(
        val event : Event,
        val types : List<EventType>,
        val isLoading : Boolean,
        val isDateValid : Boolean,
        val isNameValid : Boolean,
        val isLocationValid : Boolean,
        val showDialog : Boolean
    )


}


class DefaultEventDetailsComponent (
    componentContext: ComponentContext,
    event : Event,
    private val onFinished : () -> Unit,
    types : List<EventType>
        ) : EventDetailsComponent, ComponentContext by componentContext {

    init {
        lifecycle.doOnCreate() {
            model.value = model.value.copy(
                event = model.value.event.copy(
                    date = pgDateToDate(model.value.event.date, isEditing = true)))
        }
    }


    private val scope = CoroutineScope(Dispatchers.Main)

    override val model: MutableValue<EventDetailsComponent.EventDetailsModel> =
        MutableValue(
            EventDetailsComponent.EventDetailsModel(
                event = event,
                types = types,
                isNameValid = true,
                isDateValid = true,
                isLocationValid = true,
                isLoading = false,
                showDialog = false

            )
        )


    override fun onBackButtonClicked() {
        onFinished()
    }

    override fun onNameChanged(name: String) {
        model.value = model.value.copy(
            event = model.value.event.copy(
                name = name
            )
        )
    }

    override fun onDescChanged(desc: String) {
        model.value = model.value.copy(
            event = model.value.event.copy(
                desc = desc
            )
        )
    }

    override fun onDateChanged(date: String) {
        model.value = model.value.copy(
            event = model.value.event.copy(
                date = date
            )
        )
    }

    override fun onLocationChanged(location: String) {
        model.value = model.value.copy(
            event = model.value.event.copy(
                location = location
            )
        )
    }

    override fun onEventTypeChanged(eventType: EventType) {
        model.value = model.value.copy(
            event = model.value.event.copy(
                event_type = eventType.typeId
            )
        )
    }

    override fun deleteEvent() {
        scope.launch {
            model.value = model.value.copy(isLoading = true)

            DbManager.deleteEvent(model.value.event.event_id)

            model.value = model.value.copy(isLoading = false)

        }
        onFinished()
    }

    override fun saveChanges() {

        scope.launch {
            model.value = model.value.copy(isLoading = true)

            DbManager.editEvent(model.value.event.copy(date = dateToPgDate(model.value.event.date)))

                model.value = model.value.copy(isLoading = false)
            }
        } else {

        }
    }

    }

    override fun dateCheck(isValid : Boolean){
        model.value = model.value.copy(isDateValid = isValid)
    }


    override fun showErrorMessage() {
        model.value = model.value.copy(showDialog = true)
    }

    override fun closeErrorMessage() {
        model.value = model.value.copy(showDialog = false)
    }

    override fun locationCheck(isValid : Boolean) {
        model.value = model.value.copy(isLocationValid = false)
    }


    override fun nameCheck(isValid : Boolean) {
        model.value = model.value.copy(isNameValid = false)
    }



}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErrorMessage(openDialog: Boolean, component: EventDetailsComponent) {
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
            },
            modifier = Modifier.width(500.dp).height(500.dp)
        )
    }
}



@Composable
fun EventDetailsContent(component: EventDetailsComponent) {
    val eventDetailsModel by component.model.subscribeAsState()

    ErrorMessage(eventDetailsModel.showDialog, component)

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
                component.saveChanges()
            }) {
                Text("Save Changes")
            }
            Spacer(modifier = Modifier.weight(0.3f))
            Button(onClick = {
                component.deleteEvent()
            }) {
                Text("Delete Event")
            }
            Spacer(modifier = Modifier.weight(0.2f))
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
                            value = eventDetailsModel.event.name,
                            onValueChange = {
                                if(it.length < 25) {
                                component.onNameChanged(it)
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Ascii,
                                autoCorrect = false,
                                imeAction = ImeAction.Next
                            ),
                            label = {Text("Name")},
                            modifier = Modifier.width(200.dp)

                        )
                        Spacer(modifier = Modifier.weight(0.1f))

                        //Description
                        OutlinedTextField(
                            value = eventDetailsModel.event.desc,
                            onValueChange = {
                                if (it.length < 200) component.onDescChanged(it)
                            },
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Ascii,
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
                            value = eventDetailsModel.event.date,
                            onValueChange = {
                                if (it.length <= 8) {
                                    component.onDateChanged(it)
                                    println(eventDetailsModel.event.date)
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
                            colors = if (eventDetailsModel.isDateValid) {
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
                            value = eventDetailsModel.event.location,
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
                                keyboardType = KeyboardType.Ascii,
                                autoCorrect = false,
                                imeAction = ImeAction.Next
                            ),
                            colors = if (eventDetailsModel.isLocationValid) {
                                TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high),
                                    unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                                )
                            } else {
                                TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = MaterialTheme.colors.error,
                                    unfocusedBorderColor = MaterialTheme.colors.error
                                )
                            },
                            label = {Text("Location")}
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.1f))

                    //Type and points dropdown
                    var expanded by remember { mutableStateOf(false)}
                    var eventType = EventType(0,"", 0)

                    for (type in eventDetailsModel.types) {
                        if (eventDetailsModel.event.event_type == type.typeId) {
                            eventType = type
                        }
                    }
                    var selectedType = "${eventType.typeName}:${eventType.typePoints}"
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
                            eventDetailsModel.types.forEach { type ->
                                DropdownMenuItem(
                                    onClick = {
                                        component.onEventTypeChanged(type)
                                        expanded = false
                                    }
                                ) {
                                    Text("${type.typeName}:${type.typePoints}")
                                }
                            }
                        }
                    }
                }

                    Spacer(modifier = Modifier.weight(0.1f))

            }



    }
}

fun dateToPgDate(date: String): String {
    return LocalDate.parse("${date.substring(0,2)}/${date.substring(2,4)}/${date.substring(4)}", DateTimeFormatter.ofPattern("MM/dd/yyyy")).toString()
}