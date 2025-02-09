package com.m3u.features.playlist

import android.content.ContentResolver
import android.content.Context

sealed interface PlaylistEvent {
    data object Refresh : PlaylistEvent
    data class Favourite(val id: Int, val target: Boolean) : PlaylistEvent
    data class Hide(val id: Int) : PlaylistEvent
    data class SavePicture(val id: Int) : PlaylistEvent
    data object ScrollUp : PlaylistEvent
    data class Query(val text: String) : PlaylistEvent
    data class CreateShortcut(val context: Context, val id: Int) : PlaylistEvent
    data class CreateTvRecommend(val contentResolver: ContentResolver, val id: Int) : PlaylistEvent
}
