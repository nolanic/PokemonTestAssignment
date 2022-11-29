package assignment.pokemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import assignment.pokemon.rest.models.PokemonStats
import assignment.pokemon.ui.theme.Colors
import assignment.pokemon.ui.theme.PokemonTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = viewModels<ViewModelMainActivity>().value

        setContent {
            PokemonTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val onSearchTextChanged = { newText:String ->
                        viewModel.setDelayedSearch(newText)
                    }

                    Content(viewModel.userData.nickname, viewModel.userData.availableFunds, viewModel.searchText,
                            viewModel.searchStatus, viewModel.pokemon.value, viewModel.pokemonImage.value,
                            onSearchTextChanged, {viewModel.retrySearch()}, {viewModel.purchase()})
                }
            }
        }
    }
}

@Composable
private fun Content(userNickname: String, userFunds : Float, searchText:String,
                    searchStatus: ViewModelMainActivity.SearchStatus, pokemonStats: PokemonStats?, image: ImageBitmap?,
                    onSearchTextChanged : (newText:String) -> Unit, onRetrySearch:()->Unit, onPurchase: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(1f)) {
        Box(modifier = Modifier.fillMaxWidth(1f).background(Colors.green).padding(15.dp)) {
            Column {
                Text(text = "Logged in as:", fontSize = 10.sp)
                Text(text = userNickname, fontSize = 20.sp)
            }

            Text(text = "\$${"%.2f".format(userFunds)}", modifier = Modifier.align(Alignment.BottomEnd))
        }

        TextField(value = searchText,
                  modifier = Modifier.fillMaxWidth(1f),
                  onValueChange = {onSearchTextChanged(it)},
                  label = {
                       Text(text = "Search pokemon by name")
                  }
            )

        Box(modifier = Modifier.fillMaxSize(1f).padding(10.dp)) {
            if (pokemonStats != null) {
                LazyColumn(modifier = Modifier.fillMaxSize(1f)) {
                    item {
                        PokemonStats(pokemonStats, image)
                        PurchaseOptions(pokemonStats.cost, userFunds, onPurchase)
                    }
                }
            }
            if (searchStatus != ViewModelMainActivity.SearchStatus.IDLE) {
                SearchStatus(searchStatus, searchText, Modifier.align(Alignment.Center), onRetrySearch)
            }
        }
    }
}

@Composable
private fun PokemonStats(pokemonStats: PokemonStats, image:ImageBitmap?) {
    Column(modifier = Modifier.fillMaxWidth(1f).padding(10.dp)) {
        if (image != null) {
            Image(bitmap = image,
                  contentDescription = null,
                  modifier = Modifier.width(200.dp).height(200.dp).align(Alignment.CenterHorizontally))
        }

        Text(text = "Base Experience: ${pokemonStats.base_experience}   weight: ${pokemonStats.weight}   height: ${pokemonStats.height}")
        Spacer(modifier = Modifier.fillMaxWidth(1f).height(2.dp).background(Color.Gray))
        Text(text = "Stats:")
        for (stat in pokemonStats.stats) {
            Text(text = "${stat.name.name}: ${stat.base_stat}")
        }
        Spacer(modifier = Modifier.fillMaxWidth(1f).height(2.dp).background(Color.Gray))
        Text(text = "Types: ${pokemonStats.getTypes()}")
        Spacer(modifier = Modifier.fillMaxWidth(1f).height(2.dp).background(Color.Gray))
        Text(text = "Abilities: ${pokemonStats.getAbilities()}")
        Spacer(modifier = Modifier.fillMaxWidth(1f).height(2.dp).background(Color.Gray))
        Text(text = "Moves: ${pokemonStats.getMoves()}")
        Spacer(modifier = Modifier.fillMaxWidth(1f).height(2.dp).background(Colors.Purple500))
    }
}

@Composable
private fun PurchaseOptions(cost:Float, funds:Float, onPurchase:()->Unit) {
    Box(modifier = Modifier.fillMaxWidth(1f),
        contentAlignment = Alignment.Center) {
        if (cost > funds) {
            Text(text = "Not enough money, ${"%.2f".format(cost)}\$",
                 color = Color.White,
                 fontSize = 15.sp,
                 modifier = Modifier.background(Color.Gray, RoundedCornerShape(10.dp)).padding(10.dp))
        } else {
            Text(text = "Buy for ${"%.2f".format(cost)}$",
                 fontSize = 30.sp,
                 color = Color.White,
                 modifier = Modifier.background(Colors.blue, CircleShape).padding(15.dp, 5.dp, 15.dp, 5.dp).clickable{ onPurchase() })
        }
    }
}

@Composable
private fun SearchStatus(searchStatus:ViewModelMainActivity.SearchStatus, searchText:String, modifier: Modifier, onRetry:()->Unit) {
    Box(modifier = modifier) {
        when (searchStatus) {
            ViewModelMainActivity.SearchStatus.LOADING ->
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp).align(Alignment.Center),
                    color = Colors.Purple200)
            ViewModelMainActivity.SearchStatus.NOT_FOUND ->
                Text(text = "Pokemon with name \"$searchText\"\n was not found",
                     textAlign = TextAlign.Center,
                     modifier = Modifier.align(Alignment.Center))
            ViewModelMainActivity.SearchStatus.OTHER_ERROR ->
                Column(modifier = Modifier.align(Alignment.Center),
                       horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Some error occurred while pulling the data.\nCheck your internet connection and try again",
                         textAlign = TextAlign.Center,
                         modifier = Modifier.padding(bottom = 10.dp))
                    Text(text = "Retry",
                         fontSize = 30.sp,
                         color = Color.White,
                         modifier = Modifier.background(Colors.green, CircleShape).padding(15.dp, 5.dp, 15.dp, 5.dp).clickable { onRetry() })
                }
        }
    }
}

@Preview
@Composable
private fun ContentPreview() {
    Content("Some user", 35.76f, "Chary", ViewModelMainActivity.SearchStatus.IDLE, PokemonStats(), null, {}, {}, {})
}