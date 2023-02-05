package com.app.ui
//
//import androidx.compose.foundation.*
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.Button
//import androidx.compose.material.Card
//import androidx.compose.material.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.app.data.Event
//import kotlinx.coroutines.*
//import com.app.data.DbManager
//import java.lang.module.ModuleFinder
//
//@Composable
//fun EventListScreen(onEventSelected: (event: Event) -> Unit) {
//    val scope = CoroutineScope(Dispatchers.IO)
//    var isLoading by remember { mutableStateOf(false)}
//    var searchState by remember { mutableStateOf("")}
//    var useSearch by remember { mutableStateOf(false)}
//    var eventsList by remember { mutableStateOf(emptyList<Event>())}
//    Column {
//        Row {
//            Search(searchState, {searchState = it}, {useSearch = it})
//            Button(
//                onClick = {
//                    useSearch = false
//                    searchState = ""
//                }
//            )    {
//                Text("Clear Search")
//            }
//        }
//        if (useSearch) {
//            scope.launch {
//                isLoading = true
//                eventsList = DbManager.searchEvent(searchState, "event_name")
//                isLoading = false
//            }
//        } else {
//            scope.launch {
//                isLoading = true
//                eventsList = DbManager.loadEvents()
//                isLoading = false
//            }
//        }
//        if (isLoading) {
//            LoadingAnimation()
//        } else {
//            EventsList(eventsList, onEventSelected)
//        }
//    }
//
//    }
//
//
//@Composable
//fun EventsList(events: List<Event>, onEventSelected: (event: Event) -> Unit) {
//    val stateVertical = rememberScrollState(0)
//    Box(
//        modifier = Modifier.fillMaxSize().verticalScroll(stateVertical)
//    ) {
//        Column(
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            events.forEach {event ->
//                EventBox(event, onEventSelected)
//            }
//        }
//        VerticalScrollbar(
//            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
//            adapter = rememberScrollbarAdapter(stateVertical)
//        )
//    }
//}
//
//
//@Composable
//fun EventBox(event: Event, onEventSelected: (event: Event) -> Unit) {
//    Card(
//        shape = RoundedCornerShape(8.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 5.dp, bottom = 5.dp, start = 3.dp, end = 3.dp)
//            .clickable(onClick = {
//                onEventSelected(event)
//            })
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Start
//        ) {
//            Column {
//                Text(event.name)
//                Spacer(modifier = Modifier.weight(0.1f))
//                Text(event.desc)
//            }
//
//            Spacer(modifier = Modifier.weight(0.5f))
//            Text("Date: " + event.date.toString())
//        }
//    }
//}