package com.cricut.androidassessment.ui

import androidx.lifecycle.SavedStateHandle
import com.cricut.androidassessment.MainDispatcherRule
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.domain.model.AnswerOption
import com.cricut.androidassessment.domain.model.Question
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssessmentViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
            AnswerOption(text = "right", isCorrect = true),
            AnswerOption(text = "wrong"),
        ),
    )

    private val multipleSelect = Question.MultipleSelect(
        id = "ms",
        prompt = "Pick some",
        funFact = "",
        options = listOf(
            AnswerOption(text = "yes", isCorrect = true),
            AnswerOption(text = "no"),
        ),
    )

    private val openEnded = Question.OpenEnded(
        id = "oe",
        prompt = "Type it",
        funFact = "",
        acceptedAnswers = listOf("recomposition"),
    )

    private val questions = listOf(trueFalse, multipleChoice, multipleSelect, openEnded)

    private fun TestScope.createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
        loadGate: CompletableDeferred<Unit>? = null,
    ): AssessmentViewModel {
        val viewModel = AssessmentViewModel(
            quizRepository = FakeQuizRepository(questions = questions, loadGate = loadGate),
            savedStateHandle = savedStateHandle,
        )
        // uiState uses WhileSubscribed, so keep a collector alive for the whole test.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
        return viewModel
    }

    private val AssessmentViewModel.inProgress: AssessmentUiState.InProgress
        get() = uiState.value as AssessmentUiState.InProgress

    @Test
    fun `shows loading until the quiz arrives, then the first question`() = runTest {
        val loadGate = CompletableDeferred<Unit>()
        val viewModel = createViewModel(loadGate = loadGate)

        assertEquals(AssessmentUiState.Loading, viewModel.uiState.value)

        loadGate.complete(Unit)

        val state = viewModel.inProgress
        assertEquals(1, state.questionNumber)
        assertEquals(questions.size, state.totalQuestions)
        assertTrue(state.isFirstQuestion)
        assertFalse(state.canAdvance)
    }

    @Test
    fun `next is a no-op until the current question has a complete answer`() = runTest {
        val viewModel = createViewModel()

        viewModel.onNextClick()
        assertEquals(1, viewModel.inProgress.questionNumber)

        viewModel.onAnswerChange(Answer.TrueFalse(choice = true))
        assertTrue(viewModel.inProgress.canAdvance)

        viewModel.onNextClick()
        assertEquals(2, viewModel.inProgress.questionNumber)
    }

    @Test
    fun `navigating back restores the previously selected answer`() = runTest {
        val viewModel = createViewModel()

        viewModel.onAnswerChange(Answer.TrueFalse(choice = false))
        viewModel.onNextClick()
        assertNull(viewModel.inProgress.answer)

        viewModel.onPreviousClick()

        assertEquals(1, viewModel.inProgress.questionNumber)
        assertEquals(Answer.TrueFalse(choice = false), viewModel.inProgress.answer)
    }

    @Test
    fun `empty multi-selects and blank text answers do not unlock next`() = runTest {
        val viewModel = createViewModel()
        viewModel.answerFirstTwoQuestions()

        viewModel.onAnswerChange(Answer.MultiSelect(optionIndices = emptySet()))
        assertFalse(viewModel.inProgress.canAdvance)
        viewModel.onAnswerChange(Answer.MultiSelect(optionIndices = setOf(0)))
        assertTrue(viewModel.inProgress.canAdvance)
        viewModel.onNextClick()

        viewModel.onAnswerChange(Answer.FreeText(text = "   "))
        assertFalse(viewModel.inProgress.canAdvance)
        viewModel.onAnswerChange(Answer.FreeText(text = "recomposition"))
        assertTrue(viewModel.inProgress.canAdvance)
    }

    @Test
    fun `finishing the last question grades the quiz`() = runTest {
        val viewModel = createViewModel()
        viewModel.answerEverythingCorrectly()

        viewModel.onNextClick()

        val state = viewModel.uiState.value as AssessmentUiState.Complete
        assertEquals(questions.size, state.report.correctCount)
        assertEquals(questions.size, state.report.totalCount)
    }

    @Test
    fun `back from the results screen reopens the last question with answers intact`() = runTest {
        val viewModel = createViewModel()
        viewModel.answerEverythingCorrectly()
        viewModel.onNextClick()

        viewModel.onPreviousClick()

        val state = viewModel.inProgress
        assertEquals(questions.size, state.questionNumber)
        assertEquals(Answer.FreeText(text = "recomposition"), state.answer)
    }

    @Test
    fun `restart clears all answers and returns to the first question`() = runTest {
        val viewModel = createViewModel()
        viewModel.answerEverythingCorrectly()
        viewModel.onNextClick()

        viewModel.onRestartClick()

        val state = viewModel.inProgress
        assertEquals(1, state.questionNumber)
        assertNull(state.answer)
        assertFalse(state.canAdvance)
    }

    @Test
    fun `progress survives ViewModel recreation via SavedStateHandle`() = runTest {
        val savedStateHandle = SavedStateHandle()
        val firstViewModel = createViewModel(savedStateHandle = savedStateHandle)
        firstViewModel.onAnswerChange(Answer.TrueFalse(choice = true))
        firstViewModel.onNextClick()

        val recreatedViewModel = createViewModel(savedStateHandle = savedStateHandle)

        assertEquals(2, recreatedViewModel.inProgress.questionNumber)
        recreatedViewModel.onPreviousClick()
        assertEquals(Answer.TrueFalse(choice = true), recreatedViewModel.inProgress.answer)
    }

    private fun AssessmentViewModel.answerFirstTwoQuestions() {
        onAnswerChange(Answer.TrueFalse(choice = true))
        onNextClick()
        onAnswerChange(Answer.SingleChoice(optionIndex = 0))
        onNextClick()
    }

    private fun AssessmentViewModel.answerEverythingCorrectly() {
        answerFirstTwoQuestions()
        onAnswerChange(Answer.MultiSelect(optionIndices = setOf(0)))
        onNextClick()
        onAnswerChange(Answer.FreeText(text = "recomposition"))
    }
}

private class FakeQuizRepository(
    private val questions: List<Question>,
    private val loadGate: CompletableDeferred<Unit>? = null,
) : QuizRepository {

    override suspend fun loadQuiz(): List<Question> {
        loadGate?.await()
        return questions
    }
}
