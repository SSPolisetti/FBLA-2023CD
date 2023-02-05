package com.app.ui

import com.app.data.DbManager
import com.app.data.Prize
import com.arkivanov.decompose.ComponentContext
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