package com.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.data.DbManager
import com.app.data.Event
import com.app.data.EventType
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.doOnResume
import kotlinx.coroutines.*

interface EventListComponent {

    val model: MutableValue<EventListModel>

    fun onEventClicked(event : Event)

    fun onSearchClicked()

    fun onSearchValueChange(searchTerm: String)

    fun onClearSearch()

    fun onAddEventSelected()

    data class EventListModel(
        val events : List<Event>,
        val eventTypes : List<EventType>,
        val searchTerm : String,
        val isUsingSearch : Boolean,
        val isLoading : Boolean
    )
}


class DefaultEventListComponent(
    componentContext: ComponentContext,
    private val onEventSelected : (event : Event, types : List<EventType>) -> Unit,
    private val onAddEventClicked : (types : List<EventType>) -> Unit
) : EventListComponent, ComponentContext by componentContext {
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        lifecycle.doOnCreate {
            loadEvents()
        }


        //When taken back off of backstack, reload data in case user made changes to events
        lifecycle.doOnResume {
            loadEvents()
        }
    }


    override val model: MutableValue<EventListComponent.EventListModel> =
        MutableValue(
            EventListComponent.EventListModel(
                events = emptyList<Event>(),
                eventTypes = emptyList<EventType>(),
                searchTerm = "",
                isUsingSearch = false,
                isLoading = false
            )
        )

    private fun loadEvents() {


        scope.launch {

            //Update model to indicate data is being loaded through the UI
            model.value = model.value.copy(isLoading = true)

            delay(20)
            //Use isUsingSearch to either search or simply load students
            val events = if (model.value.isUsingSearch) {
                DbManager.searchEvent(model.value.searchTerm)
            } else {
                DbManager.loadEvents()
            }
            val types = DbManager.loadTypes()

            delay(20)

            //Update the model with the loaded student data
            model.value = model.value.copy(events = events, eventTypes = types,isLoading = false)


        }
    }


    override fun onSearchClicked() {
        model.value = model.value.copy(isUsingSearch = true)
        loadEvents()

    }



    override fun onEventClicked(event : Event) {
        onEventSelected(event, model.value.eventTypes)
    }

    override fun onClearSearch() {
        model.value = model.value.copy(searchTerm = "", isUsingSearch = false)
        loadEvents()
    }

    override fun onAddEventSelected() {
        onAddEventClicked(model.value.eventTypes)
    }


    override fun onSearchValueChange(searchTerm: String) {
        model.value = model.value.copy(searchTerm = searchTerm)
    }

}

@Composable
fun EventListContent(component: EventListComponent) {
    val eventListModel by component.model.subscribeAsState()

    Column{
        Row(
            modifier = Modifier.align(Alignment.End).padding(top = 10.dp, bottom = 15.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*Search bar*/
            TextField(
                value = eventListModel.searchTerm,
                onValueChange = { component.onSearchValueChange(it) },
                singleLine = true,
                modifier = Modifier.height(75.dp).width(300.dp).padding(start = 10.dp, end = 10.dp),
                label = { Text("Search") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    autoCorrect = false,
                    imeAction = ImeAction.Next
                )
            )

            /*Search Button and Clear Search Button*/
            Button(
                onClick = {
                    component.onSearchClicked()
                },
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text("Search")
            }

            Button(
                onClick = {
                    component.onClearSearch()
                }
            ) {
                Text("Clear Search", textAlign = TextAlign.Center)
            }
        }
            if(eventListModel.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(color = Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
                }
            } else if (eventListModel.events.isNotEmpty()) {
                val stateVertical = rememberScrollState(0)
                Box(
                    modifier = Modifier.height(425.dp).fillMaxWidth().verticalScroll(stateVertical)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        eventListModel.events.forEach { event ->

                            var eventType = EventType(0, "", 0)
                            for (type in eventListModel.eventTypes) {
                                if (event.event_type == type.typeId) {
                                    eventType = type
                                }
                            }

                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(175.dp)
                                        .padding(top = 5.dp, bottom = 5.dp, start = 3.dp, end = 3.dp)
                                        .clickable(onClick = {
                                            component.onEventClicked(event)
                                        })
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Start,
                                    ) {
                                        Column(
                                            modifier = Modifier.width(120.dp),
                                            verticalArrangement = Arrangement.SpaceEvenly,
                                            horizontalAlignment = Alignment.CenterHorizontally

                                        ) {

                                            //Spacer(modifier = Modifier.weight(0.1f))
                                            Text("Name: ${event.name}", softWrap = true)
                                            Spacer(modifier = Modifier.weight(0.1f))
                                            Text("Description: ${event.desc}", maxLines = 4, softWrap = true)
                                        }
                                        Spacer(modifier = Modifier.weight(0.3f))


                                        Column(
                                            verticalArrangement = Arrangement.SpaceEvenly,
                                            horizontalAlignment = Alignment.CenterHorizontally

                                        ) {

                                            //Spacer(modifier = Modifier.weight(0.01f))
                                            Text("Date: ${pgDateToDate(event.date, false)}")

                                            Spacer(modifier = Modifier.weight(0.1f))

                                            Text("Location: ${event.location}")
                                        }

                                        Spacer(modifier = Modifier.weight(0.1f))

                                        Column(
                                            verticalArrangement = Arrangement.SpaceEvenly,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {

                                            //Spacer(modifier = Modifier.weight(0.01f))
                                            Text("Type: ${eventType.typeName}")

                                            Spacer(modifier = Modifier.weight(0.1f))

                                            Text("Points: ${eventType.typePoints}")
                                        }


                                        Spacer(modifier = Modifier.weight(0.1f))

                                    }
                                }
                            }
                        }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).height(425.dp),
                        adapter = rememberScrollbarAdapter(stateVertical)
                    )
                }

            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(425.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Events Found")
                }
            }

            Spacer(modifier = Modifier.weight(0.1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Spacer(modifier = Modifier.weight(0.5f))
                Button(
                    onClick = {
                        component.onAddEventSelected()
                    }
                ) {
                    Text("Add Event")
                }
                Spacer(modifier = Modifier.weight(0.5f))
            }




    }
}




