package com.cricut.androidassessment.data

import com.cricut.androidassessment.domain.model.AnswerOption
import com.cricut.androidassessment.domain.model.Question

/**
 * The bundled question bank. It lives in the data layer because in a real app this
 * content would arrive from an API — the rest of the app only ever sees [Question]s
 * handed out by a [QuizRepository].
 */
internal val AndroidTriviaQuestions: List<Question> = listOf(
    Question.TrueFalse(
        id = "composable-return-value",
        prompt = "True or False: a @Composable function can return a value.",
        correctAnswer = true,
        funFact = "Composables that emit UI return Unit by convention, but remember { }, " +
            "rememberScrollState() and friends are all composable functions that return values.",
    ),
    Question.MultipleChoice(
        id = "first-android-phone",
        prompt = "Android 1.0 shipped in 2008. Which phone had the honor of launching it?",
        options = listOf(
            AnswerOption(text = "HTC Dream (a.k.a. T-Mobile G1)", isCorrect = true),
            AnswerOption(text = "Motorola Droid"),
            AnswerOption(text = "Nexus One"),
            AnswerOption(text = "Samsung Galaxy S"),
        ),
        funFact = "The HTC Dream packed a slide-out keyboard AND a trackball. The Droid (2009) " +
            "and Nexus One (2010) showed up fashionably late.",
    ),
    Question.MultipleSelect(
        id = "dessert-codenames",
        prompt = "Sweet tooth check: select every REAL Android dessert codename.",
        options = listOf(
            AnswerOption(text = "Cupcake", isCorrect = true),
            AnswerOption(text = "Tiramisu", isCorrect = true),
            AnswerOption(text = "Rocky Road"),
            AnswerOption(text = "Eclair", isCorrect = true),
        ),
        funFact = "Cupcake (1.5) and Eclair (2.0) are classics, and Tiramisu lives on as " +
            "Android 13's internal codename. Rocky Road never made the dessert menu.",
    ),
    Question.OpenEnded(
        id = "recomposition",
        prompt = "Fill in the blank: when state changes, Compose re-runs the affected " +
            "composables in a process called ________.",
        acceptedAnswers = listOf("recomposition", "recomposing", "recompose"),
        funFact = "Compose is lazy in the best way — recomposition skips any composable " +
            "whose inputs haven't changed.",
    ),
)
