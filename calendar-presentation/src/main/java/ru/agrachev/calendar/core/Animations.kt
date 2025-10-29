package ru.agrachev.presentation.core

import androidx.compose.animation.core.spring

private fun <T> bounce(dampingRatio: Float) = spring<T>(
    dampingRatio = dampingRatio,
)

fun <T> bounceLow() = bounce<T>(.7f)

fun <T> bounceHigh() = bounce<T>(.5f)
