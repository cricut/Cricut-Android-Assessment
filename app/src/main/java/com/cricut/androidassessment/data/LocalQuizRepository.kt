package com.cricut.androidassessment.data

import com.cricut.androidassessment.data.di.IoDispatcher
import com.cricut.androidassessment.domain.model.Question
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serves the bundled Android trivia while pretending to be a slow network, so the
 * UI's loading path stays honest.
 */
@Singleton
class LocalQuizRepository @Inject constructor(@param:IoDispatcher private val ioDispatcher: CoroutineDispatcher) :
    QuizRepository {

    override suspend fun loadQuiz(): List<Question> = withContext(ioDispatcher) {
        delay(FAKE_NETWORK_DELAY_MS)
        AndroidTriviaQuestions
    }

    private companion object {
        const val FAKE_NETWORK_DELAY_MS = 800L
    }
}
