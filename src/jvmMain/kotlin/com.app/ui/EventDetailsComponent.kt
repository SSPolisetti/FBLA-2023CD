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

    fun saveChanges()

    data class EventDetailsModel(
        val event : Event,
        val types : List<EventType>,
        val isLoading : Boolean
    )


}


class DefaultEventDetailsComponent (
    componentContext: ComponentContext,
    event : Event,
    private val onFinished : () -> Unit,
    types : List<EventType>
        ) : EventDetailsComponent {



    private val scope = CoroutineScope(Dispatchers.Main)

    override val model: MutableValue<EventDetailsComponent.EventDetailsModel> =
        MutableValue(
            EventDetailsComponent.EventDetailsModel(
                event = event,
                types = types,
                isLoading = false
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

            DbManager.editEvent(model.value.event)

            model.value = model.value.copy(isLoading = false)

        }
    }

}

@Composable
fun EventDetailsContent(component: EventDetailsComponent) {
    val eventDetailsModel by component.model.subscribeAsState()

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
                                keyboardType = KeyboardType.Text,
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
                                if(it.length < 100) {
                                    component.onDescChanged(it)
                                }
                            },
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                autoCorrect = false,
                                imeAction = ImeAction.Next
                            ),
                            maxLines = 4

                        )

                    }


                    Spacer(modifier = Modifier.weight(0.3f))


                    Column(
                        modifier = Modifier.width(150.dp)
                    ) {

                        //Date
                        OutlinedTextField(
                            value = pgDateToDate(eventDetailsModel.event.date),
                            onValueChange = {
                                    component.onDateChanged(dateToPgDate(it))
                            },
                            visualTransformation = DateTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                autoCorrect = false,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.weight(0.1f))

                        //Location
                        OutlinedTextField(
                            value = eventDetailsModel.event.location,
                            onValueChange = {
                                component.onLocationChanged(it)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                autoCorrect = false,
                                imeAction = ImeAction.Next
                            )
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
                            eventDetailsModel.types.forEach { type ->
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

                    Spacer(modifier = Modifier.weight(0.1f))

            }



    }
}

fun dateToPgDate(date: String): String {
    return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/YYYY")).toString()
}