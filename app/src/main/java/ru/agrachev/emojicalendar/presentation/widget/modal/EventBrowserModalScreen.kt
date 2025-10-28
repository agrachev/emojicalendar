package ru.agrachev.emojicalendar.presentation.widget.modal

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import ru.agrachev.emojicalendar.R
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventBrowserModalScreen(
    dateUIModel: MainCalendarDateUIModel,
    onEventItemClicked: (item: ItemIndex) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 16.dp),
        ) {
            Text(
                text = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                    .format(dateUIModel.date)
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.getDefault()
                        ) else it.toString()
                    }.replaceFirst(", ", ",\n", ignoreCase = true),
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        lineHeight = 1.2.em,
                    )
                ),
                autoSize = TextAutoSize.StepBased(),
                maxLines = 2,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
            )
            FloatingActionButton(
                elevation = FloatingActionButtonDefaults.elevation(0.dp),
                onClick = {
                    onEventItemClicked(CalendarItem.New)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_add_on_24px),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(.7f)
                )
            }
        }
        if (dateUIModel.calendarEvents.isNotEmpty()) {
            EventListBrowserModalScreen(
                calendarEvents = dateUIModel.calendarEvents,
                onEventClicked = { index ->
                    onEventItemClicked(CalendarItem.Selected(index))
                },
                modifier = modifier,
            )
        } else {
            EmptyEventBrowserModalScreen(
                modifier = Modifier
                    .padding(all = 16.dp),
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun EventBrowserModalScreenPreview() {
    EmojiCalendarTheme {
        EventBrowserModalScreen(
            dateUIModel = MainCalendarDateUIModel(
                date = LocalDate.now(),
                calendarEvents = emptyList(),
            ),
            onEventItemClicked = {

            },
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}

@JvmInline
value class ItemIndex(val index: Int)

object CalendarItem {
    fun Selected(index: Int) = ItemIndex(index)
    val New = ItemIndex(-1)
}
