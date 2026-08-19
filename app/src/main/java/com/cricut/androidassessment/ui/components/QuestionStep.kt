package com.cricut.androidassessment.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cricut.androidassessment.R
import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.ui.AssessmentUiState
import com.cricut.androidassessment.ui.preview.SampleQuizData
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

/** One question, filling the screen: header with progress, prompt, editor, navigation. */
@Composable
fun QuestionStep(
    state: AssessmentUiState.InProgress,
    onAnswerChange: (Answer) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        QuizHeader(
            questionNumber = state.questionNumber,
            totalQuestions = state.totalQuestions,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp),
        ) {
            Text(
                text = state.question.prompt,
                style = MaterialTheme.typography.headlineSmall,
            )
            Spacer(modifier = Modifier.height(24.dp))
            AnswerEditor(
                question = state.question,
                answer = state.answer,
                onAnswerChange = onAnswerChange,
            )
        }
        QuizNavigationBar(
            isFirstQuestion = state.isFirstQuestion,
            isLastQuestion = state.isLastQuestion,
            canAdvance = state.canAdvance,
            onPreviousClick = onPreviousClick,
            onNextClick = onNextClick,
        )
    }
}

@Composable
private fun QuizHeader(questionNumber: Int, totalQuestions: Int, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.quiz_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(R.string.question_progress, questionNumber, totalQuestions),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        val progress by animateFloatAsState(
            targetValue = questionNumber / totalQuestions.toFloat(),
            label = "quizProgress",
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun QuizNavigationBar(
    isFirstQuestion: Boolean,
    isLastQuestion: Boolean,
    canAdvance: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedButton(
            onClick = onPreviousClick,
            modifier = Modifier.weight(1f),
            enabled = !isFirstQuestion,
        ) {
            Text(text = stringResource(R.string.action_back))
        }
        Button(
            onClick = onNextClick,
            modifier = Modifier.weight(1f),
            enabled = canAdvance,
        ) {
            Text(text = stringResource(if (isLastQuestion) R.string.action_finish else R.string.action_next))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuestionStepPreview() {
    AndroidAssessmentTheme {
        QuestionStep(
            state = SampleQuizData.inProgressState,
            onAnswerChange = {},
            onPreviousClick = {},
            onNextClick = {},
        )
    }
}
