package ru.agrachev.emojicalendar.presentation

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices.DEFAULT
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.agrachev.emojicalendar.domain.model.Id
import ru.agrachev.emojicalendar.presentation.core.CacheKey
import ru.agrachev.emojicalendar.presentation.core.CalendarDateStorageKey
import ru.agrachev.emojicalendar.presentation.core.CalendarDateStorageValue
import ru.agrachev.emojicalendar.presentation.core.Constants.INFINITE
import ru.agrachev.emojicalendar.presentation.core.Constants.NOW_INDEX
import ru.agrachev.emojicalendar.presentation.core.LocalDateProvider
import ru.agrachev.emojicalendar.presentation.core.MainCalendarUIModelStorage
import ru.agrachev.emojicalendar.presentation.core.regularOffset
import ru.agrachev.emojicalendar.presentation.widget.modal.DateEventsBottomModal
import ru.agrachev.emojicalendar.presentation.model.LocalizedCalendarResources
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.model.MainCalendarUIModel
import ru.agrachev.emojicalendar.presentation.scope.calendar.EmojiCalendarScope
import ru.agrachev.emojicalendar.presentation.scope.calendar.MainCalendarScope
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme
import ru.agrachev.emojicalendar.presentation.viewmodel.CalendarMviStateHolder
import ru.agrachev.emojicalendar.presentation.viewmodel.CalendarViewModel
import ru.agrachev.emojicalendar.presentation.widget.CalendarMonthItem
import ru.agrachev.emojicalendar.presentation.widget.LocalTextMeasurer
import ru.agrachev.emojicalendar.presentation.widget.MonthItemLayout
import ru.agrachev.emojicalendar.presentation.widget.YearMonthItemContentType
import ru.agrachev.emojicalendar.presentation.widget.YearMonthLazyRow
import java.util.Locale
import kotlin.math.abs

class CalendarActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: CalendarViewModel by viewModel()
        setContent {
            EmojiCalendar(viewModel)
        }
    }
}

@Composable
fun EmojiCalendar(viewModel: CalendarMviStateHolder) {
    EmojiCalendarTheme {
        val currentLocale = LocalConfiguration.currentLocale
        val localizedCalendarResources = remember(currentLocale) {
            viewModel.getLocalizedCalendarResources(currentLocale)
        }
        val textMeasurer = rememberTextMeasurer()
        CompositionLocalProvider(
            LocalLocalizedCalendarResources provides localizedCalendarResources,
            LocalTextMeasurer provides textMeasurer,
        ) {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle(
                initialValue = viewModel.initialUiState,
            )
            val mainCalendarUIModel by remember {
                derivedStateOf {
                    uiState.mainCalendarUIModel
                }
            }
            Scaffold(
                modifier = Modifier.fillMaxSize(),
            ) { innerPadding ->
                with(remember(viewModel) {
                    EmojiCalendarScope(viewModel)
                }) {
                    MainCalendar(
                        calendarUiModelProvider = {
                            mainCalendarUIModel
                        },
                        modifier = Modifier
                            .padding(innerPadding),
                    )
                }
            }
            uiState.eventsBrowserUIModel?.let {
                val initialPendingRuleProvider = remember {
                    {
                        it.pendingRule
                    }
                }
                DateEventsBottomModal(
                    dateUIModel = it.dateModel,
                    initialPendingRuleProvider = initialPendingRuleProvider,
                    calendarStateHolder = viewModel,
                    modifier = Modifier
                        .fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun MainCalendarScope.MainCalendar(
    calendarUiModelProvider: () -> MainCalendarUIModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        YearMonthRow()
        WeekdayRow()
        CalendarGrid(
            calendarRefreshRequestTokenProvider = {
                calendarUiModelProvider().calendarRefreshRequestToken
            },
            mainCalendarDateUIModelFactory = {
                calendarUiModelProvider().mainCalendarDateModelStorage[it]
            },
        )
    }
}

@Composable
private fun MainCalendarScope.YearMonthRow() {
    val backgroundColor = MaterialTheme.colorScheme.background
    val (currentYear, currentMonth) = with(LocalDateProvider.current) {
        Pair(year, monthValue - 1)
    }
    val lazyRowState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentMonth,
    )
    YearMonthLazyRow(
        stickyHeaderContent = { yearOffsetFromNow: Int, modifier: Modifier ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(68.dp)
                    .fillMaxHeight()
                    .drawBehind {
                        drawRect(
                            color = backgroundColor,
                            size = size.copy(width = size.height)
                        )
                    }
                    .padding(horizontal = 2.dp)
                    .background(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(16.dp),
                    )
            ) {
                Text(
                    text = (currentYear + yearOffsetFromNow).toString(),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        },
        itemContent = { monthIndex: Int, modifier: Modifier ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .width(68.dp)
                    .fillMaxHeight()
                    .padding(horizontal = 2.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Text(
                    text = LocalLocalizedCalendarResources.current.monthNames[monthIndex],
                )
            }
        },
        lazyRowState = lazyRowState,
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
    )
    LaunchedEffect(lazyRowState) {
        snapshotFlow {
            currentMonthIndex
        }
            .distinctUntilChanged()
            .collect {
                // TODO rework scrolling logic
                lazyRowState.animateScrollToItem(
                    currentMonth + currentMonthIndex.regularOffset + 1 *
                            (if (lazyRowState.layoutInfo.visibleItemsInfo[3].contentType == YearMonthItemContentType.YEAR) 1 else 0)
                )
            }
    }
}

@Composable
private fun WeekdayRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        LocalLocalizedCalendarResources.current.weekdayNames.forEach {
            Text(
                text = it,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f),
            )
        }
    }
}

@Composable
private fun MainCalendarScope.CalendarGrid(
    calendarRefreshRequestTokenProvider: () -> Id,
    mainCalendarDateUIModelFactory: (CalendarDateStorageKey) -> CalendarDateStorageValue?,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
    ) {
        val boxWithConstraintsScope = this
        val width = boxWithConstraintsScope.maxWidth
        val height = boxWithConstraintsScope.maxHeight
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = NOW_INDEX,
        )
        val currentMonthIndex by remember {
            derivedStateOf {
                with(listState.layoutInfo.visibleItemsInfo) {
                    if (isNullOrEmpty()) {
                        listState.firstVisibleItemIndex
                    } else {
                        minBy { abs(it.offset) }.index
                    }
                }
            }
        }
        LaunchedEffect(listState) {
            snapshotFlow {
                currentMonthIndex
            }
                .distinctUntilChanged()
                .collect {
                    setCurrentMonthIndex(it)
                }
        }
        key(
            calendarRefreshRequestTokenProvider(),
            LocalConfiguration.currentLocale,
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                flingBehavior = rememberSnapFlingBehavior(
                    lazyListState = listState,
                    snapPosition = SnapPosition.Start,
                ),
            ) {
                items(count = INFINITE, key = { it.regularOffset }) { index ->
                    val offset = index.regularOffset
                    val key = remember(offset) {
                        CacheKey(offset)
                    }
                    LaunchedEffect(key) {
                        requestDayModelsCallback(key)
                    }
                    val monthDays =
                        mainCalendarDateUIModelFactory(key)
                            ?: arrayOfNulls<MainCalendarDateUIModel>(
                                7 * requestNumberOfWeeksCallback(
                                    offset
                                )
                            ).toList()
                    MonthItemLayout(
                        width, height,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        monthDays.forEachIndexed { time, model ->
                            key(time) {
                                CalendarMonthItem(
                                    mainCalendarDateModel = model,
                                    currentSelectedIndexProvider = { currentMonthIndex },
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .customBorder(
                                            index = time,
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                        ) then (
                                            model?.let {
                                                Modifier.clickable {
                                                    openModalRequest(model)
                                                }
                                            } ?: Modifier)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
@NonRestartableComposable
fun Dp.toIntPx() = with(LocalDensity.current) {
    this@toIntPx.roundToPx()
}

@Preview(showBackground = true, device = DEFAULT)
@Composable
fun GreetingPreview() {
    val uiState = MainCalendarUIModel(
        mainCalendarDateModelStorage = object : MainCalendarUIModelStorage {
            override fun get(
                key: CalendarDateStorageKey,
            ): CalendarDateStorageValue? = null

            override fun put(
                key: CalendarDateStorageKey,
                value: CalendarDateStorageValue
            ): CalendarDateStorageValue? = TODO()
        },
    )
    EmojiCalendarTheme {
        val scope = remember {
            object : MainCalendarScope {
                override fun requestDayModelsCallback(monthOffset: CalendarDateStorageKey) = TODO()

                override fun openModalRequest(model: MainCalendarDateUIModel) = TODO()

                override fun requestNumberOfWeeksCallback(monthOffset: Int): Int = 4

                override fun setCurrentMonthIndex(index: Int) = TODO()

            }
        }
        val textMeasurer = rememberTextMeasurer()
        CompositionLocalProvider(
            LocalTextMeasurer provides textMeasurer,
        ) {
            scope.MainCalendar(
                calendarUiModelProvider = { uiState },
            )
        }
    }
}

fun Modifier.customBorder(index: Int, width: Dp, color: Color): Modifier =
    this then Modifier.drawWithContent {
        drawContent()
        drawLine(color, Offset.Zero, Offset(size.width, 0f), strokeWidth = width.toPx())
        if (index % 7 != 6) {
            drawLine(
                color,
                Offset(size.width, 0f),
                Offset(size.width, size.height),
                strokeWidth = width.toPx()
            )
        }
    }

@Composable
fun Modifier.shimmer(cornerRadius: Dp = 0.dp): Modifier {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.outlineVariant,
        Color.White,
        MaterialTheme.colorScheme.outlineVariant,
    )

    val transition = rememberInfiniteTransition(
        label = "shimmer",
    )
    val translateAnim by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = FastOutSlowInEasing,
            )
        ),
        label = "translate gradient"
    )

    return this.drawWithCache {
        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim, 0f),
            end = Offset(translateAnim + size.width / 1.5f, size.height),
        )
        val cornerPx = cornerRadius.toPx()
        onDrawWithContent {
            drawRoundRect(
                brush = brush,
                cornerRadius = CornerRadius(cornerPx, cornerPx),
            )
        }
    }
}

val LocalLocalizedCalendarResources = staticCompositionLocalOf {
    LocalizedCalendarResources(
        weekdayNames = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"),
        monthNames = listOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sept",
            "Oct",
            "Nov",
            "Dec",
        ),
    )
}

inline val ProvidableCompositionLocal<Configuration>.currentLocale: Locale
    @Composable @ReadOnlyComposable get() = this.current.locales[0]
