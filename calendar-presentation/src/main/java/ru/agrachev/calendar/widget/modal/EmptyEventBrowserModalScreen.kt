package ru.agrachev.presentation.widget.modal

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ru.agrachev.presentation.R
import ru.agrachev.presentation.theme.EmojiCalendarTheme

@Composable
fun EmptyEventBrowserModalScreen(
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.lbl_no_events),
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyEventBrowserModalScreenPreview() {
    EmojiCalendarTheme {
        EmptyEventBrowserModalScreen(
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}
