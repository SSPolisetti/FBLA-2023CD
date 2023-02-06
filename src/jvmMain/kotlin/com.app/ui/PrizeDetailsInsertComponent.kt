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


interface PrizeDetailsInsertComponent {

    val model : MutableValue<PrizeDetailsInsertModel>
    fun onNameChanged(name : String)
    fun onCancelClicked()

    fun onTypeChanged(type : String)

    fun onMinPointsChanged(points : Int)

    fun insertPrize()

    fun nameCheck(isValid : Boolean)

    fun pointsCheck(isValid : Boolean)

    fun showErrorMessage()

    fun closeErrorMessage()

    data class PrizeDetailsInsertModel(
        val name : String,
        val min_point : Int,
        val prize_type : String,
        val isLoading : Boolean,
        val isNameValid : Boolean,
        val isPointsValid : Boolean,
        val showDialog : Boolean
    )

}


class DefaultPrizeDetailsInsertComponent(
    componentContext: ComponentContext,
    private val onFinished : () -> Unit
) : PrizeDetailsInsertComponent {

    private val scope = CoroutineScope(Dispatchers.Main)

    override val model: MutableValue<PrizeDetailsInsertComponent.PrizeDetailsInsertModel> =
        MutableValue(
            PrizeDetailsInsertComponent.PrizeDetailsInsertModel(
                name = "",
                min_point = 0,
                prize_type = PrizeTypes.SchoolReward.label,
                isLoading = false,
                isNameValid = true,
                isPointsValid = true,
                showDialog = false
            )
        )


    override fun onNameChanged(name: String) {
        model.value = model.value.copy(name = name)
    }

    override fun onCancelClicked() {
        onFinished()
    }

    override fun onTypeChanged(type: String) {
        model.value = model.value.copy(prize_type = type)
    }

    override fun onMinPointsChanged(points: Int) {
        model.value = model.value.copy(min_point = points)
    }

    override fun insertPrize() {
        scope.launch {

            model.value = model.value.copy(isLoading =  true)

            DbManager.addPrize(Prize(
                0,
                model.value.name,
                model.value.min_point,
                model.value.prize_type
            ))

            model.value = model.value.copy(isLoading = false)

        }

        onFinished()
    }

    override fun nameCheck(isValid: Boolean) {
        model.value = model.value.copy(isNameValid = isValid)
    }

    override fun pointsCheck(isValid: Boolean) {
        model.value = model.value.copy(isPointsValid = isValid)
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
fun ErrorMessage(openDialog: Boolean, component: PrizeDetailsInsertComponent) {
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
fun PrizeDetailsInsertContent(component: PrizeDetailsInsertComponent) {
    val prizeDetailsInsertModel by component.model.subscribeAsState()

    ErrorMessage(prizeDetailsInsertModel.showDialog, component)

    Column {
        Row(
           verticalAlignment = Alignment.CenterVertically
        ){
            Spacer(modifier = Modifier.weight(0.2f))
            Button(
                onClick = {
                  component.onCancelClicked()
                }
            ) {
                Text("Cancel")
            }

            Spacer(modifier = Modifier.weight(0.6f))

            Button(
                onClick = {
                    if (prizeDetailsInsertModel.isPointsValid && prizeDetailsInsertModel.isNameValid) {
                        component.insertPrize()
                    } else {
                        component.showErrorMessage()
                    }
                }
            ) {
                Text("Add Prize")
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
                    value = prizeDetailsInsertModel.name,
                    onValueChange = {
                        if(it.length < 30) {
                            component.onNameChanged(it)
                            component.nameCheck(true)
                        }
                        if(it.isEmpty()){
                            component.nameCheck(false)
                        }
                    },
                    colors = if (prizeDetailsInsertModel.isNameValid) {
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
                        keyboardType = KeyboardType.Number,
                        autoCorrect = false,
                        imeAction = ImeAction.Next
                    ),
                    label = {Text("Name")}
                )

                Spacer(modifier = Modifier.weight(0.2f))


                OutlinedTextField(
                    value = prizeDetailsInsertModel.min_point.toString(),
                    onValueChange = {
                        if(it.isNotEmpty()) {
                            println(it)
                            component.pointsCheck(true)
                            component.onMinPointsChanged(Integer.parseInt(it))

                        } else {
                            component.pointsCheck(false)
                        }
                    },
                    colors = if (prizeDetailsInsertModel.isPointsValid) {
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
                var selectedType = prizeDetailsInsertModel.prize_type
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