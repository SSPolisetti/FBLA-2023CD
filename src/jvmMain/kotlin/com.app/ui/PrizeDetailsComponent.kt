package com.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.app.data.Prize
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.MutableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


interface PrizeDetailsComponent {

   val model : MutableValue<PrizeDetailModel>

   fun saveChanges()


    fun deletePrize()

    fun onNameChanged(name : String)
    fun onExitClicked()

    fun onTypeChanged(type : String)

    fun onMinPointsChanged(points : Int)

    fun pointCheck(isValid : Boolean)

    fun nameCheck(isValid : Boolean)

    fun showErrorMessage()

    fun closeErrorMessage()

    data class PrizeDetailModel(
       val prize: Prize,
       val isLoading : Boolean
   )

}



class DefaultPrizeDetailsComponent(
    componentContext: ComponentContext,
    prize : Prize,
    private val onFinished : () -> Unit
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

    override fun onExitClicked() {
        onFinished()
    }

    override fun deletePrize() {
        scope.launch {
            model.value = model.value.copy(isLoading = true)

            DbManager.deletePrize(model.value.prize.prize_id)

            model.value = model.value.copy(isLoading = false)
        }
        onFinished()
    }

    override fun onNameChanged(name : String) {
        model.value = model.value.copy(
            prize = model.value.prize.copy(
                name = name
            )
        )
    }

    override fun onMinPointsChanged(points : Int) {
        model.value = model.value.copy(
            prize = model.value.prize.copy(
                min_point = points
            )
        )
    }


    override fun onTypeChanged(type : String) {

        model.value = model.value.copy(
            prize = model.value.prize.copy(
                type = type
            )
        )

    }

    override fun pointCheck(isValid: Boolean) {
        model.value = model.value.copy(isPointsValid = isValid)
    }

    override fun nameCheck(isValid : Boolean) {
        model.value = model.value.copy(isNameValid = isValid)
    }


    override fun showErrorMessage() {
        model.value = model.value.copy(showDialog = true)
    }

    override fun closeErrorMessage() {
        model.value = model.value.copy(showDialog = false)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ErrorMessage(openDialog: Boolean, component: PrizeDetailsComponent) {
    if (openDialog) {
        AlertDialog(
            onDismissRequest = {
                component.closeErrorMessage()
            },
            title = {
                Text("Invalid Input")
            },
            text = {
                Text("Name and points are required fields")
            },
            buttons = {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        component.closeErrorMessage()
                    }
                ) {
                    Text("Dismiss")
                }
            },
            modifier = Modifier.width(500.dp).height(250.dp)
        )
    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PrizeDetailsContent(component: PrizeDetailsComponent) {
    val prizeDetailsModel by component.model.subscribeAsState()

    ErrorMessage(prizeDetailsModel.showDialog, component)

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            Button(
                onClick = {
                    component.onExitClicked()
                }
            ) {
                Text("Exit")
            }

            Spacer(modifier = Modifier.weight(0.3f))

            Button(
                onClick = {
                    if (prizeDetailsModel.isNameValid && prizeDetailsModel.isPointsValid){
                        component.saveChanges()
                    } else {
                        component.showErrorMessage()
                    }

                }

            ) {
                Text("Save Changes")
            }

            Spacer(modifier = Modifier.weight(0.3f))

            Button(
                onClick = {
                        component.deletePrize()
                }
            ) {
                Text("Delete Prize")
            }

            Spacer(modifier = Modifier.weight(0.2f))

        }

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 10.dp, bottom = 10.dp, start = 5.dp, end = 5.dp)

        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(all = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {

                OutlinedTextField(
                    value = prizeDetailsModel.prize.name,
                    onValueChange = {
                        if(it.length < 30) {
                            component.onNameChanged(it)
                            component.nameCheck(true)
                        }
                        if(it.isEmpty()){
                            component.nameCheck(false)
                        }
                    },
                    colors = if (prizeDetailsModel.isNameValid) {
                        TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high),
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                        )
                    } else {
                        TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.error,
                            unfocusedBorderColor = MaterialTheme.colors.error
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Ascii,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    label = {Text("Name")}
                )

                Spacer(modifier = Modifier.weight(0.5f))

                OutlinedTextField(
                    value = prizeDetailsModel.prize.min_point.toString(),
                    onValueChange = {
                        if(it.isNotEmpty()){
                            component.onMinPointsChanged(Integer.parseInt(it))
                            component.pointCheck(true)
                        } else {
                            component.pointCheck(false)
                        }

                    },
                    colors = if (prizeDetailsModel.isPointsValid) {
                        TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.high),
                            unfocusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                        )
                    } else {
                        TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colors.error,
                            unfocusedBorderColor = MaterialTheme.colors.error
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.width(70.dp),
                    label = {Text("Min Points")}

                )


                var expanded by remember {mutableStateOf(false)}
                var selectedType = prizeDetailsModel.prize.type
                val items = PrizeTypes.values().toList()
                Column {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {
                            selectedType = it
                        },
                        readOnly = true,
                        label = {Text("Prize Type")},
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown,"", modifier = Modifier.clickable { expanded = true })
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {expanded = false},
                        modifier = Modifier.width(300.dp)
                    ) {
                        items.forEach {
                            DropdownMenuItem(
                                onClick = {
                                    component.onTypeChanged(it.label)
                                    expanded = false
                                }
                            ) {
                                Text(it.label)
                            }

                        }
                    }



                }




            }
        }

    }
}