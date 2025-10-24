package ru.agrachev.emojicalendar.presentation

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NonRestartableComposable
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
import kotlinx.coroutines.flow.mapNotNull
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.agrachev.emojicalendar.domain.repository.DayModelStorage
import ru.agrachev.emojicalendar.presentation.arch.EmojiCalendarIntent
import ru.agrachev.emojicalendar.presentation.core.Constants
import ru.agrachev.emojicalendar.presentation.model.CalendarRuleUIModel
import ru.agrachev.emojicalendar.presentation.model.LocalizedCalendarResources
import ru.agrachev.emojicalendar.presentation.model.MainCalendarDateUIModel
import ru.agrachev.emojicalendar.presentation.model.MainCalendarUIModel
import ru.agrachev.emojicalendar.presentation.model.emptyCalendarRuleUIModel
import ru.agrachev.emojicalendar.presentation.theme.EmojiCalendarTheme
import ru.agrachev.emojicalendar.presentation.viewmodel.CalendarViewModel
import ru.agrachev.emojicalendar.presentation.widget.CalendarMonthItem
import ru.agrachev.emojicalendar.presentation.widget.DateEventsBottomModal
import ru.agrachev.emojicalendar.presentation.widget.LocalTextMeasurer
import ru.agrachev.emojicalendar.presentation.widget.MonthItemLayout
import ru.agrachev.emojicalendar.presentation.widget.YearMonthLazyRow
import ru.agrachev.emojicalendar.presentation.widget.testModel
import java.time.LocalDate
import kotlin.math.abs

class CalendarActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: CalendarViewModel by viewModel()
        println(viewModel.initialUiState)
        setContent {
            EmojiCalendarTheme {
                val currentLocale = LocalConfiguration.current.locales[0]
                val localizedCalendarResources = remember(currentLocale) {
                    viewModel.getLocalizedCalendarResources(currentLocale)
                }
                CompositionLocalProvider(
                    LocalLocalizedCalendarResources provides localizedCalendarResources,
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
                        initialValue = viewModel.initialUiState,
                    )
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                    ) { innerPadding ->
                        Greeting(
                            requestDayModelsCallback = { offset ->
                                viewModel.accept(
                                    EmojiCalendarIntent.LoadDayModelsForMonth(
                                        monthOffsetFromNow = offset,
                                    )
                                )
                            },
                            openModalRequest = { model ->
                                viewModel.accept(
                                    EmojiCalendarIntent.OpenEventsBrowserModal(
                                        model = model,
                                    )
                                )
                            },
                            requestNumberOfWeeksCallback = { offsetFromCurrentMonth ->
                                viewModel.getNumberOfWeeks(offsetFromCurrentMonth)
                            },
                            uiState = uiState.mainCalendarUIModel,
                            modifier = Modifier
                                .padding(innerPadding),
                        )
                    }
                    uiState.eventsBrowserUIModel?.let {
                        val textMeasurer = rememberTextMeasurer()
                        CompositionLocalProvider(LocalTextMeasurer provides textMeasurer) {
                            DateEventsBottomModal(
                                dateUIModel = it.dateModel,
                                pendingRuleStateProvider = {
                                    viewModel.uiState.mapNotNull { uiState ->
                                        uiState.eventsBrowserUIModel?.pendingRule
                                    }.collectAsStateWithLifecycle(
                                        initialValue = uiState.eventsBrowserUIModel?.pendingRule
                                            ?: emptyCalendarRuleUIModel(it.dateModel.date)
                                    )
                                },
                                pendingRuleUpdater = { updater ->
                                    viewModel.accept(
                                        EmojiCalendarIntent.UpdatePendingRule(
                                            updater
                                        )
                                    )
                                },
                                onCalendarRulePushRequest = {
                                    viewModel.accept(
                                        EmojiCalendarIntent.PushCalendarRule(
                                            it
                                        )
                                    )
                                },
                                onCalendarRulePushSuccess = {
                                    viewModel.accept(
                                        EmojiCalendarIntent.RequestCalendarUpdate
                                    )
                                },
                                labelProvider = {
                                    viewModel.labels
                                },
                                onEventItemClicked = { index ->
                                    viewModel.accept(
                                        EmojiCalendarIntent.NavigateToItem(
                                            index
                                        )
                                    )
                                },
                                onDismissRequest = {
                                    viewModel.accept(
                                        EmojiCalendarIntent.DismissEventsBrowserModal
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                //.wrapContentHeight()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(
    requestDayModelsCallback: (Int) -> Unit,
    openModalRequest: (model: MainCalendarDateUIModel) -> Unit,
    requestNumberOfWeeksCallback: (Int) -> Int,
    uiState: MainCalendarUIModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        val cc = remember {
            LocalDate.now().year
        }
        val aa = remember {
            LocalDate.now().monthValue - 1
        }
        val lazyRowState = rememberLazyListState(
            initialFirstVisibleItemIndex = aa,
        )
        val backgroundColor = MaterialTheme.colorScheme.background
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
                        .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
                ) {
                    Text(
                        text = (cc + yearOffsetFromNow).toString(),
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
                            color = Color.LightGray,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            LocalLocalizedCalendarResources.current.weekdayNames.forEach {
                Text(
                    text = it,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1.0f),
                )
            }
        }
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
        ) {
            val boxWithConstraintsScope = this
            val width = boxWithConstraintsScope.maxWidth
            val height = boxWithConstraintsScope.maxHeight
            val textMeasurer = rememberTextMeasurer()
            val listState = rememberLazyListState(
                initialFirstVisibleItemIndex = Constants.NOW_INDEX,
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
            LaunchedEffect(lazyRowState) {
                snapshotFlow {
                    currentMonthIndex
                }
                    .distinctUntilChanged()
                    .collect {
                        println("AAAAAAAAAA $currentMonthIndex ${lazyRowState.layoutInfo.visibleItemsInfo.map { it.contentType }}")
                        lazyRowState.animateScrollToItem(
                            aa + (currentMonthIndex - Int.MAX_VALUE / 2) + 1 *
                                    (if (lazyRowState.layoutInfo.visibleItemsInfo[3].contentType == "yearTile") 1 else 0)
                        )
                        //n = it
                    }
            }
            CompositionLocalProvider(LocalTextMeasurer provides textMeasurer) {
                key(uiState.calendarRefreshRequestId, LocalConfiguration.current.locales[0]) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        flingBehavior = rememberSnapFlingBehavior(
                            lazyListState = listState,
                            snapPosition = SnapPosition.Start,
                        ),
                    ) {
                        items(count = Int.MAX_VALUE) { index ->
                            val offset = index - Int.MAX_VALUE / 2
                            LaunchedEffect(offset) {
                                requestDayModelsCallback.invoke(offset)
                            }
                            val monthDays =
                                uiState.mainCalendarDateModelStorage[offset]
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
                                            currentMonthIndex,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .customBorder(
                                                    index = time,
                                                    width = 1.dp,
                                                    color = Color.LightGray,
                                                )
                                                .then(model?.let {
                                                    Modifier.clickable {
                                                        openModalRequest(model)
                                                    }
                                                } ?: Modifier),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@NonRestartableComposable
fun Dp.toIntPx() = with(LocalDensity.current) {
    this@toIntPx.roundToPx()
}

@Preview(showBackground = true, device = DEFAULT)
@Composable
fun GreetingPreview() {
    val uiState = MainCalendarUIModel(
        mainCalendarDateModelStorage = object :
            DayModelStorage<Int, List<MainCalendarDateUIModel>> {
            override fun get(key: Int): List<MainCalendarDateUIModel>? = null

            override fun put(
                key: Int,
                value: List<MainCalendarDateUIModel>
            ): List<MainCalendarDateUIModel>? {
                TODO("Not yet implemented")
            }

            override fun remove(key: Int) {
                TODO("Not yet implemented")
            }

        },
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
            "Dec"
        ),

        )
    EmojiCalendarTheme {
        Greeting(
            requestDayModelsCallback = { _ ->
                List(42) {
                    testModel
                }
            },
            requestNumberOfWeeksCallback = {
                0
            },
            openModalRequest = { },
            uiState = uiState,
        )
    }
}

@Composable
fun Modifier.customBorder(index: Int, width: Dp, color: Color): Modifier =
    this.drawWithContent {
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
        Color.LightGray,
        Color.White,
        Color.LightGray
    )

    val transition = rememberInfiniteTransition(
        label = "shimmer",
    )
    val translateAnim by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600, // slower = smoother
                easing = FastOutSlowInEasing // smoother easing
            )
        ),
        label = "translate gradient"
    )

    return this.drawWithCache {
        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim, 0f),
            // wider gradient
            end = Offset(translateAnim + size.width / 1.5f, size.height)
        )
        val cornerPx = cornerRadius.toPx()
        onDrawWithContent {
            drawRoundRect(
                brush = brush,
                cornerRadius = CornerRadius(cornerPx, cornerPx),
                size = size
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

