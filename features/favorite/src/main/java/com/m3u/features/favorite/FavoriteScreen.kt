package com.m3u.features.favorite

import android.content.res.Configuration
import android.view.KeyEvent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.m3u.core.architecture.pref.LocalPref
import com.m3u.core.util.basic.title
import com.m3u.data.database.model.Stream
import com.m3u.features.favorite.components.DialogStatus
import com.m3u.features.favorite.components.FavoriteDialog
import com.m3u.features.favorite.components.FavouriteGallery
import com.m3u.i18n.R
import com.m3u.material.components.Background
import com.m3u.material.ktx.interceptVolumeEvent
import com.m3u.material.ktx.isTelevision
import com.m3u.material.ktx.thenIf
import com.m3u.material.model.LocalHazeState
import com.m3u.ui.Destination
import com.m3u.ui.LocalVisiblePageInfos
import com.m3u.ui.Sort
import com.m3u.ui.SortBottomSheet
import com.m3u.ui.TvSortFullScreenDialog
import com.m3u.ui.helper.Action
import com.m3u.ui.helper.LocalHelper
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.haze
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun FavouriteRoute(
    navigateToStream: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: FavouriteViewModel = hiltViewModel()
) {
    val tv = isTelevision()

    val title = stringResource(R.string.ui_title_favourite)
    val helper = LocalHelper.current
    val pref = LocalPref.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val streams by viewModel.streams.collectAsStateWithLifecycle()
    val zapping by viewModel.zapping.collectAsStateWithLifecycle()
    val sorts = viewModel.sorts
    val sort by viewModel.sort.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()

    var isSortSheetVisible by rememberSaveable { mutableStateOf(false) }
    var dialogStatus: DialogStatus by remember { mutableStateOf(DialogStatus.Idle) }

    val visiblePageInfos = LocalVisiblePageInfos.current
    val pageIndex = remember { Destination.Root.entries.indexOf(Destination.Root.Favourite) }
    val isPageInfoVisible = remember(pageIndex, visiblePageInfos) {
        visiblePageInfos.find { it.index == pageIndex } != null
    }

    if (isPageInfoVisible) {
        LifecycleResumeEffect(title) {
            helper.title = title.title()
            helper.actions = persistentListOf(
                Action(
                    icon = Icons.AutoMirrored.Rounded.Sort,
                    contentDescription = "sort",
                    onClick = { isSortSheetVisible = true }
                )
            )
            onPauseOrDispose {
                helper.actions = persistentListOf()
            }
        }
    }

    Background {
        val content = @Composable {
            FavoriteScreen(
                contentPadding = contentPadding,
                rowCount = pref.rowCount,
                streams = streams,
                zapping = zapping,
                onClickStream = { stream ->
                    coroutineScope.launch {
                        helper.play(stream.id)
                        navigateToStream()
                    }
                },
                onLongClickStream = { dialogStatus = DialogStatus.Selections(it) },
                sort = sort,
                modifier = Modifier
                    .fillMaxSize()
                    .thenIf(!tv && pref.godMode) {
                        Modifier.interceptVolumeEvent { event ->
                            pref.rowCount = when (event) {
                                KeyEvent.KEYCODE_VOLUME_UP -> (pref.rowCount - 1).coerceAtLeast(1)
                                KeyEvent.KEYCODE_VOLUME_DOWN -> (pref.rowCount + 1).coerceAtMost(2)
                                else -> return@interceptVolumeEvent
                            }
                        }
                    }
                    .then(modifier)
            )
        }
        content()
        if (!tv) {
            SortBottomSheet(
                visible = isSortSheetVisible,
                sort = sort,
                sorts = sorts,
                sheetState = sheetState,
                onChanged = { viewModel.sort(it) },
                onDismissRequest = { isSortSheetVisible = false }
            )
            FavoriteDialog(
                status = dialogStatus,
                onUpdate = { dialogStatus = it },
                cancelFavorite = { id -> viewModel.cancelFavourite(id) },
                createShortcut = { id ->
                    viewModel.createShortcut(context, id)
                }
            )
        } else {
            TvSortFullScreenDialog(
                visible = dialogStatus != DialogStatus.Idle,
                sort = sort,
                sorts = sorts,
                onChanged = { viewModel.sort(it) },
                onDismissRequest = { dialogStatus = DialogStatus.Idle }
            )
        }
    }
}

@Composable
private fun FavoriteScreen(
    contentPadding: PaddingValues,
    rowCount: Int,
    streams: ImmutableList<Stream>,
    zapping: Stream?,
    onClickStream: (Stream) -> Unit,
    onLongClickStream: (Stream) -> Unit,
    sort: Sort,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val actualRowCount = when (configuration.orientation) {
        Configuration.ORIENTATION_PORTRAIT -> rowCount
        Configuration.ORIENTATION_LANDSCAPE -> rowCount + 2
        else -> rowCount + 2
    }
    FavouriteGallery(
        contentPadding = contentPadding,
        streams = streams,
        zapping = zapping,
        rowCount = actualRowCount,
        sort = sort,
        onClick = onClickStream,
        onLongClick = onLongClickStream,
        modifier = modifier.haze(
            LocalHazeState.current,
            HazeDefaults.style(MaterialTheme.colorScheme.surface)
        )
    )
}