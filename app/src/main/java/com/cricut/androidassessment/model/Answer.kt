package com.cricut.androidassessment.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Clayton Hatathlie on 5/28/25
 **/
sealed interface Answer : Parcelable {
    @Parcelize
    data class TrueFalse(val value: Boolean) : Answer

    @Parcelize
    data class MultipleChoice(val selectedIndex: Int) : Answer
}