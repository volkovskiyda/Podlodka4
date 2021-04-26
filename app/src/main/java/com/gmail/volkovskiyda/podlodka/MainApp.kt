package com.gmail.volkovskiyda.podlodka

import android.app.Application
import com.gmail.volkovskiyda.podlodka.data.SessionRepository

class MainApp : Application() {
    val repository = SessionRepository()
}