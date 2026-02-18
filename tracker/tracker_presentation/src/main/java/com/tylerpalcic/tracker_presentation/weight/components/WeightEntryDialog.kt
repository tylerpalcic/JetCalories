package com.tylerpalcic.tracker_presentation.weight.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun WeightEntryDialog(
    currentWeight: Float?,
    modifier: Modifier = Modifier,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember {
        mutableStateOf(currentWeight?.let { "%.1f".format(it) } ?: "")
    }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = { Text(text = "Log Weight") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { newValue ->
                    text = newValue.filter { it.isDigit() || it == '.' }
                },
                label = { Text("Weight (lbs)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(text) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
