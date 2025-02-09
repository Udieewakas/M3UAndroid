package com.m3u.features.favorite.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.items
import com.m3u.core.architecture.pref.LocalPref
import com.m3u.data.database.model.Stream
import com.m3u.material.ktx.isTelevision
import com.m3u.material.ktx.plus
import com.m3u.material.model.LocalSpacing
import com.m3u.ui.Sort
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun FavouriteGallery(
    contentPadding: PaddingValues,
    streams: ImmutableList<Stream>,
    zapping: Stream?,
    rowCount: Int,
    sort: Sort,
    onClick: (Stream) -> Unit,
    onLongClick: (Stream) -> Unit,
    modifier: Modifier = Modifier
) {
    val pref = LocalPref.current
    val compact = pref.compact

    if (!compact) {
        FavouriteGalleryImpl(
            contentPadding = contentPadding,
            streams = streams,
            zapping = zapping,
            rowCount = rowCount,
            sort = sort,
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = modifier
        )
    } else {
        CompactFavouriteGalleryImpl(
            contentPadding = contentPadding,
            streams = streams,
            zapping = zapping,
            rowCount = rowCount,
            sort = sort,
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = modifier
        )
    }
}

@Composable
private fun FavouriteGalleryImpl(
    contentPadding: PaddingValues,
    streams: ImmutableList<Stream>,
    zapping: Stream?,
    rowCount: Int,
    sort: Sort,
    onClick: (Stream) -> Unit,
    onLongClick: (Stream) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val pref = LocalPref.current
    val tv = isTelevision()
    if (!tv) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(rowCount),
            verticalItemSpacing = spacing.medium,
            horizontalArrangement = Arrangement.spacedBy(spacing.large),
            contentPadding = PaddingValues(spacing.medium) + contentPadding,
            modifier = modifier.fillMaxSize(),
        ) {
            items(
                items = streams,
                key = { it.id },
                contentType = { it.cover.isNullOrEmpty() }
            ) { stream ->
                FavoriteItem(
                    stream = stream,
                    noPictureMode = pref.noPictureMode,
                    zapping = zapping == stream,
                    sort = sort,
                    onClick = { onClick(stream) },
                    onLongClick = { onLongClick(stream) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        TvLazyVerticalGrid(
            columns = TvGridCells.Fixed(rowCount),
            verticalArrangement = Arrangement.spacedBy(spacing.large),
            horizontalArrangement = Arrangement.spacedBy(spacing.large),
            contentPadding = PaddingValues(
                vertical = spacing.medium,
                horizontal = spacing.large
            ) + contentPadding,
            modifier = modifier.fillMaxSize(),
        ) {
            items(
                items = streams,
                key = { it.id },
                contentType = { it.cover.isNullOrEmpty() }
            ) { stream ->
                FavoriteItem(
                    stream = stream,
                    noPictureMode = pref.noPictureMode,
                    zapping = zapping == stream,
                    sort = sort,
                    onClick = { onClick(stream) },
                    onLongClick = { onLongClick(stream) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CompactFavouriteGalleryImpl(
    contentPadding: PaddingValues,
    streams: ImmutableList<Stream>,
    zapping: Stream?,
    rowCount: Int,
    sort: Sort,
    onClick: (Stream) -> Unit,
    onLongClick: (Stream) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val pref = LocalPref.current

    val tv = isTelevision()
    if (!tv) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(rowCount),
            contentPadding = contentPadding,
            modifier = modifier.fillMaxSize(),
        ) {
            items(
                items = streams,
                key = { it.id },
                contentType = { it.cover.isNullOrEmpty() }
            ) { stream ->
                FavoriteItem(
                    stream = stream,
                    noPictureMode = pref.noPictureMode,
                    zapping = zapping == stream,
                    sort = sort,
                    onClick = { onClick(stream) },
                    onLongClick = { onLongClick(stream) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        TvLazyVerticalGrid(
            columns = TvGridCells.Fixed(rowCount),
            horizontalArrangement = Arrangement.spacedBy(spacing.medium),
            contentPadding = contentPadding,
            modifier = modifier.fillMaxSize(),
        ) {
            items(
                items = streams,
                key = { it.id },
                contentType = { it.cover.isNullOrEmpty() }
            ) { stream ->
                FavoriteItem(
                    stream = stream,
                    noPictureMode = pref.noPictureMode,
                    zapping = zapping == stream,
                    sort = sort,
                    onClick = { onClick(stream) },
                    onLongClick = { onLongClick(stream) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}