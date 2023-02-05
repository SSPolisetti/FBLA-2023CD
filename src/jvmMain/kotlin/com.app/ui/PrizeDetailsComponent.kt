package com.app.ui

import com.app.data.DbManager
import com.app.data.Prize
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


interface PrizeDetailsComponent {

   val model : MutableValue<PrizeDetailModel>

   fun saveChanges()

   data class PrizeDetailModel(
       val prize: Prize,
       val isLoading : Boolean
   )

}



class DefaultPrizeDetailsComponent(
    componentContext: ComponentContext,
    prize : Prize,
    onFinished : () -> Unit
) : PrizeDetailsComponent {

    private val scope = CoroutineScope(Dispatchers.Main)

    override val model: MutableValue<PrizeDetailsComponent.PrizeDetailModel> =
        MutableValue(
            PrizeDetailsComponent.PrizeDetailModel(
                prize = prize,
                isLoading = false
            )
        )

    override fun saveChanges() {
        scope.launch {

            model.value = model.value.copy(isLoading = true)

            DbManager.editPrize(model.value.prize)

            model.value = model.value.copy(isLoading = true)

        }

    }

}