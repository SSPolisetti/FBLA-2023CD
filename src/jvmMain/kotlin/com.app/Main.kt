package com.app

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.app.nav.NavController
import com.app.nav.NavHost


@Composable
@Preview
fun App() {
    val screens : Array<Screen> = Screen.values()
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

