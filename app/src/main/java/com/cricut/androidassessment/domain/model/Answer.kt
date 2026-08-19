package com.cricut.androidassessment.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * The user's answer to a [Question], with one shape per question type.
 *
 * Answers are [Parcelable] so in-progress quiz state can ride along in
 * [androidx.lifecycle.SavedStateHandle] and survive process death, not just
 * configuration changes.
 */
sealed interface Answer : Parcelable {

    @Parcelize
    data class TrueFalse(val choice: Boolean) : Answer

    @Parcelize
    data class SingleChoice(val optionIndex: Int) : Answer

    @Parcelize
    data class MultiSelect(val optionIndices: Set<Int>) : Answer

    @Parcelize
    data class FreeText(val text: String) : Answer

    /**
     * Whether this answer is filled in enough to move on to the next question.
     * The UI uses this to gate the Next/Finish button.
     */
    fun isComplete(): Boolean = when (this) {
        is TrueFalse, is SingleChoice -> true
        is MultiSelect -> optionIndices.isNotEmpty()
        is FreeText -> text.isNotBlank()
    }
}
