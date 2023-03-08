package com.blundell.polkiemon.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blundell.polkiemon.R
import java.net.URL

@Composable
fun PokemonCircularImage(
    imageUrl: URL,
    contentDescription: String
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl.toString())
            .error(R.drawable.whos_that_charmander)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        placeholder = painterResource(id = R.drawable.whos_that_charmander),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
    )
}
