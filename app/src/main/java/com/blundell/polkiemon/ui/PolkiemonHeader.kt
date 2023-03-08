package com.blundell.polkiemon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PolkiemonHeader(
    includeRefresh: Boolean = false,
    onRefresh: (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface
            )
            .padding(6.dp)
    ) {
        Text(
            text = "Polkiemon",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium,
        )
        if (includeRefresh) {
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onRefresh!!
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_revert),
                    contentDescription = "refresh"
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewHeader() {
    PolkiemonHeader()
}
