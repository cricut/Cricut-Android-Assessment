package com.cricut.androidassessment.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cricut.androidassessment.R
import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.ui.AssessmentUiState
import com.cricut.androidassessment.ui.AssessmentViewModel
import com.cricut.androidassessment.ui.components.QuestionStep
import com.cricut.androidassessment.ui.components.ResultsStep
import com.cricut.androidassessment.ui.preview.SampleQuizData
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

/**
 * The quiz's single screen. Collects [AssessmentUiState] from the ViewModel and
 * swaps between loading, one-question-at-a-time, and results content.
 */
@Composable
fun AssessmentScreen(modifier: Modifier = Modifier, viewModel: AssessmentViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // System back mirrors the on-screen Back button: previous question, or from the
    // results screen back into the quiz. On the first question it exits as usual.
    BackHandler(enabled = uiState.allowsBackNavigation()) {
        viewModel.onPreviousClick()
    }

    AssessmentScreenContent(
        uiState = uiState,
        onAnswerChange = viewModel::onAnswerChange,
        onNextClick = viewModel::onNextClick,
        onPreviousClick = viewModel::onPreviousClick,
        onRestartClick = viewModel::onRestartClick,
        modifier = modifier,
    )
}

/** Stateless content, split out so previews and UI tests can drive any state directly. */
@Composable
internal fun AssessmentScreenContent(
    uiState: AssessmentUiState,
    onAnswerChange: (Answer) -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = uiState,
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        transitionSpec = { quizStepTransition() },
        contentKey = { it.stepKey() },
        label = "quizStep",
    ) { state ->
        when (state) {
            AssessmentUiState.Loading -> LoadingStep()

            is AssessmentUiState.InProgress -> QuestionStep(
                state = state,
                onAnswerChange = onAnswerChange,
                onPreviousClick = onPreviousClick,
                onNextClick = onNextClick,
            )

            is AssessmentUiState.Complete -> ResultsStep(
                report = state.report,
                onReviewClick = onPreviousClick,
                onRestartClick = onRestartClick,
            )
        }
    }
}

@Composable
private fun LoadingStep(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.loading_quip),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

/** Slide toward the direction of travel: forward slides in from the right, back from the left. */
private fun AnimatedContentTransitionScope<AssessmentUiState>.quizStepTransition(): ContentTransform {
    val direction = if (targetState.stepOrder() >= initialState.stepOrder()) 1 else -1
    return (slideInHorizontally { fullWidth -> direction * fullWidth / SLIDE_DISTANCE_DIVISOR } + fadeIn())
        .togetherWith(slideOutHorizontally { fullWidth -> -direction * fullWidth / SLIDE_DISTANCE_DIVISOR } + fadeOut())
}

/**
 * Identity of the current step. Keying [AnimatedContent] on this (instead of the whole
 * state) means typing an answer recomposes in place rather than re-running the
 * step transition.
 */
private fun AssessmentUiState.stepKey(): Any = when (this) {
    AssessmentUiState.Loading -> "loading"
    is AssessmentUiState.InProgress -> "question-$questionNumber"
    is AssessmentUiState.Complete -> "results"
}

private fun AssessmentUiState.stepOrder(): Int = when (this) {
    AssessmentUiState.Loading -> 0
    is AssessmentUiState.InProgress -> questionNumber
    is AssessmentUiState.Complete -> Int.MAX_VALUE
}

private fun AssessmentUiState.allowsBackNavigation(): Boolean = when (this) {
    AssessmentUiState.Loading -> false
    is AssessmentUiState.InProgress -> !isFirstQuestion
    is AssessmentUiState.Complete -> true
}

private const val SLIDE_DISTANCE_DIVISOR = 4

@Preview(showBackground = true)
@Composable
private fun AssessmentScreenLoadingPreview() {
    AndroidAssessmentTheme {
        AssessmentScreenContent(
            uiState = AssessmentUiState.Loading,
            onAnswerChange = {},
            onNextClick = {},
            onPreviousClick = {},
            onRestartClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AssessmentScreenQuestionPreview() {
    AndroidAssessmentTheme {
        AssessmentScreenContent(
            uiState = SampleQuizData.inProgressState,
            onAnswerChange = {},
            onNextClick = {},
            onPreviousClick = {},
            onRestartClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AssessmentScreenResultsPreview() {
    AndroidAssessmentTheme {
        AssessmentScreenContent(
            uiState = AssessmentUiState.Complete(report = SampleQuizData.report),
            onAnswerChange = {},
            onNextClick = {},
            onPreviousClick = {},
            onRestartClick = {},
        )
    }
}
