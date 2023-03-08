package com.blundell.polkiemon.ui.details

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition
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
            .transitionFactory { a, b -> CrossfadeTransition(a, b, 2500, true) }
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(240.dp)
            .border(1.5.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(2))
    )
}
