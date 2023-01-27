package com.app.nav
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.Stack


class NavController(
    private val login : String,
    private var backStack: Stack<String> = Stack()
) {
    //store state of current screen
    var curScreen : MutableState<String>  = mutableStateOf(login)

    //navigate between screens using back stack
    fun navigate(goTo : String) {
        if (goTo != curScreen.value) {

        }
    }

}