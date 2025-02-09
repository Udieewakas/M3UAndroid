package com.m3u.material.components.mask

import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.m3u.material.components.IconButton
import com.m3u.material.ktx.isTelevision
import com.m3u.material.ktx.thenIf

@Composable
fun MaskButton(
    state: MaskState,
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
    enabled: Boolean = true
) {
    val tv = isTelevision()
    val tooltipState = rememberTooltipState()

    TooltipBox(
        state = tooltipState,
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text(text = contentDescription.uppercase())
            }
        }
    ) {
        IconButton(
            icon = icon,
            enabled = enabled,
            contentDescription = contentDescription,
            onClick = {
                state.wake()
                onClick()
            },
            modifier = modifier.thenIf(tv) {
                Modifier.onFocusEvent {
                    if (it.isFocused) {
                        state.wake()
                    }
                }
            },
            tint = tint
        )
    }
}
