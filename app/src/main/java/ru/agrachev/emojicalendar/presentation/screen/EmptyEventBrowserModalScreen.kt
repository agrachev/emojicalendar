package ru.agrachev.emojicalendar.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme

@Composable
fun EmptyEventBrowserModalScreen(
    modifier: Modifier = Modifier,
    onNewEventClicked: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Button(
            onClick = onNewEventClicked,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            Text(
                text = "New Event",
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyEventBrowserModalScreenPreview() {
    EmojiCalendarTheme {
        EmptyEventBrowserModalScreen(
            modifier = Modifier
                .fillMaxSize(),
            onNewEventClicked = {

            }
        )
    }
}
