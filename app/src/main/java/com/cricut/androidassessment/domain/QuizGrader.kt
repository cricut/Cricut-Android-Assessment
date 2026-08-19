package com.cricut.androidassessment.domain

import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.domain.model.Question

/** The graded outcome of a single question. */
data class QuestionResult(val question: Question, val answer: Answer?, val isCorrect: Boolean)

/** The graded outcome of a whole quiz run. */
data class QuizReport(val results: List<QuestionResult>) {
    val correctCount: Int = results.count(QuestionResult::isCorrect)
    val totalCount: Int = results.size
}

/**
 * Pure grading logic — no Android types, no state. Given the questions and the
 * user's answers, produces a [QuizReport].
 */
object QuizGrader {

    fun grade(questions: List<Question>, answers: Map<String, Answer>): QuizReport = QuizReport(
        results = questions.map { question ->
            val answer = answers[question.id]
            QuestionResult(
                question = question,
                answer = answer,
                isCorrect = answer != null && isCorrect(question = question, answer = answer),
            )
        },
    )

    private fun isCorrect(question: Question, answer: Answer): Boolean = when (question) {
        is Question.TrueFalse ->
            (answer as? Answer.TrueFalse)?.choice == question.correctAnswer

        is Question.MultipleChoice ->
            (answer as? Answer.SingleChoice)
                ?.let { question.options.getOrNull(it.optionIndex)?.isCorrect } == true

        is Question.MultipleSelect ->
            (answer as? Answer.MultiSelect)?.optionIndices == question.correctOptionIndices

        is Question.OpenEnded ->
            (answer as? Answer.FreeText)
                ?.let { free -> question.acceptedAnswers.any { free.text.contains(it, ignoreCase = true) } } == true
    }
}
