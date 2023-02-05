package com.app.ui
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.app.data.Event
//import com.app.Screen.EventListScreen
//import com.app.data.DbManager
//import com.app.data.EventType
//import com.app.nav.NavController
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//
//@Composable
//fun EventScreen(navController: NavController, event:Event) {
//    val scope = CoroutineScope(Dispatchers.IO)
//    var isInserting = false
//    if (event.event_id == -1) {
//        isInserting = true
//    }
//
//    Column(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Button(
//            onClick = {
//                navController.navigateTo(EventListScreen.name, null)
//            },
//            modifier = Modifier.align(Alignment.Start).padding(10.dp)
//        ) {
//            Text("Exit")
//        }
//        var eventName by remember { mutableStateOf("") }
//        var eventDesc by remember { mutableStateOf("") }
//        var eventDate by remember { mutableStateOf("") }
//        var eventTypeId by remember { mutableStateOf(-1) }
//        var eventLocation by remember { mutableStateOf("") }
//        var types = emptyList<EventType>()
//        scope.launch {
//            types = DbManager.loadTypes()
//            if (!isInserting) {
//                eventName = event.name
//                eventDesc = event.desc
//                eventDate = dateToField(event.date.toString())
//                eventTypeId = event.event_type
//                eventLocation = event.location
//            }
//        }
//
//
//        Box(
//            modifier = Modifier.fillMaxSize()
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(
//
//                ) {
//                    TextField(
//                        value = eventName,
//                        onValueChange = { eventName = it },
//                        label = { Text("Name") },
//                        singleLine = true
//                    )
//                    TextField(
//                        value = eventDesc,
//                        onValueChange = { eventDesc = it },
//                        label = { Text("Description") },
//                        singleLine = false,
//                        maxLines = 7
//                    )
//                }
//                Column(
//
//                ) {
//                    TextField(
//                        value = eventDate,
//                        onValueChange = { if (it.length <= 8) eventDate = it },
//                        singleLine = true,
//                        visualTransformation = DateTransformation()
//                    )
//                    TextField(
//                        value = eventLocation,
//                        onValueChange = { if (it.length <= 50) eventLocation = it },
//                        singleLine = true
//                    )
//                    TypesDropdown(types, isInserting, event.event_type) {
//                        eventTypeId = it
//                    }
//                    if (!isInserting) {
//                        Button(
//                            onClick = {
//                                scope.launch {
//                                    DbManager.editEvent(Event(
//                                        event.event_id,
//                                        eventName,
//                                        eventDesc,
//                                        fieldToDate(eventDate).toString(),
//                                        eventTypeId,
//                                        eventLocation
//                                    ))
//                                }
//                                navController.navigateTo(EventListScreen.name, null)
//                            }
//                        ) {
//                            Text("Insert")
//                        }
//                    } else {
//                        Button(
//                            onClick = {
//                                scope.launch {
//                                    DbManager.addEvent(Event(-1,
//                                        eventName,
//                                        eventDesc,
//                                        fieldToDate(eventDate).toString(),
//                                        eventTypeId,
//                                        eventLocation
//                                    ))
//                                }
//                                navController.navigateTo(EventListScreen.name, null)
//                            }
//                        ) {
//                            Text("Save")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun TypesDropdown(typesList : List<EventType>, isInserting : Boolean, typeId:Int, setTypeId: (Int) -> Unit) {
//    var expanded by remember { mutableStateOf(false)}
//    var initialIndex = typeId
//    if (!isInserting) {
//        for (type in typesList) {
//            if (typeId == type.typeId) {
//                initialIndex = typesList.indexOf(type)
//            }
//        }
//    }
//    var selectedIndex by remember { mutableStateOf(initialIndex)}
//    Box(
//        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)
//    ) {
//        Text(
//            typesList[selectedIndex].typeName + ": " + typesList[selectedIndex].typePoints.toString(),
//            modifier = Modifier.fillMaxWidth().clickable(onClick = {if (isInserting) expanded = true})
//        )
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = {expanded = false},
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            typesList.forEachIndexed { index, eventType ->
//                DropdownMenuItem(onClick = {
//                    selectedIndex = index
//                    expanded = false
//
//                }) {
//                    Text(eventType.typeName +": " + eventType.typePoints.toString())
//                }
//            }
//        }
//    }
//}
//
//
//fun dateToField(date : String) : String {
//    val ymd = date.split("-")
//    var dmy = ""
//    ymd.forEach {
//        dmy+= "$it/"
//    }
//    return dmy
//}
//
//fun fieldToDate(date: String): LocalDate {
//    return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/YYYY"))
//}
