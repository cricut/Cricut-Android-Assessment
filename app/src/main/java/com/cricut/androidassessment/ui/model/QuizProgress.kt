package com.cricut.androidassessment.ui.model

import android.os.Parcelable
import com.cricut.androidassessment.domain.model.Answer
import kotlinx.parcelize.Parcelize

/**
 * Everything the user has done so far, in one parcelable value. Stored in
 * [androidx.lifecycle.SavedStateHandle] so a configuration change, backgrounding,
 * or even process death can't eat the user's answers.
 */
@Parcelize
data class QuizProgress(
    val answers: Map<String, Answer> = emptyMap(),
    val currentQuestionIndex: Int = 0,
    val isSubmitted: Boolean = false,
) : Parcelable
