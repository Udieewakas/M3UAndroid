package com.m3u.features.setting

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.m3u.core.annotation.ClipMode
import com.m3u.core.annotation.ConnectTimeout
import com.m3u.core.annotation.FeedStrategy
import com.m3u.core.annotation.OnClipMode
import com.m3u.core.annotation.OnFeedStrategy
import com.m3u.data.database.entity.Live
import com.m3u.features.setting.parts.FeedsPart
import com.m3u.features.setting.parts.PreferencesPart
import com.m3u.features.setting.parts.ScriptsPart
import com.m3u.ui.model.LocalHelper
import com.m3u.ui.model.LocalSpacing
import com.m3u.ui.model.LocalTheme

typealias NavigateToConsole = () -> Unit

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingRoute(
    modifier: Modifier = Modifier,
    isCurrentPage: Boolean,
    viewModel: SettingViewModel = hiltViewModel(),
    navigateToConsole: NavigateToConsole
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val helper = LocalHelper.current

    LaunchedEffect(isCurrentPage) {
        if (isCurrentPage) {
            helper.actions = emptyList()
        }
    }

    val configuration = LocalConfiguration.current
    val type = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK
    val useCommonUIMode = if (type == Configuration.UI_MODE_TYPE_NORMAL) true
    else state.useCommonUIMode
    val useCommonUIModeEnable = (type != Configuration.UI_MODE_TYPE_NORMAL)

    val controller = LocalSoftwareKeyboardController.current
    SettingScreen(
        version = state.version,
        title = state.title,
        url = state.url,
        feedStrategy = state.feedStrategy,
        godMode = state.godMode,
        clipMode = state.clipMode,
        scrollMode = state.scrollMode,
        connectTimeout = state.connectTimeout,
        useCommonUIMode = useCommonUIMode,
        useCommonUIModeEnable = useCommonUIModeEnable,
        navigateToConsole = navigateToConsole,
        experimentalMode = state.experimentalMode,
        mutedLives = state.mutedLives,
        onGodMode = { state.godMode = !state.godMode },
        onConnectTimeout = { viewModel.onEvent(SettingEvent.OnConnectTimeout) },
        onTitle = { viewModel.onEvent(SettingEvent.OnTitle(it)) },
        onUrl = { viewModel.onEvent(SettingEvent.OnUrl(it)) },
        onSubscribe = {
            controller?.hide()
            viewModel.onEvent(SettingEvent.Subscribe)
        },
        onScrollMode = { state.scrollMode = !state.scrollMode },
        onFeedStrategy = { viewModel.onEvent(SettingEvent.OnSyncMode) },
        onUIMode = { viewModel.onEvent(SettingEvent.OnUseCommonUIMode) },
        onExperimentalMode = { viewModel.onEvent(SettingEvent.OnExperimentalMode) },
        onBannedLive = { viewModel.onEvent(SettingEvent.OnBannedLive(it)) },
        onClipMode = { viewModel.onEvent(SettingEvent.OnClipMode) },
        autoRefresh = state.autoRefresh,
        onAutoRefresh = { state.autoRefresh = !state.autoRefresh },
        isSSLVerification = state.isSSLVerification,
        onSSLVerification = { state.isSSLVerification = !state.isSSLVerification },
        fullInfoPlayer = state.fullInfoPlayer,
        onFullInfoPlayer = { state.fullInfoPlayer = !state.fullInfoPlayer },
        initialDestination = remember(state.initialDestinationIndex, state.destinations) {
            state.destinations.getOrNull(state.initialDestinationIndex).orEmpty()
        },
        onInitialDestination = { viewModel.onEvent(SettingEvent.OnInitialDestination) },
        noPictureMode = state.noPictureMode,
        onNoPictureMode = { state.noPictureMode = !state.noPictureMode },
        silentMode = state.silentMode,
        onSilentMode = { viewModel.onEvent(SettingEvent.OnSilentMode) },
        cinemaMode = state.cinemaMode,
        onCinemaMode = { state.cinemaMode = !state.cinemaMode },
        importJavaScript = { viewModel.onEvent(SettingEvent.ImportJavaScript(it)) },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
private fun SettingScreen(
    version: String,
    title: String,
    url: String,
    @FeedStrategy feedStrategy: Int,
    godMode: Boolean,
    @ClipMode clipMode: Int,
    @ConnectTimeout connectTimeout: Int,
    scrollMode: Boolean,
    onGodMode: () -> Unit,
    onConnectTimeout: () -> Unit,
    onTitle: (String) -> Unit,
    onUrl: (String) -> Unit,
    onSubscribe: () -> Unit,
    onScrollMode: () -> Unit,
    onFeedStrategy: OnFeedStrategy,
    useCommonUIMode: Boolean,
    useCommonUIModeEnable: Boolean,
    mutedLives: List<Live>,
    onBannedLive: (Int) -> Unit,
    onUIMode: () -> Unit,
    onClipMode: OnClipMode,
    navigateToConsole: NavigateToConsole,
    experimentalMode: Boolean,
    onExperimentalMode: () -> Unit,
    autoRefresh: Boolean,
    onAutoRefresh: () -> Unit,
    isSSLVerification: Boolean,
    onSSLVerification: () -> Unit,
    fullInfoPlayer: Boolean,
    onFullInfoPlayer: () -> Unit,
    initialDestination: String,
    onInitialDestination: () -> Unit,
    noPictureMode: Boolean,
    onNoPictureMode: () -> Unit,
    silentMode: Boolean,
    onSilentMode: () -> Unit,
    cinemaMode: Boolean,
    onCinemaMode: () -> Unit,
    importJavaScript: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    var part: SettingPart by remember { mutableStateOf(SettingPart.PREFERENCE) }
    Box(
        modifier = Modifier.testTag("features:setting")
    ) {
        val configuration = LocalConfiguration.current
        when (configuration.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                PortraitOrientationContent(
                    version = version,
                    part = part,
                    title = title,
                    url = url,
                    godMode = godMode,
                    connectTimeout = connectTimeout,
                    scrollMode = scrollMode,
                    onFold = { part = it },
                    onTitle = onTitle,
                    onUrl = onUrl,
                    onSubscribe = onSubscribe,
                    feedStrategy = feedStrategy,
                    clipMode = clipMode,
                    onClipMode = onClipMode,
                    onConnectTimeout = onConnectTimeout,
                    onFeedStrategy = onFeedStrategy,
                    onGodMode = onGodMode,
                    onScrollMode = onScrollMode,
                    useCommonUIMode = useCommonUIMode,
                    navigateToConsole = navigateToConsole,
                    useCommonUIModeEnable = useCommonUIModeEnable,
                    experimentalMode = experimentalMode,
                    onExperimentalMode = onExperimentalMode,
                    mutedLives = mutedLives,
                    onBannedLive = onBannedLive,
                    autoRefresh = autoRefresh,
                    onAutoRefresh = onAutoRefresh,
                    isSSLVerificationEnabled = isSSLVerification,
                    onSSLVerificationEnabled = onSSLVerification,
                    fullInfoPlayer = fullInfoPlayer,
                    onFullInfoPlayer = onFullInfoPlayer,
                    initialTabTitle = initialDestination,
                    onInitialTabIndex = onInitialDestination,
                    noPictureMode = noPictureMode,
                    onNoPictureMode = onNoPictureMode,
                    silentMode = silentMode,
                    onSilentMode = onSilentMode,
                    cinemaMode = cinemaMode,
                    onCinemaMode = onCinemaMode,
                    importJavaScript = importJavaScript,
                    modifier = modifier
                        .fillMaxWidth()
                        .scrollable(
                            orientation = Orientation.Vertical,
                            state = rememberScrollableState { it }
                        )
                )
            }

            Configuration.ORIENTATION_LANDSCAPE -> {
                LandscapeOrientationContent(
                    version = version,
                    part = part,
                    title = title,
                    url = url,
                    godMode = godMode,
                    clipMode = clipMode,
                    scrollMode = scrollMode,
                    feedStrategy = feedStrategy,
                    connectTimeout = connectTimeout,
                    useCommonUIMode = useCommonUIMode,
                    useCommonUIModeEnable = useCommonUIModeEnable,
                    onFold = { part = it },
                    onTitle = onTitle,
                    onUrl = onUrl,
                    onClipMode = onClipMode,
                    onScrollMode = onScrollMode,
                    onSubscribe = onSubscribe,
                    onFeedStrategy = onFeedStrategy,
                    onGodMode = onGodMode,
                    onConnectTimeout = onConnectTimeout,
                    onUIMode = onUIMode,
                    navigateToConsole = navigateToConsole,
                    experimentalMode = experimentalMode,
                    onExperimentalMode = onExperimentalMode,
                    mutedLives = mutedLives,
                    onBannedLive = onBannedLive,
                    autoRefresh = autoRefresh,
                    onAutoRefresh = onAutoRefresh,
                    isSSLVerificationEnabled = isSSLVerification,
                    onSSLVerificationEnabled = onSSLVerification,
                    fullInfoPlayer = fullInfoPlayer,
                    onFullInfoPlayer = onFullInfoPlayer,
                    initialTabTitle = initialDestination,
                    onInitialTabIndex = onInitialDestination,
                    noPictureMode = noPictureMode,
                    onNoPictureMode = onNoPictureMode,
                    silentMode = silentMode,
                    onSilentMode = onSilentMode,
                    cinemaMode = cinemaMode,
                    onCinemaMode = onCinemaMode,
                    importJavaScript = importJavaScript,
                    modifier = modifier.scrollable(
                        orientation = Orientation.Vertical,
                        state = rememberScrollableState { it }
                    )
                )
            }

            else -> {}
        }
    }
    BackHandler(part != SettingPart.PREFERENCE) {
        part = SettingPart.PREFERENCE
    }
}

@Composable
private fun PortraitOrientationContent(
    version: String,
    part: SettingPart,
    title: String,
    url: String,
    @FeedStrategy feedStrategy: Int,
    godMode: Boolean,
    @ClipMode clipMode: Int,
    @ConnectTimeout connectTimeout: Int,
    useCommonUIMode: Boolean,
    useCommonUIModeEnable: Boolean,
    onGodMode: () -> Unit,
    onClipMode: OnClipMode,
    onConnectTimeout: () -> Unit,
    mutedLives: List<Live>,
    onBannedLive: (Int) -> Unit,
    onFold: (SettingPart) -> Unit,
    onTitle: (String) -> Unit,
    onUrl: (String) -> Unit,
    onSubscribe: () -> Unit,
    onFeedStrategy: OnFeedStrategy,
    navigateToConsole: NavigateToConsole,
    experimentalMode: Boolean,
    onScrollMode: () -> Unit,
    scrollMode: Boolean,
    onExperimentalMode: () -> Unit,
    autoRefresh: Boolean,
    onAutoRefresh: () -> Unit,
    isSSLVerificationEnabled: Boolean,
    onSSLVerificationEnabled: () -> Unit,
    fullInfoPlayer: Boolean,
    onFullInfoPlayer: () -> Unit,
    initialTabTitle: String,
    onInitialTabIndex: () -> Unit,
    noPictureMode: Boolean,
    onNoPictureMode: () -> Unit,
    silentMode: Boolean,
    onSilentMode: () -> Unit,
    cinemaMode: Boolean,
    onCinemaMode: () -> Unit,
    importJavaScript: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Box {
        PreferencesPart(
            version = version,
            feedStrategy = feedStrategy,
            useCommonUIMode = useCommonUIMode,
            useCommonUIModeEnable = useCommonUIModeEnable,
            godMode = godMode,
            clipMode = clipMode,
            connectTimeout = connectTimeout,
            onConnectTimeout = onConnectTimeout,
            onFeedStrategy = onFeedStrategy,
            onClipMode = onClipMode,
            onUIMode = { },
            onGodMode = onGodMode,
            onFeedManagement = {
                onFold(SettingPart.FEED)
            },
            onScriptManagement = {
                onFold(SettingPart.SCRIPT)
            },
            navigateToConsole = navigateToConsole,
            experimentalMode = experimentalMode,
            onExperimentalMode = onExperimentalMode,
            scrollMode = scrollMode,
            onScrollMode = onScrollMode,
            autoRefresh = autoRefresh,
            onAutoRefresh = onAutoRefresh,
            isSSLVerificationEnabled = isSSLVerificationEnabled,
            onSSLVerificationEnabled = onSSLVerificationEnabled,
            fullInfoPlayer = fullInfoPlayer,
            onFullInfoPlayer = onFullInfoPlayer,
            initialTabTitle = initialTabTitle,
            onInitialTabIndex = onInitialTabIndex,
            noPictureMode = noPictureMode,
            onNoPictureMode = onNoPictureMode,
            silentMode = silentMode,
            onSilentMode = onSilentMode,
            cinemaMode = cinemaMode,
            onCinemaMode = onCinemaMode,
            modifier = modifier
        )

        AnimatedVisibility(
            visible = part != SettingPart.PREFERENCE,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            when (part) {
                SettingPart.FEED -> {
                    FeedsPart(
                        title = title,
                        url = url,
                        mutedLives = mutedLives,
                        onBannedLive = onBannedLive,
                        onTitle = onTitle,
                        onUrl = onUrl,
                        onSubscribe = onSubscribe,
                        modifier = modifier.background(LocalTheme.current.background)
                    )
                }

                SettingPart.SCRIPT -> {
                    ScriptsPart(
                        importJavaScript = importJavaScript,
                        modifier = modifier.background(LocalTheme.current.background)
                    )
                }

                else -> {}
            }
        }
    }
}

@Composable
private fun LandscapeOrientationContent(
    version: String,
    part: SettingPart,
    title: String,
    url: String,
    godMode: Boolean,
    @ClipMode clipMode: Int,
    onFold: (SettingPart) -> Unit,
    onTitle: (String) -> Unit,
    onUrl: (String) -> Unit,
    onSubscribe: () -> Unit,
    @FeedStrategy feedStrategy: Int,
    @ConnectTimeout connectTimeout: Int,
    onFeedStrategy: OnFeedStrategy,
    onConnectTimeout: () -> Unit,
    useCommonUIMode: Boolean,
    useCommonUIModeEnable: Boolean,
    scrollMode: Boolean,
    onUIMode: () -> Unit,
    onGodMode: () -> Unit,
    onClipMode: OnClipMode,
    onScrollMode: () -> Unit,
    mutedLives: List<Live>,
    onBannedLive: (Int) -> Unit,
    navigateToConsole: NavigateToConsole,
    experimentalMode: Boolean,
    onExperimentalMode: () -> Unit,
    autoRefresh: Boolean,
    onAutoRefresh: () -> Unit,
    isSSLVerificationEnabled: Boolean,
    onSSLVerificationEnabled: () -> Unit,
    fullInfoPlayer: Boolean,
    onFullInfoPlayer: () -> Unit,
    initialTabTitle: String,
    onInitialTabIndex: () -> Unit,
    noPictureMode: Boolean,
    onNoPictureMode: () -> Unit,
    silentMode: Boolean,
    onSilentMode: () -> Unit,
    cinemaMode: Boolean,
    onCinemaMode: () -> Unit,
    importJavaScript: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing.medium, Alignment.Start),
        modifier = modifier.padding(horizontal = spacing.medium)
    ) {
        PreferencesPart(
            version = version,
            godMode = godMode,
            clipMode = clipMode,
            onClipMode = onClipMode,
            onFeedManagement = { onFold(SettingPart.FEED) },
            onScriptManagement = { onFold(SettingPart.SCRIPT) },
            feedStrategy = feedStrategy,
            connectTimeout = connectTimeout,
            onFeedStrategy = onFeedStrategy,
            onConnectTimeout = onConnectTimeout,
            useCommonUIMode = useCommonUIMode,
            useCommonUIModeEnable = useCommonUIModeEnable,
            onUIMode = onUIMode,
            onGodMode = onGodMode,
            navigateToConsole = navigateToConsole,
            experimentalMode = experimentalMode,
            onExperimentalMode = onExperimentalMode,
            scrollMode = scrollMode,
            onScrollMode = onScrollMode,
            autoRefresh = autoRefresh,
            onAutoRefresh = onAutoRefresh,
            isSSLVerificationEnabled = isSSLVerificationEnabled,
            onSSLVerificationEnabled = onSSLVerificationEnabled,
            fullInfoPlayer = fullInfoPlayer,
            onFullInfoPlayer = onFullInfoPlayer,
            initialTabTitle = initialTabTitle,
            onInitialTabIndex = onInitialTabIndex,
            noPictureMode = noPictureMode,
            onNoPictureMode = onNoPictureMode,
            silentMode = silentMode,
            onSilentMode = onSilentMode,
            cinemaMode = cinemaMode,
            onCinemaMode = onCinemaMode,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        )

        when (part) {
            SettingPart.FEED -> {
                FeedsPart(
                    title = title,
                    url = url,
                    mutedLives = mutedLives,
                    onBannedLive = onBannedLive,
                    onTitle = onTitle,
                    onUrl = onUrl,
                    onSubscribe = onSubscribe,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }

            SettingPart.SCRIPT -> {
                ScriptsPart(
                    importJavaScript = importJavaScript,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }

            else -> {}
        }
    }
}
