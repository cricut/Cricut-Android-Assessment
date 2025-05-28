package com.cricut.androidassessment.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cricut.androidassessment.model.Answer
import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

/**
 * Created by Clayton Hatathlie on 5/27/25
 **/
@Composable
fun MultipleChoiceCard(
    question: MultipleChoiceQuestion,
    onAnswer: (Answer.MultipleChoice) -> Unit,
    selected: Answer.MultipleChoice?
) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(text = question.text, style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(24.dp))

        question.options.forEachIndexed { i, option ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { onAnswer(Answer.MultipleChoice(i)) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected?.selectedIndex == i,
                    onClick = { onAnswer(Answer.MultipleChoice(i)) }
                )
                Spacer(Modifier.width(8.dp))
                Text(option)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMultipleChoiceCard() {
    AndroidAssessmentTheme {
        MultipleChoiceCard(
            question = MultipleChoiceQuestion(
                id = "1",
                text = "Which lifecycle event does not belong in an Activity?",
                options = listOf(
                    "onCreate",
                    "onDestroy",
                    "onTryAgain",
                    "onPause"
                ),
                correctAnswer = 2 // onTryAgain
            ),
            onAnswer = {},
            selected = Answer.MultipleChoice(selectedIndex = 2)
        )
    }
}