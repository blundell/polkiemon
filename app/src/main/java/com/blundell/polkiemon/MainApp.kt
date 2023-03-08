package com.blundell.polkiemon.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blundell.polkiemon.ui.details.DetailsScreen
import com.blundell.polkiemon.ui.list.ListPokemonScreen
import com.blundell.polkiemon.ui.theme.PolkiemonTheme

@Composable
fun MainApp() {
    val navController = rememberNavController()
    PolkiemonTheme {
        NavHost(navController = navController, startDestination = "list") {
            composable("list") {
                ListPokemonScreen(viewModel()) { name ->
                    navController.navigate("details/$name")
                }
            }
            composable(
                route = "details/{name}",
                arguments = listOf(navArgument("name") {})
            ) {
                DetailsScreen(viewModel())
            }
        }
    }
}
