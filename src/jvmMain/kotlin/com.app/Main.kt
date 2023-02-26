package com.app


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import com.app.data.DbManager
import com.app.nav.*
import com.app.ui.*
import javax.swing.SwingUtilities
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@com.arkivanov.decompose.ExperimentalDecomposeApi
fun main() {
    DbManager.client
    val lifecycle = LifecycleRegistry()

    val root = runOnUiThread {
        DefaultRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle)
        )
    }

    application {
        val windowState = rememberWindowState()

        LifecycleController(lifecycle, windowState)

        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Student Participation Tracker",
            icon = BitmapPainter(useResource("purple_icon_upscaled.jpg", ::loadImageBitmap))

        ) {
            MaterialTheme {
                Surface {
                    RootContent(component = root, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
@com.arkivanov.decompose.ExperimentalDecomposeApi
@Composable
fun RootContent(component : RootComponent, modifier : Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier
    ) {
        Column(modifier = Modifier.width(75.dp)) {

            NavigationRail() {
                NavigationRailItem(
                    selected = false,
                    label = {Text("Students")},
                    icon = { Icons.Default.Person},
                    onClick = {
                        component.navigateToLists("Students")

                    }

                )
                NavigationRailItem(
                    selected = false,
                    label = { Text("Events") },
                    icon = { Icons.Default.LocationOn},
                    onClick = {

                        component.navigateToLists("Events")

                    }
                )
                NavigationRailItem(
                    selected = false,
                    label = {Text("Prizes")},
                    icon = {Icons.Default.ShoppingCart},
                    onClick = {

                        component.navigateToLists("Prizes")

                    }
                )
                NavigationRailItem(
                    selected = false,
                    label = {Text("About")},
                    icon = {Icons.Default.ShoppingCart},
                    onClick = {

                        component.navigateToLists("About")

                    }
                )
            }
        }

        Column (modifier = Modifier.fillMaxHeight().padding(start = 100.dp, end = 25.dp)){
            when (val child = it.instance) {
                is RootComponent.Child.StudentListChild -> StudentListContent(component = child.component)
                is RootComponent.Child.StudentDetailsChild -> StudentDetailsContent(component = child.component)
                is RootComponent.Child.StudentDetailsInsertChild -> StudentDetailsInsertContent(component = child.component)
                is RootComponent.Child.EventListChild -> EventListContent(component = child.component)
                is RootComponent.Child.EventDetailsChild -> EventDetailsContent(component = child.component)
                is RootComponent.Child.EventDetailsInsertChild -> EventDetailsInsertContent(component = child.component)
                is RootComponent.Child.PrizeListChild -> PrizeListContent(component = child.component)
                is RootComponent.Child.PrizeDetailsChild -> PrizeDetailsContent(component = child.component)
                is RootComponent.Child.PrizeDetailsInsertChild -> PrizeDetailsInsertContent(component = child.component)
                is RootComponent.Child.AboutChild -> AboutContent(component = child.component)
            }
        }

    }
}



internal fun <T> runOnUiThread(block: () -> T): T {
    if (SwingUtilities.isEventDispatchThread()) {
        return block()
    }

    var error: Throwable? = null
    var result: T? = null

    SwingUtilities.invokeAndWait {
        try {
            result = block()
        } catch (e: Throwable) {
            error = e
        }
    }

    error?.also { throw it }

    @Suppress("UNCHECKED_CAST")
    return result as T
}


//
//@Composable
//@Preview
//fun App() {
//    //instantiate the singleton client
//    DbManager.client
//    val scope = CoroutineScope(Dispatchers.IO)
//    scope.launch() {
//        val testList = DbManager.loadEvents()
//        print(testList.toString())
//    }
//
//    //set up navigation system
//    val screens : Array<Screen> = Screen.values()
//    val navController by rememberNavController(Screen.StudentsListScreen.name)
//    val curScreen by remember {navController.curScreen}
//
//    MaterialTheme {
//
//        Surface(modifier = Modifier.background(color = MaterialTheme.colors.background)) {
//
//            Box(modifier = Modifier.fillMaxSize()) {
//
//                NavigationRail(
//                    modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight()
//                ) {
//                    screens.forEach {if (!it.isDeep) {
//                        NavigationRailItem(
//                            selected = curScreen == it.name,
//                            icon = {},
//                            label = { Text(it.screen) },
//                            onClick = {
//                                navController.navigateTo(it.name, null)
//                            }
//                        )
//                       }
//                    }
//                }
//                Box(modifier = Modifier.fillMaxHeight()) {
//                    CustomNavHost(navController)
//                }
//            }
//        }
//    }
//
//}
//
//fun main() = application {
//    Window(onCloseRequest = ::exitApplication) {
//        App()
//    }
//}
//
//enum class Screen (
//    val screen : String,
//    var isDeep : Boolean
//        ) {
//    StudentsListScreen(
//        screen = "Students",
//        isDeep = false
//    ),
//    StudentScreen(
//        screen = "Student",
//        isDeep = true
//    ),
//    EventListScreen(
//        screen = "Events",
//        isDeep = false
//    ),
//    EventScreen(
//        screen = "Students",
//        isDeep = true
//    ),
//    PrizesScreen(
//        screen = "Prizes",
//        isDeep = false
//    )
//}
//
//@Composable
//fun CustomNavHost(
//    navController: NavController
//) {
//    NavHost(navController) {
//        composable(Screen.StudentsListScreen.name) {
//            StudentListScreen {
//                student -> navController.navigateTo(
//                    Screen.StudentScreen.name,
//                    mutableMapOf("student" to student)
//                )
//            }
//        }
//
//        composable(Screen.EventListScreen.name) {
//            EventListScreen { event ->
//                navController.navigateTo(
//                    Screen.EventScreen.name,
//                    mutableMapOf("event" to event)
//                )
//
//            }
//        }
//
//        composable(Screen.PrizesScreen.name) {
//            LeaderboardScreen(navController)
//        }
//
//        composable(Screen.StudentScreen.name) {
//            val student = navController.screenArgs?.get("student") as Student
//            StudentScreen(navController, student)
//        }
//
//        composable(Screen.EventScreen.name) {
//            val event = navController.screenArgs?.get("event") as Event
//            EventScreen(navController, event)
//        }
//    }.build()
//}
