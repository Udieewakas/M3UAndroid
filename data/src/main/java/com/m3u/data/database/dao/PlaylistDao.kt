package com.m3u.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.m3u.data.database.model.Playlist
import com.m3u.data.database.model.PlaylistWithStreams
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(playlist: Playlist): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(vararg playlists: Playlist)

    @Delete
    suspend fun delete(vararg playlist: Playlist)

    @Query("DELETE FROM playlists WHERE url = :url")
    suspend fun deleteByUrl(url: String)

    @Query("SELECT * FROM playlists WHERE url = :url")
    suspend fun getByUrl(url: String): Playlist?

    @Query("SELECT * FROM playlists ORDER BY title")
    fun observeAll(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM playlists ORDER BY title")
    fun observeAllWithStreams(): Flow<List<PlaylistWithStreams>>

    @Transaction
    @Query("SELECT * FROM playlists ORDER BY title")
    suspend fun getAllWithStreams(): List<PlaylistWithStreams>

    @Query("SELECT * FROM playlists WHERE url = :url ORDER BY title")
    fun observeByUrl(url: String): Flow<Playlist?>

    @Transaction
    @Query("SELECT * FROM playlists WHERE url = :url ORDER BY title")
    fun observeByUrlWithStreams(url: String): Flow<PlaylistWithStreams?>

    @Transaction
    @Query("SELECT * FROM playlists WHERE url = :url ORDER BY title")
    suspend fun getByUrlWithStreams(url: String): PlaylistWithStreams?

    @Query("UPDATE playlists SET title = :target WHERE url = :url")
    suspend fun rename(url: String, target: String)

    @Transaction
    suspend fun updateUrl(oldUrl: String, newUrl: String) {
        val playlist = getByUrl(oldUrl) ?: return
        insertOrReplace(
            playlist.copy(
                url = newUrl
            )
        )
        // because the url is the primary key so we should delete it manual.
        deleteByUrl(oldUrl)
    }

    @Transaction
    suspend fun updatePinnedCategories(url: String, updater: (List<String>) -> List<String>) {
        val playlist = getByUrl(url) ?: return
        insertOrReplace(
            playlist.copy(
                pinnedCategories = updater(playlist.pinnedCategories)
            )
        )
    }

    @Transaction
    suspend fun hideOrUnhideCategory(url: String, category: String) {
        val playlist = getByUrl(url) ?: return
        val prev = playlist.hiddenCategories
        insertOrReplace(
            playlist.copy(
                hiddenCategories = if (category in prev) prev - category
                else prev + category
            )
        )
    }

    @Transaction
    suspend fun updateUserAgent(url: String, userAgent: String?) {
        val playlist = getByUrl(url) ?: return
        insertOrReplace(
            playlist.copy(
                userAgent = userAgent
            )
        )
    }
}
