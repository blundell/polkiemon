package com.blundell.polkiemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.blundell.polkiemon.ui.theme.PolkiemonTheme

class MainActivity : ComponentActivity() {

    private val viewModel: ListPokemonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolkiemonTheme {
                when (val state = viewModel.state.collectAsStateWithLifecycle().value) {
                    ListPokemonState.Empty -> EmptyView()
                    is ListPokemonState.Failure -> FailureView(errorMessage = state.errorMessage)
                    ListPokemonState.Loading -> LoadingView()
                    is ListPokemonState.Success -> ListPokemonView(pokemon = state.pokemon)
                }
            }
        }
    }
}

@Composable
fun ListPokemonView(pokemon: List<PokemonListItem>) {
    LazyColumn {
        items(pokemon) {
            PokemonCard(it)
        }
    }
}

@Composable
fun PokemonCard(item: PokemonListItem) {
    Row(
        modifier = Modifier.padding(all = 8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageUrl)
                .error(R.drawable.whos_that_charmander)
                .crossfade(true)
                .build(),
            contentDescription = "A ${item.name}.",
            placeholder = painterResource(id = R.drawable.whos_that_charmander),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.secondary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = item.name,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.headlineSmall,
        )
    }
}

@Composable
fun EmptyView() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Text(text = "Team Rocket have taken all the Pokemon!")
    }
}

@Composable
fun LoadingView() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Loading")
            CircularProgressIndicator()
        }
    }
}

@Composable
fun FailureView(errorMessage: String) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Oh no, we've dropped all our Poke-balls!")
            Text(text = "Technical issue: [${errorMessage}].")
        }
    }
}
