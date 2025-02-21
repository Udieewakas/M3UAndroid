package com.m3u.data.repository

import com.m3u.data.database.model.Stream
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlin.time.Duration

interface StreamRepository {
    fun observe(id: Int): Flow<Stream?>
    fun observeAll(): Flow<List<Stream>>
    suspend fun get(id: Int): Stream?
    @Deprecated("stream url is not unique")
    suspend fun getByUrl(url: String): Stream?
    suspend fun getByPlaylistUrl(playlistUrl: String): List<Stream>
    suspend fun setFavourite(id: Int, target: Boolean)
    suspend fun hide(id: Int, target: Boolean)
    suspend fun reportPlayed(id: Int)
    suspend fun getPlayedRecently(): Stream?
    fun observeAllUnseenFavourites(limit: Duration): Flow<List<Stream>>
}

inline fun StreamRepository.observeAll(
    crossinline predicate: (Stream) -> Boolean
): Flow<List<Stream>> = observeAll()
    .map { it.filter(predicate) }
    .distinctUntilChanged()