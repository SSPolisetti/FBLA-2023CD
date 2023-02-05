package com.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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

    data class EventDetailsInsertModel(
        val name : String,
        val description : String,
        val s_date : String,
        val type_id : Int,
        val location : String,
        val types : List<EventType>,
        val isLoading : Boolean,
        val isLocked : Boolean
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
                isLocked = false
            )
        )

    override fun onCancelClicked() {
        onFinished()
    }

    override fun insertEvent() {

        scope.launch {

            DbManager.addEvent(
                Event(
                    name = model.value.name,
                    desc = model.value.description,
                    date = model.value.s_date,
                    event_type = model.value.type_id,
                    location = model.value.location
                )
            )


        }
    }
}


@Composable
fun EventDetailsInsertContent(component: EventDetailsInsertComponent) {
    val eventDetailsModel by component.model.subscribeAsState()

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

        }

    }



}

