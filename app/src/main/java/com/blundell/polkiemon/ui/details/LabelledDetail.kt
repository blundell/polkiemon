package com.blundell.polkiemon.ui.details

import androidx.compose.animation.*
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RowScope.LabelLabel(label: String, weight: Float) {
    Text(
        text = label,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.weight(weight)
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun RowScope.LabelDetail(detail: String, weight: Float) {
    AnimatedContent(
        targetState = detail,
        transitionSpec = {
            EnterTransition.None with ExitTransition.None
        },
        modifier = Modifier.weight(weight),
    ) {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.animateEnterExit(
                enter = scaleIn(),
                exit = scaleOut()
            )
        )
    }
}
