package com.app.nav
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.MutableState
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.saveable.rememberSaveable
//
//class NavController(
//    private val startScreen: String,
//    private var backStack: MutableSet<String> = mutableSetOf(),
//    var screenArgs : MutableMap<String, Any>? = mutableMapOf()
//) {
//    //store state of current screen
//    var curScreen: MutableState<String> = mutableStateOf(startScreen)
//
//    //navigate between screens using back stack
//    fun navigateTo(goTo: String, args: MutableMap<String, Any>?) {
//        print("Test")
//        if (goTo != curScreen.value) {
//            if (backStack.contains(curScreen.value) && curScreen.value != startScreen) {
//                backStack.remove(curScreen.value)
//            }
//            if (goTo == startScreen) {
//                backStack = mutableSetOf()
//            } else {
//                backStack.add(curScreen.value)
//                if (args != null) {
//                    screenArgs = args
//                }
//            }
//        }
//        curScreen.value = goTo
//    }
//}
//    @Composable
//    fun rememberNavController(
//        startScreen: String,
//        backStack: MutableSet<String> = mutableSetOf(),
//        screenArgs : MutableMap<String, Any>? = mutableMapOf()
//    ): MutableState<NavController> = rememberSaveable {
//        mutableStateOf(NavController(startScreen, backStack, screenArgs))
//    }
