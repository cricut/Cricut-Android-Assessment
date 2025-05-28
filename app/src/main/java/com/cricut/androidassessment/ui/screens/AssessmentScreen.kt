package com.cricut.androidassessment.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.cricut.androidassessment.model.Answer
import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.TrueFalseQuestion
import com.cricut.androidassessment.navigation.Screen
import com.cricut.androidassessment.viewmodel.AssessmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssessmentScreen(
    index: Int,
    navController: NavController,
    viewModel: AssessmentViewModel
) {
    val questions = viewModel.quizQuestions
    val prevAnswer = viewModel.answerFor(questions[index].id)

    val answerState = rememberSaveable(questions[index].id) { mutableStateOf(prevAnswer) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Question ${index + 1} of ${questions.size}") }
            )
        },
        bottomBar = {
            NavigationBar {
                if (index > 0) {
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.popBackStack() },
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        },
                        label = { Text("Back") }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (index <= questions.size - 1) {
                    val isLast = index == questions.size - 1
                    NavigationBarItem(
                        selected = false,
                        enabled = answerState.value != null,
                        icon = {
                            Icon(
                                if (isLast) Icons.Default.Done
                                else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                        },
                        label = { Text(if (isLast) "Complete" else "Next") },
                        onClick = {
                            // saves answer
                            answerState.value?.let { viewModel.onAnswer(questions[index].id, it) }
                            if (isLast) {
                                navController.navigate(Screen.Result.route) {
                                    // clears questions from back‑stack so can’t go back
                                    popUpTo(Screen.Question.createRoute(0)) { inclusive = false }
                                }
                            } else {
                                navController.navigate(Screen.Question.createRoute(index + 1))
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (val question = questions[index]) {
                is TrueFalseQuestion ->
                    TrueFalseCard(
                        question = question,
                        onAnswer = answerState::value::set,
                        selected = answerState.value as? Answer.TrueFalse
                    )

                is MultipleChoiceQuestion ->
                    MultipleChoiceCard(
                        question,
                        answerState::value::set,
                        answerState.value as? Answer.MultipleChoice
                    )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun PreviewAssessmentScreen() {
//    AndroidAssessmentTheme {
//        AssessmentScreen(
//            modifier = Modifier,
//            index = 1,
//            navController = rememberNavController(),
//            viewModel = AssessmentViewModel()
//        )
//    }
//}
