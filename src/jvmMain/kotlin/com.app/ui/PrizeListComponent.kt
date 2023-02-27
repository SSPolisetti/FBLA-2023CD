package com.app.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import kotlinx.coroutines.*


interface PrizeListComponent {

    val model: MutableValue<PrizeListModel>

    fun onPrizeClicked(prize : Prize)

    fun onAddPrizeSelected()

    data class PrizeListModel(
        val prizes : List<Prize>,
        val isLoading : Boolean
    )
}

class DefaultPrizeListComponent(
    componentContext : ComponentContext,
    private val onPrizeSelected : (prize : Prize) -> Unit,
    private val onAddPrizeClicked : () -> Unit
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

            delay(20)
            delay(20)
            val prizes = DbManager.loadPrizes()



            model.value = model.value.copy(prizes = prizes, isLoading = false)

        }

    }

    override fun onPrizeClicked(prize: Prize) {
        onPrizeSelected(prize)
    }

    override fun onAddPrizeSelected() {
        onAddPrizeClicked()
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
        } else if(prizeListModel.prizes.isNotEmpty()) {
            val stateVertical = rememberScrollState(0)
            Box(
                modifier = Modifier.height(425.dp).fillMaxWidth().wrapContentSize(Alignment.Center).verticalScroll(stateVertical)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 15.dp)
                ) {
                    prizeListModel.prizes.forEach {prize ->

                        Card(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp, bottom = 5.dp, start = 3.dp)
                                .clickable {
                                    component.onPrizeClicked(prize)
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(all = 20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                            ) {
                                Text("${prize.name}")
                                Spacer(modifier = Modifier.weight(0.4f))

                                Text("Points Required: ${prize.min_point}")

                                Spacer(modifier = Modifier.weight(0.5f))

                                Text("Prize Type: ${prize.type}")

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
                    component.onAddPrizeSelected()
                }
            ) {
                Text("Add Prize")
            }
            Spacer(modifier = Modifier.weight(0.5f))


        }


    }

}

enum class PrizeTypes (
    val label : String
        ){
    SchoolReward(
        label = "School Reward"
    ),
    FoodReward(
        label = "Food Item"
    ),
    SchoolSpiritItem(
        label = "School Spirit Item"
    )

}