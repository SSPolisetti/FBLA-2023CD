package com.app


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.app.data.DbManager
import com.app.data.Event
import com.app.data.Student
import com.app.nav.NavController
import com.app.nav.NavHost
import com.app.nav.composable
import com.app.nav.rememberNavController
import com.app.ui.*

@Composable
@Preview
fun App() {
    //instantiate the singleton client
    DbManager.client
    //set up navigation system
    val screens : Array<Screen> = Screen.values()
    val navController by rememberNavController(Screen.StudentsListScreen.name)
    val curScreen by remember {navController.curScreen}

    MaterialTheme {

        Surface(modifier = Modifier.background(color = MaterialTheme.colors.background)) {

            Box(modifier = Modifier.fillMaxSize()) {

                NavigationRail(
                    modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight()
                ) {
                    screens.forEach {if (!it.isDeep) {
                        NavigationRailItem(
                            selected = curScreen == it.name,
                            icon = {},
                            label = { Text(it.screen) },
                            onClick = {
                                navController.navigateTo(it.name, null)
                            }
                        )
                       }
                    }
                }
                Box(modifier = Modifier.fillMaxHeight()) {
                    CustomNavHost(navController)
                }
            }
        }
    }

}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

enum class Screen (
    val screen : String,
    var isDeep : Boolean
        ) {
    StudentsListScreen(
        screen = "Students",
        isDeep = false
    ),
    StudentScreen(
        screen = "Student",
        isDeep = true
    ),
    EventListScreen(
        screen = "Events",
        isDeep = false
    ),
    EventScreen(
        screen = "Students",
        isDeep = true
    ),
    LeaderboardScreen(
        screen = "Leaderboard",
        isDeep = false
    )
}

@Composable
fun CustomNavHost(
    navController: NavController
) {
    NavHost(navController) {
        composable(Screen.StudentsListScreen.name) {
            StudentListScreen() {
                student -> navController.navigateTo(
                    Screen.StudentScreen.name,
                    mutableMapOf("student" to student)
                )
            }
        }

        composable(Screen.EventListScreen.name) {
            EventListScreen() { event ->
                navController.navigateTo(
                    Screen.EventScreen.name,
                    mutableMapOf("event" to event)
                )

            }
        }

        composable(Screen.LeaderboardScreen.name) {
            LeaderboardScreen(navController)
        }

        composable(Screen.StudentScreen.name) {
            val student = navController.screenArgs?.get("student") as Student
            StudentScreen(navController, student)
        }

        composable(Screen.EventScreen.name) {
            val event = navController.screenArgs?.get("event") as Event
            EventScreen(navController, event)
        }
    }.build()
}
