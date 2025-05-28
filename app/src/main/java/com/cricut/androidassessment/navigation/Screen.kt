package com.cricut.androidassessment.navigation

/**
 * Created by Clayton Hatathlie on 5/28/25
 **/
sealed class Screen(val route: String) {
    data object Question : Screen("question/{index}") {
        fun createRoute(index: Int) = "question/$index"
    }

    data object Result : Screen("result")
}