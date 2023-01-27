package com.app.nav

import androidx.compose.runtime.Composable

class NavHost (val navController: NavController, val contents: @Composable NavigationGraphBuilder.() -> Unit) {
    @Composable
    fun build() {
        NavigationGraphBuilder().renderContents()
    }

    inner class NavigationGraphBuilder(val navController: NavController = this@NavHost.navController) {
        @Composable
        fun renderContents() {
            this@NavHost.contents(this)
        }
    }

    @Composable
    fun NavHost.NavigationGraphBuilder.composable( destination : String, content: @Composable ()-> Unit) {
        if (navController.curScreen.value == destination) {
            content()
        }
    }
}