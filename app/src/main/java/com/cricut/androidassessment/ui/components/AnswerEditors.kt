package com.cricut.androidassessment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cricut.androidassessment.R
import com.cricut.androidassessment.domain.model.Answer
import com.cricut.androidassessment.domain.model.AnswerOption
import com.cricut.androidassessment.domain.model.Question
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

/**
 * Renders the right input control for [question] and reports edits as [Answer]s.
 * This is the single place where question types map to their editors.
 */
@Composable
fun AnswerEditor(
    question: Question,
    answer: Answer?,
    onAnswerChange: (Answer) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (question) {
        is Question.TrueFalse -> TrueFalseEditor(
            selected = (answer as? Answer.TrueFalse)?.choice,
            onSelect = { onAnswerChange(Answer.TrueFalse(choice = it)) },
            modifier = modifier,
        )

        is Question.MultipleChoice -> SingleChoiceEditor(
            options = question.options.map(AnswerOption::text),
            selectedIndex = (answer as? Answer.SingleChoice)?.optionIndex,
            onSelect = { onAnswerChange(Answer.SingleChoice(optionIndex = it)) },
            modifier = modifier,
        )

        is Question.MultipleSelect -> MultiSelectEditor(
            options = question.options.map(AnswerOption::text),
            selectedIndices = (answer as? Answer.MultiSelect)?.optionIndices.orEmpty(),
            onSelectionChange = { onAnswerChange(Answer.MultiSelect(optionIndices = it)) },
            modifier = modifier,
        )

        is Question.OpenEnded -> OpenEndedEditor(
            text = (answer as? Answer.FreeText)?.text.orEmpty(),
            onTextChange = { onAnswerChange(Answer.FreeText(text = it)) },
            modifier = modifier,
        )
    }
}

@Composable
private fun TrueFalseEditor(selected: Boolean?, onSelect: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    SingleChoiceEditor(
        options = listOf(stringResource(R.string.answer_true), stringResource(R.string.answer_false)),
        selectedIndex = when (selected) {
            true -> 0
            false -> 1
            null -> null
        },
        onSelect = { onSelect(it == 0) },
        modifier = modifier,
    )
}

@Composable
private fun SingleChoiceEditor(
    options: List<String>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        options.forEachIndexed { index, text ->
            val selected = index == selectedIndex
            AnswerOptionCard(
                text = text,
                selected = selected,
                interactionModifier = Modifier.selectable(
                    selected = selected,
                    role = Role.RadioButton,
                    onClick = { onSelect(index) },
                ),
            ) {
                RadioButton(selected = selected, onClick = null)
            }
        }
    }
}

@Composable
private fun MultiSelectEditor(
    options: List<String>,
    selectedIndices: Set<Int>,
    onSelectionChange: (Set<Int>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(R.string.multi_select_hint),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        options.forEachIndexed { index, text ->
            val checked = index in selectedIndices
            AnswerOptionCard(
                text = text,
                selected = checked,
                interactionModifier = Modifier.toggleable(
                    value = checked,
                    role = Role.Checkbox,
                    onValueChange = { isChecked ->
                        onSelectionChange(if (isChecked) selectedIndices + index else selectedIndices - index)
                    },
                ),
            ) {
                Checkbox(checked = checked, onCheckedChange = null)
            }
        }
    }
}

@Composable
private fun OpenEndedEditor(text: String, onTextChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.open_ended_label)) },
        placeholder = { Text(text = stringResource(R.string.open_ended_placeholder)) },
        supportingText = {
            if (text.isBlank()) {
                Text(text = stringResource(R.string.open_ended_hint))
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    )
}

/** Shared tappable option row: a rounded card with a leading control and label. */
@Composable
private fun AnswerOptionCard(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    interactionModifier: Modifier = Modifier,
    control: @Composable () -> Unit,
) {
    val shape = MaterialTheme.shapes.medium
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(color = containerColor)
            .then(interactionModifier)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        control()
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
private fun SingleChoiceEditorPreview() {
    AndroidAssessmentTheme {
        SingleChoiceEditor(
            options = listOf("ViewModel", "Activity", "A global singleton", "The layout file"),
            selectedIndex = 0,
            onSelect = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MultiSelectEditorPreview() {
    AndroidAssessmentTheme {
        MultiSelectEditor(
            options = listOf("Cupcake", "Tiramisu", "Rocky Road", "Eclair"),
            selectedIndices = setOf(0, 1),
            onSelectionChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OpenEndedEditorPreview() {
    AndroidAssessmentTheme {
        OpenEndedEditor(
            text = "",
            onTextChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
