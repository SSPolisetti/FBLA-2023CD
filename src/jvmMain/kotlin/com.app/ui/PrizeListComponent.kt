package com.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.data.DbManager
import com.app.data.Prize
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


interface PrizeListComponent {

    val model: MutableValue<PrizeListModel>

    fun onPrizeClicked(prize : Prize)


    data class PrizeListModel(
        val prizes : List<Prize>,
        val isLoading : Boolean
    )
}

class DefaultPrizeListComponent(
    componentContext : ComponentContext,
    private val onPrizeSelected : (prize : Prize) -> Unit
) : PrizeListComponent, ComponentContext by componentContext{

    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        lifecycle.doOnCreate {
            loadPrizes()
        }

        lifecycle.doOnDestroy {
            scope.cancel()
        }

        //When taken back off of backstack, reload data in case user made changes to events
        lifecycle.doOnResume {
            loadPrizes()
        }
    }

    override val model: MutableValue<PrizeListComponent.PrizeListModel> =
        MutableValue(
            PrizeListComponent.PrizeListModel(
                prizes = emptyList<Prize>(),
                isLoading = false
            )
        )

    private fun loadPrizes() {

        scope.launch {

            model.value = model.value.copy(isLoading = true)

            val prizes = DbManager.loadPrizes()

            model.value = model.value.copy(prizes = prizes, isLoading = false)

        }

    }

    override fun onPrizeClicked(prize: Prize) {
        onPrizeSelected(prize)
    }

}


@Composable
fun PrizeListContent(component: PrizeListComponent) {

    val prizeListModel by component.model.subscribeAsState()
    Column {
        if (prizeListModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(color = Color.Gray),
                contentAlignment = Alignment.Center

            ) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
            }

        } else if (prizeListModel.prizes.isNotEmpty()) { //Check prize list is empty, if not display clickable prize
            val stateVertical = rememberScrollState(0)
            Box(modifier = Modifier.height(425.dp).fillMaxWidth().verticalScroll(stateVertical)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    prizeListModel.prizes.forEach { prize ->

                        Card(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 5.dp, start = 3.dp, end = 3.dp)
                                .clickable(onClick = {
                                    component.onPrizeClicked(prize)
                                })
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(all = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,

                                ) {
                                var name = prize.name
                                Text("Name: $name")
                                Spacer(modifier = Modifier.weight(1f))
                                Text("Minimum Points: " + prize.min_point.toString())
                                Spacer(modifier = Modifier.weight(0.5f))
                                Text("Is Won: " + prize.is_won)
                                Spacer(modifier = Modifier.weight(0.5f))
                                Text("Prize Type: " + prize.type)
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.1f))
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).height(425.dp),
                    adapter = rememberScrollbarAdapter(stateVertical)
                )
            }

        } else { //If there are no prize records, display message

            Box(
                modifier = Modifier.fillMaxWidth().height(425.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No Prizes Found")
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

                }
            ) {
                Text("Add Prize")
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }

    }

}