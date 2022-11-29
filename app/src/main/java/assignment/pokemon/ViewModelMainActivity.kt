package assignment.pokemon

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import assignment.pokemon.rest.ApiInterface
import assignment.pokemon.rest.models.PokemonStats
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ViewModelMainActivity : ViewModel() {
    val userData = UserData("Default_User", 57.49f)
    var searchText by mutableStateOf("")
    var pokemon : MutableState<PokemonStats?> = mutableStateOf(null)
    var searchStatus by mutableStateOf(SearchStatus.IDLE)
    var pokemonImage : MutableState<ImageBitmap?> = mutableStateOf(null)


    val apiInterface : ApiInterface
    var searchJob : Job? = null

    init {
        apiInterface = Retrofit.Builder().run {
            baseUrl("https://pokeapi.co/api/v2/")
            addConverterFactory(GsonConverterFactory.create())
            build()
        }.create()
    }

    /** pulls the pokemon data via an API call and also gets the pokemon Image on the same coroutine*/
    private fun search(pokemonName: String, delayed:Boolean) {
        searchJob?.cancel()
        searchJob = CoroutineScope(Dispatchers.IO).launch {
            if (delayed) {
                delay(2000)
                if (!isActive) {
                    return@launch
                }
            }

            //Sets the Loading status for the UI and pulls the pokemon data
            CoroutineScope(Dispatchers.Main).launch {
                searchStatus = SearchStatus.LOADING
            }
            val call = apiInterface.pokemonStats(pokemonName.lowercase())
            var pokemonStats : PokemonStats?
            var responseCode : Int
            try {
                val pokemonResponse = call.execute()
                pokemonStats = pokemonResponse.body()
                responseCode = pokemonResponse.code()
            } catch (error : Throwable) {
                Log.d("atf", error.message!!)
                pokemonStats = null
                responseCode = -1
            }
            if (!isActive) {
                CoroutineScope(Dispatchers.Main).launch {
                    searchStatus = SearchStatus.IDLE
                }
                return@launch
            }

            //Sending the result to the UI
            CoroutineScope(Dispatchers.Main).launch {
                if (responseCode == 200) {
                    pokemon.value = pokemonStats
                    searchStatus = SearchStatus.IDLE
                } else if (responseCode == 404) {
                    pokemon.value = null
                    searchStatus = SearchStatus.NOT_FOUND
                } else {
                    pokemon.value = null
                    searchStatus = SearchStatus.OTHER_ERROR
                }
            }

            //Loading Pokemon Image
            if (responseCode == 200) {
                val imageUrl = pokemonStats?.getImageUrl()
                if (imageUrl != null) {
                    var imageBitmap : ImageBitmap? = null

                    val httpClient = OkHttpClient.Builder().build()
                    val imageRequest = Request.Builder().url(imageUrl).build()
                    val imageResponse = httpClient.newCall(imageRequest).execute()
                    if (imageResponse.code() == 200) {
                        val imageStream = imageResponse.body()?.byteStream()
                        if (imageStream != null) {
                            imageBitmap = BitmapFactory.decodeStream(imageStream).asImageBitmap()
                        }
                    }
                    imageResponse.close()

                    if (!isActive) {
                        return@launch
                    }
                    //Sending the image to UI
                    CoroutineScope(Dispatchers.Main).launch {
                        pokemonImage.value = imageBitmap
                    }
                }
            }
        }
    }

    fun setDelayedSearch(pokemonName:String) {
        searchText = pokemonName
        searchStatus = SearchStatus.IDLE
        pokemon.value = null
        pokemonImage.value = null

        if (searchText == "") {
            searchJob?.cancel()
            pokemon.value = null
            return
        }

        search(searchText, true)
    }

    fun retrySearch() {
        search(searchText, false)
    }

    fun purchase() {
        userData.availableFunds -= pokemon.value?.cost ?: 0f
    }

    class UserData(nickname: String, availableFunds:Float) {
        val nickname by mutableStateOf(nickname)
        var availableFunds by mutableStateOf(availableFunds)
    }

    enum class SearchStatus {
        NOT_FOUND, OTHER_ERROR, LOADING, IDLE
    }
}