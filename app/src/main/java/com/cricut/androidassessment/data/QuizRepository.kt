package com.cricut.androidassessment.data

import com.cricut.androidassessment.domain.model.Question

/**
 * Source of quiz content. In this app the implementation is an in-memory fake
 * ([LocalQuizRepository]); a networked implementation could slot in behind the
 * same interface without the UI layer noticing.
 */
interface QuizRepository {

    /** Fetches the ordered list of questions for a quiz run. */
    suspend fun loadQuiz(): List<Question>
}
