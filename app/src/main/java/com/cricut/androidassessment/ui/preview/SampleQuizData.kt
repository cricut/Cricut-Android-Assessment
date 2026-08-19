package com.cricut.androidassessment.ui.preview

import com.cricut.androidassessment.domain.QuizGrader
import com.cricut.androidassessment.domain.QuizReport
import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.domain.model.AnswerOption
import com.cricut.androidassessment.domain.model.Question
import com.cricut.androidassessment.ui.AssessmentUiState

/** Hand-rolled fixtures for `@Preview` composables — never used in real flows. */
internal object SampleQuizData {

    val multipleChoice = Question.MultipleChoice(
        id = "sample-mc",
        prompt = "Which layer should own UI state in the recommended architecture?",
        options = listOf(
            AnswerOption(text = "ViewModel", isCorrect = true),
            AnswerOption(text = "Activity"),
            AnswerOption(text = "A global singleton"),
            AnswerOption(text = "The layout file"),
        ),
        funFact = "ViewModels outlive configuration changes, which is the whole trick.",
    )

    val openEnded = Question.OpenEnded(
        id = "sample-open",
        prompt = "Name Compose's state-driven re-execution mechanism.",
        acceptedAnswers = listOf("recomposition"),
        funFact = "Recomposition skips composables whose inputs haven't changed.",
    )

    val inProgressState = AssessmentUiState.InProgress(
        question = multipleChoice,
        answer = Answer.SingleChoice(optionIndex = 0),
        questionNumber = 2,
        totalQuestions = 4,
        isFirstQuestion = false,
        isLastQuestion = false,
        canAdvance = true,
    )

    val report: QuizReport = QuizGrader.grade(
        questions = listOf(multipleChoice, openEnded),
        answers = mapOf(
            multipleChoice.id to Answer.SingleChoice(optionIndex = 0),
            openEnded.id to Answer.FreeText(text = "delegation"),
        ),
    )
}
