package com.cricut.androidassessment.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cricut.androidassessment.model.Answer
import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.Question
import com.cricut.androidassessment.model.TrueFalseQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AssessmentViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val answersKey = "answers"

    private val _answers = MutableStateFlow(
        savedStateHandle.get<Map<String, Answer>>(answersKey) ?: emptyMap()
    )
    val answers: StateFlow<Map<String, Answer>> = _answers.asStateFlow()

    val quizQuestions: List<Question> = listOf(
        TrueFalseQuestion(
            id = "1",
            text = "Is Kotlin the best coding language?",
            correctAnswer = true
        ),
        MultipleChoiceQuestion(
            id = "2",
            text = "Which lifecycle event does not belong in an Activity?",
            options = listOf(
                "onCreate", "onDestroy", "onTryAgain", "onPause"
            ),
            correctAnswer = 2, // onTryAgain
        ),
        TrueFalseQuestion(
            id = "3",
            text = "Is Compose a declarative UI framework?",
            correctAnswer = true
        )
    )

    fun onAnswer(questionId: String, answer: Answer) {
        _answers.update { it + (questionId to answer) }
        savedStateHandle[answersKey] = _answers.value
    }

    fun answerFor(questionId: String): Answer? = _answers.value[questionId]
}
