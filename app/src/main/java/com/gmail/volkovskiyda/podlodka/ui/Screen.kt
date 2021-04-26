package com.gmail.volkovskiyda.podlodka.ui

sealed class Screen {
    object Main : Screen()
    data class Session(val id: String) : Screen()
}
