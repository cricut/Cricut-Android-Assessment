package com.cricut.androidassessment.ui

import com.cricut.androidassessment.domain.QuizReport
import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.domain.model.Question

/**
 * Everything [com.cricut.androidassessment.ui.screens.AssessmentScreen] needs to
 * render, modelled as a sealed hierarchy so each phase of the quiz is impossible
 * to half-render.
 */
sealed interface AssessmentUiState {

    /** Questions are still "downloading" (the repository is faking a network trip). */
    data object Loading : AssessmentUiState

    /** The user is mid-quiz, looking at exactly one question. */
    data class InProgress(
        val question: Question,
        val answer: Answer?,
        val questionNumber: Int,
        val totalQuestions: Int,
        val isFirstQuestion: Boolean,
        val isLastQuestion: Boolean,
        /** True once the current question is answered well enough to move on. */
        val canAdvance: Boolean,
    ) : AssessmentUiState

    /** The user finished the quiz and is admiring (or mourning) the results. */
    data class Complete(val report: QuizReport) : AssessmentUiState
}
