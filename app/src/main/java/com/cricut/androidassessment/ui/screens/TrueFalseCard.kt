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
import com.cricut.androidassessment.model.TrueFalseQuestion
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

/**
 * Created by Clayton Hatathlie on 5/27/25
 **/
@Composable
fun TrueFalseCard(
    question: TrueFalseQuestion,
    onAnswer: (Answer.TrueFalse) -> Unit,
    selected: Answer.TrueFalse?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = question.text,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        listOf(true to "True", false to "False").forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAnswer(Answer.TrueFalse(value)) }
                    .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected?.value == value,
                    onClick = { onAnswer(Answer.TrueFalse(value)) }
                )

                Spacer(Modifier.width(8.dp))

                Text(label)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTrueFalseCard() {
    AndroidAssessmentTheme {
        TrueFalseCard(
            question = TrueFalseQuestion(
                id = "1",
                text = "Is Kotlin the best coding language?",
                correctAnswer = true
            ),
            onAnswer = {},
            selected = Answer.TrueFalse(value = true)
        )
    }
}