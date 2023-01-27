package com.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun LoginEntry() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        var email by remember { mutableStateOf("") }
        if (email.isNotEmpty()) {
            Text(
                text = "Email",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.h5
            )
        }
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = {Text("Enter your Email")},

            //For user input from keyboard
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next

            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        var password by remember { mutableStateOf("")}
        if (email.isNotEmpty()) {
            Text(
                text = "Password",
                style = MaterialTheme.typography.h5
            )
        }

    }


}



@Composable
fun LoginButton() {

}


@Composable
fun LoginScreen() {
    MaterialTheme {
        Surface {

        }
    }
}