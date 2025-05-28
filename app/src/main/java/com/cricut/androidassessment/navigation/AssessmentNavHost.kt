package com.cricut.androidassessment.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cricut.androidassessment.ui.screens.AssessmentScreen
import com.cricut.androidassessment.ui.screens.ResultScreen
import com.cricut.androidassessment.viewmodel.AssessmentViewModel

/**
 * Created by Clayton Hatathlie on 5/27/25
 **/
@Composable
fun AssessmentNavHost(viewModel: AssessmentViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Question.createRoute(0)
    ) {
        composable(
            route = Screen.Question.route,
            arguments = listOf(navArgument("index") { type = NavType.IntType })
        ) { backStackEntry ->
            val index = backStackEntry.arguments!!.getInt("index")
            AssessmentScreen(
                index = index,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                navController = navController,
                viewModel = viewModel)
        }
    }
}