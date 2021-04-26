package com.gmail.volkovskiyda.podlodka.data

import com.gmail.volkovskiyda.podlodka.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class SessionRepository {

    private val favorites = MutableStateFlow(emptySet<String>())

    fun getSession(id: String): Session = requireNotNull(sessions.find { it.id == id })
    fun getSessions() = sessions
    fun observeFavorites(): Flow<Set<String>> = favorites
    fun toggleFavorite(id: String) {
        favorites.value = favorites.value.toMutableSet().apply { if (add(id).not()) remove(id) }
    }
}