package com.cricut.androidassessment.domain

import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.domain.model.AnswerOption
import com.cricut.androidassessment.domain.model.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizGraderTest {

    private val trueFalse = Question.TrueFalse(
        id = "tf",
        prompt = "True or false?",
        funFact = "",
        correctAnswer = true,
    )

    private val multipleChoice = Question.MultipleChoice(
        id = "mc",
        prompt = "Pick one",
        funFact = "",
        options = listOf(
            AnswerOption(text = "wrong"),
            AnswerOption(text = "right", isCorrect = true),
            AnswerOption(text = "also wrong"),
        ),
    )

    private val multipleSelect = Question.MultipleSelect(
        id = "ms",
        prompt = "Pick some",
        funFact = "",
        options = listOf(
            AnswerOption(text = "yes", isCorrect = true),
            AnswerOption(text = "no"),
            AnswerOption(text = "also yes", isCorrect = true),
        ),
    )

    private val openEnded = Question.OpenEnded(
        id = "oe",
        prompt = "Type it",
        funFact = "",
        acceptedAnswers = listOf("recomposition", "recompose"),
    )

    private fun gradeSingle(question: Question, answer: Answer?): Boolean = QuizGrader.grade(
        questions = listOf(question),
        answers = answer?.let { mapOf(question.id to it) }.orEmpty(),
    ).results.single().isCorrect

    @Test
    fun `true false answers are graded against the correct boolean`() {
        assertTrue(gradeSingle(trueFalse, Answer.TrueFalse(choice = true)))
        assertFalse(gradeSingle(trueFalse, Answer.TrueFalse(choice = false)))
    }

    @Test
    fun `single choice answers are graded by the selected option's correctness`() {
        assertTrue(gradeSingle(multipleChoice, Answer.SingleChoice(optionIndex = 1)))
        assertFalse(gradeSingle(multipleChoice, Answer.SingleChoice(optionIndex = 0)))
        assertFalse(gradeSingle(multipleChoice, Answer.SingleChoice(optionIndex = 99)))
    }

    @Test
    fun `multi select answers must match the correct set exactly`() {
        assertTrue(gradeSingle(multipleSelect, Answer.MultiSelect(optionIndices = setOf(0, 2))))
        assertFalse(gradeSingle(multipleSelect, Answer.MultiSelect(optionIndices = setOf(0))))
        assertFalse(gradeSingle(multipleSelect, Answer.MultiSelect(optionIndices = setOf(0, 1, 2))))
    }

    @Test
    fun `open ended answers match any accepted answer, ignoring case and surrounding words`() {
        assertTrue(gradeSingle(openEnded, Answer.FreeText(text = "Recomposition")))
        assertTrue(gradeSingle(openEnded, Answer.FreeText(text = "it's called RECOMPOSITION!")))
        assertFalse(gradeSingle(openEnded, Answer.FreeText(text = "inflation")))
    }

    @Test
    fun `unanswered and type-mismatched answers are graded incorrect`() {
        assertFalse(gradeSingle(trueFalse, answer = null))
        assertFalse(gradeSingle(trueFalse, Answer.FreeText(text = "true")))
    }

    @Test
    fun `the report tallies correct answers across the whole quiz`() {
        val report = QuizGrader.grade(
            questions = listOf(trueFalse, multipleChoice, openEnded),
            answers = mapOf(
                trueFalse.id to Answer.TrueFalse(choice = true),
                multipleChoice.id to Answer.SingleChoice(optionIndex = 0),
            ),
        )
        assertEquals(1, report.correctCount)
        assertEquals(3, report.totalCount)
    }
}
