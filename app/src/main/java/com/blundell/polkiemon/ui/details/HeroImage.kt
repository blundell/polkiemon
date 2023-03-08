package com.blundell.polkiemon.ui.details

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blundell.polkiemon.R

@Composable
fun HeroImage(
    imageUrl: String,
    contentDescription: String,
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .error(R.drawable.whos_that_charmander)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        placeholder = painterResource(id = R.drawable.whos_that_charmander),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(240.dp)
            .border(1.5.dp, MaterialTheme.colorScheme.secondary)
    )
}
