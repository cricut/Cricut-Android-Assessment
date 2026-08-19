package com.cricut.androidassessment.domain.model

/**
 * A single quiz question, modelled as a sealed hierarchy so the UI and grading logic
 * handle every question style exhaustively — adding a new question type becomes a
 * compile-time checklist instead of a runtime surprise.
 */
sealed interface Question {

    /** Stable identifier used to key the user's [Answer] to this question. */
    val id: String

    /** The question text shown to the user. */
    val prompt: String

    /** A little trivia served alongside the verdict on the results screen. */
    val funFact: String

    data class TrueFalse(
        override val id: String,
        override val prompt: String,
        override val funFact: String,
        val correctAnswer: Boolean,
    ) : Question

    data class MultipleChoice(
        override val id: String,
        override val prompt: String,
        override val funFact: String,
        val options: List<AnswerOption>,
    ) : Question {
        init {
            require(options.count(AnswerOption::isCorrect) == 1) {
                "A multiple-choice question needs exactly one correct option"
            }
        }
    }

    data class MultipleSelect(
        override val id: String,
        override val prompt: String,
        override val funFact: String,
        val options: List<AnswerOption>,
    ) : Question {
        init {
            require(options.any(AnswerOption::isCorrect)) {
                "A multiple-select question needs at least one correct option"
            }
        }

        /** Indices of every correct option, e.g. `{0, 2}`. */
        val correctOptionIndices: Set<Int>
            get() = options.mapIndexedNotNull { index, option -> index.takeIf { option.isCorrect } }.toSet()
    }

    data class OpenEnded(
        override val id: String,
        override val prompt: String,
        override val funFact: String,
        val acceptedAnswers: List<String>,
    ) : Question {
        init {
            require(acceptedAnswers.isNotEmpty()) {
                "An open-ended question needs at least one accepted answer"
            }
        }
    }
}

/** One selectable option of a [Question.MultipleChoice] or [Question.MultipleSelect]. */
data class AnswerOption(val text: String, val isCorrect: Boolean = false)
