package com.cricut.androidassessment.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.domain.QuizGrader
import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.domain.model.Question
import com.cricut.androidassessment.ui.model.QuizProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns a quiz session. Question content comes from the data layer, the user's
 * progress lives in [SavedStateHandle] (so it survives configuration changes AND
 * process death), and the two are combined into a single [AssessmentUiState]
 * stream for the UI to render.
 */
@HiltViewModel
class AssessmentViewModel @Inject constructor(
    quizRepository: QuizRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val questions = MutableStateFlow<List<Question>>(emptyList())
    private val progress = savedStateHandle.getStateFlow(key = KEY_PROGRESS, initialValue = QuizProgress())

    val uiState: StateFlow<AssessmentUiState> =
        combine(questions, progress, ::buildUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
                initialValue = AssessmentUiState.Loading,
            )

    init {
        viewModelScope.launch {
            questions.value = quizRepository.loadQuiz()
        }
    }

    /** Records (or edits) the answer to the question currently on screen. */
    fun onAnswerChange(answer: Answer) {
        if (progress.value.isSubmitted) return
        val current = currentQuestion() ?: return
        updateProgress { it.copy(answers = it.answers + (current.id to answer)) }
    }

    /** Advances to the next question, or submits the quiz from the last one. */
    fun onNextClick() {
        val loaded = questions.value
        val current = progress.value
        if (loaded.isEmpty() || current.isSubmitted) return
        val index = current.currentQuestionIndex.coerceIn(0, loaded.lastIndex)
        if (current.answers[loaded[index].id]?.isComplete() != true) return

        updateProgress {
            if (index == loaded.lastIndex) {
                it.copy(isSubmitted = true)
            } else {
                it.copy(currentQuestionIndex = index + 1)
            }
        }
    }

    /** Steps back one question; from the results screen, re-opens the last question. */
    fun onPreviousClick() {
        updateProgress {
            if (it.isSubmitted) {
                it.copy(isSubmitted = false)
            } else {
                it.copy(currentQuestionIndex = (it.currentQuestionIndex - 1).coerceAtLeast(0))
            }
        }
    }

    /** Wipes all progress and starts the quiz over. */
    fun onRestartClick() {
        savedStateHandle[KEY_PROGRESS] = QuizProgress()
    }

    private fun currentQuestion(): Question? = questions.value.getOrNull(progress.value.currentQuestionIndex)

    private fun updateProgress(transform: (QuizProgress) -> QuizProgress) {
        savedStateHandle[KEY_PROGRESS] = transform(progress.value)
    }

    private fun buildUiState(questions: List<Question>, progress: QuizProgress): AssessmentUiState = when {
        questions.isEmpty() -> AssessmentUiState.Loading

        progress.isSubmitted -> AssessmentUiState.Complete(
            report = QuizGrader.grade(questions = questions, answers = progress.answers),
        )

        else -> {
            val index = progress.currentQuestionIndex.coerceIn(0, questions.lastIndex)
            val question = questions[index]
            val answer = progress.answers[question.id]
            AssessmentUiState.InProgress(
                question = question,
                answer = answer,
                questionNumber = index + 1,
                totalQuestions = questions.size,
                isFirstQuestion = index == 0,
                isLastQuestion = index == questions.lastIndex,
                canAdvance = answer?.isComplete() == true,
            )
        }
    }

    private companion object {
        const val KEY_PROGRESS = "quiz_progress"
        const val STOP_TIMEOUT_MS = 5_000L
    }
}
