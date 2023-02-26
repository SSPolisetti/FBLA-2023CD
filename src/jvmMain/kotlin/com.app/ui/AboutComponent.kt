package com.app.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue

interface AboutComponent{
    val model : MutableValue<AboutModel>

    data class AboutModel(
        val generalAbout : String,
        val studentsAbout : String,
        val eventsAbout : String,
        val prizesAbout : String
    )

}

class DefaultAboutComponent(
    componentContext: ComponentContext
) : AboutComponent {
    override val model: MutableValue<AboutComponent.AboutModel> =
        MutableValue(
            AboutComponent.AboutModel(
                generalAbout = """
                    This program can be used as a point-tracking database for teachers to track students' participation in their school's many events, as well as pick out winners and automatically assign them prizes based on the amount of points they have.
                """.trimIndent(),
                studentsAbout = """
                    On the Students tab, users can see every student record, which includes name, grade, and points. When clicking on an entry, users can edit the student record's information, and track their events. The buttons at the bottom allow the user to generate a report of the point listings, as well as add and remove students from the database.
                """.trimIndent(),
                eventsAbout = """
                    On the Events tab, users can keep track of every event that the school hosts, and are able to access the date, amount of points, location, and type of event that the event is. The user can also add, edit, and delete events from the database. When events are removed, all points associated with that event are also deducted from students who attended it.
                """.trimIndent(),
                prizesAbout = """
                    On the Prizes tab, users can view which prizes are available for awarding to students, which includes the points required to earn each prize, as well as other information about the prize. Prizes can be added, edited, and removed from the database as needed by clicking on each prize record.
                """.trimIndent()

            )
        )


}


@Composable
fun AboutContent(component: AboutComponent) {
    val aboutModel by component.model.subscribeAsState()

    Column(
        modifier = Modifier.padding(end = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val stateVertical = rememberScrollState(0)
        Box(
            modifier = Modifier.fillMaxHeight().fillMaxWidth().verticalScroll(stateVertical)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("General")
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, bottom = 30.dp, start = 5.dp, end = 5.dp)


                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        Spacer(modifier = Modifier.weight(1f))

                        Text(aboutModel.generalAbout)
                    }
                }

                Text("Students")
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, bottom = 30.dp, start = 5.dp, end = 5.dp)
                ) {
                    Row {


                        Spacer(modifier = Modifier.weight(1f))

                        Text(aboutModel.studentsAbout)
                    }
                }

                Text("Events")
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, bottom = 30.dp, start = 5.dp, end = 5.dp)
                ) {
                    Row {


                        Spacer(modifier = Modifier.weight(1f))

                        Text(aboutModel.eventsAbout)
                    }
                }

                Text("Prizes")
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp, bottom = 30.dp, start = 5.dp, end = 5.dp)
                ) {
                    Row {


                        Spacer(modifier = Modifier.weight(1f))

                        Text(aboutModel.prizesAbout)
                    }

                }

            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).height(425.dp),
                adapter = rememberScrollbarAdapter(stateVertical)
            )
        }
    }


}