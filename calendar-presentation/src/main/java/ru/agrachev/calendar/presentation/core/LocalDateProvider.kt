package ru.agrachev.calendar.presentation.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.LocalDate

object LocalDateProvider {
    val current: LocalDate
        @Composable get() = remember(System.currentTimeMillis() / ((60L * 60L * 1000L))) {
            LocalDate.now()
        }
}
