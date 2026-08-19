package com.cricut.androidassessment.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cricut.androidassessment.R
import com.cricut.androidassessment.domain.QuestionResult
import com.cricut.androidassessment.domain.QuizReport
import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.domain.model.AnswerOption
import com.cricut.androidassessment.domain.model.Question
import com.cricut.androidassessment.ui.preview.SampleQuizData
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

/** The post-quiz scorecard: headline score, per-question review, and actions. */
@Composable
fun ResultsStep(
    report: QuizReport,
    onReviewClick: () -> Unit,
    onRestartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "score-header") {
                ScoreHeader(report = report)
            }
            items(items = report.results, key = { it.question.id }) { result ->
                ResultCard(result = result)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            OutlinedButton(
                onClick = onReviewClick,
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(R.string.action_review_answers))
            }
            Button(
                onClick = onRestartClick,
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(R.string.action_play_again))
            }
        }
    }
}

@Composable
private fun ScoreHeader(report: QuizReport, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.results_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = stringResource(R.string.results_score, report.correctCount, report.totalCount),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = scoreHeadline(report = report),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ResultCard(result: QuestionResult, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(
                        if (result.isCorrect) R.string.verdict_correct else R.string.verdict_incorrect,
                    ),
                )
                Text(
                    text = result.question.prompt,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Text(
                text = stringResource(R.string.result_your_answer, userAnswerText(result = result)),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (!result.isCorrect) {
                Text(
                    text = stringResource(
                        R.string.result_correct_answer,
                        correctAnswerText(question = result.question),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = stringResource(R.string.result_fun_fact, result.question.funFact),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** Human-readable rendering of what the user answered, resolved against the question. */
@Composable
@ReadOnlyComposable
private fun userAnswerText(result: QuestionResult): String {
    val question = result.question
    return when (val answer = result.answer) {
        null -> stringResource(R.string.result_unanswered)

        is Answer.TrueFalse ->
            stringResource(if (answer.choice) R.string.answer_true else R.string.answer_false)

        is Answer.SingleChoice ->
            (question as? Question.MultipleChoice)?.options?.getOrNull(answer.optionIndex)?.text
                ?: stringResource(R.string.result_unanswered)

        is Answer.MultiSelect ->
            (question as? Question.MultipleSelect)?.options
                ?.let { options -> answer.optionIndices.sorted().mapNotNull { options.getOrNull(it)?.text } }
                ?.takeIf { it.isNotEmpty() }
                ?.joinToString()
                ?: stringResource(R.string.result_unanswered)

        is Answer.FreeText -> "“${answer.text.trim()}”"
    }
}

@Composable
@ReadOnlyComposable
private fun correctAnswerText(question: Question): String = when (question) {
    is Question.TrueFalse ->
        stringResource(if (question.correctAnswer) R.string.answer_true else R.string.answer_false)

    is Question.MultipleChoice -> question.options.first(AnswerOption::isCorrect).text

    is Question.MultipleSelect ->
        question.options.filter(AnswerOption::isCorrect).joinToString { it.text }

    is Question.OpenEnded -> question.acceptedAnswers.first()
}

@Composable
@ReadOnlyComposable
private fun scoreHeadline(report: QuizReport): String {
    val ratio = if (report.totalCount == 0) 0f else report.correctCount / report.totalCount.toFloat()
    return stringResource(
        when {
            ratio >= 1f -> R.string.score_perfect
            ratio >= GREAT_SCORE_RATIO -> R.string.score_great
            ratio >= MID_SCORE_RATIO -> R.string.score_mid
            else -> R.string.score_low
        },
    )
}

private const val GREAT_SCORE_RATIO = 0.75f
private const val MID_SCORE_RATIO = 0.5f

@Preview(showBackground = true)
@Composable
private fun ResultsStepPreview() {
    AndroidAssessmentTheme {
        ResultsStep(
            report = SampleQuizData.report,
            onReviewClick = {},
            onRestartClick = {},
        )
    }
}
