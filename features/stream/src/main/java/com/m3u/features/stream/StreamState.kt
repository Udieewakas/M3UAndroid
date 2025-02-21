package com.m3u.features.stream

import android.graphics.Rect
import androidx.compose.runtime.Immutable
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import org.jupnp.model.meta.Device

data class StreamState(
    val connected: Device<*, *, *>? = null
) {
    @Immutable
    data class PlayerState(
        val playState: @Player.State Int = Player.STATE_IDLE,
        val videoSize: Rect = Rect(),
        val playerError: PlaybackException? = null,
        val player: Player? = null
    )
}
