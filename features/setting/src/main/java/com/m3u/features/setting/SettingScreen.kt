package com.m3u.features.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.m3u.core.util.toast
import com.m3u.ui.components.basic.M3UColumn
import com.m3u.ui.components.basic.M3UTextButton
import com.m3u.ui.components.basic.M3UTextField
import com.m3u.ui.local.LocalSpacing
import com.m3u.ui.local.LocalTheme
import com.m3u.ui.util.EventEffect

@Composable
internal fun SettingRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val state by viewModel.readable.collectAsStateWithLifecycle()
    val context = LocalContext.current

    EventEffect(state.message) {
        context.toast(it)
    }

    SettingScreen(
        subscribeEnable = !state.adding,
        title = state.title,
        url = state.url,
        onTitle = { viewModel.onEvent(SettingEvent.OnTitle(it)) },
        onUrl = { viewModel.onEvent(SettingEvent.OnUrl(it)) },
        onSubscribe = { viewModel.onEvent(SettingEvent.SubscribeUrl) },
        version = state.version,
        modifier = modifier
    )
}

@Composable
private fun SettingScreen(
    subscribeEnable: Boolean,
    version: String,
    title: String,
    url: String,
    onTitle: (String) -> Unit,
    onUrl: (String) -> Unit,
    onSubscribe: () -> Unit,
    modifier: Modifier = Modifier
) {
    M3UColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("features:setting"),
        verticalArrangement = Arrangement.spacedBy(LocalSpacing.current.small)
    ) {
        val focusRequester = remember { FocusRequester() }
        M3UTextField(
            text = title,
            placeholder = stringResource(R.string.placeholder_title),
            onValueChange = onTitle,
            keyboardActions = KeyboardActions(
                onNext = {
                    focusRequester.requestFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        M3UTextField(
            text = url,
            placeholder = stringResource(R.string.placeholder_url),
            onValueChange = onUrl,
            keyboardActions = KeyboardActions(
                onDone = {
                    onSubscribe()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
        val buttonTextResId = if (subscribeEnable) R.string.label_subscribe
        else R.string.label_subscribing
        M3UTextButton(
            enabled = subscribeEnable,
            text = stringResource(buttonTextResId),
            onClick = { onSubscribe() },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            val text = stringResource(R.string.label_app_version, version)
            Text(
                text = text,
                style = MaterialTheme.typography.subtitle2,
                textDecoration = TextDecoration.Underline,
                color = LocalTheme.current.primary
            )
        }
    }
}