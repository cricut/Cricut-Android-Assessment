package com.cricut.androidassessment.data

import com.cricut.androidassessment.domain.model.Question
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LocalQuizRepositoryTest {

    @Test
    fun `serves the bundled quiz after the simulated network delay`() = runTest {
        val repository = LocalQuizRepository(ioDispatcher = StandardTestDispatcher(testScheduler))

        val questions = repository.loadQuiz()

        assertTrue(questions.isNotEmpty())
        assertEquals(questions.size, questions.map(Question::id).distinct().size)
    }

    @Test
    fun `the bundled quiz covers all four question types`() = runTest {
        val repository = LocalQuizRepository(ioDispatcher = StandardTestDispatcher(testScheduler))

        val questions = repository.loadQuiz()

        assertTrue(questions.any { it is Question.TrueFalse })
        assertTrue(questions.any { it is Question.MultipleChoice })
        assertTrue(questions.any { it is Question.MultipleSelect })
        assertTrue(questions.any { it is Question.OpenEnded })
    }
}
