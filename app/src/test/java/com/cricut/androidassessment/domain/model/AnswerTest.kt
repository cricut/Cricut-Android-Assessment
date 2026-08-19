package com.cricut.androidassessment.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnswerTest {

    @Test
    fun `true false and single choice answers are always complete`() {
        assertTrue(Answer.TrueFalse(choice = false).isComplete())
        assertTrue(Answer.SingleChoice(optionIndex = 2).isComplete())
    }

    @Test
    fun `multi select answers need at least one selection`() {
        assertFalse(Answer.MultiSelect(optionIndices = emptySet()).isComplete())
        assertTrue(Answer.MultiSelect(optionIndices = setOf(1)).isComplete())
    }

    @Test
    fun `free text answers need non-blank input`() {
        assertFalse(Answer.FreeText(text = "").isComplete())
        assertFalse(Answer.FreeText(text = "   ").isComplete())
        assertTrue(Answer.FreeText(text = "recomposition").isComplete())
    }
}
