package com.cricut.androidassessment.model

/**
 * Created by Clayton Hatathlie on 5/27/25
 **/
sealed interface Question {
    val id: String
    val text: String

    fun isCorrect(answer: Answer): Boolean
}

data class TrueFalseQuestion(
    override val id: String,
    override val text: String,
    val correctAnswer: Boolean
) : Question {
    override fun isCorrect(answer: Answer): Boolean =
        (answer as? Answer.TrueFalse)?.value == correctAnswer
}

data class MultipleChoiceQuestion(
    override val id: String,
    override val text: String,
    val options: List<String>,
    val correctAnswer: Int
) : Question {
    override fun isCorrect(answer: Answer): Boolean =
        (answer as? Answer.MultipleChoice)?.selectedIndex == correctAnswer
}
