package assignment.pokemon.rest

import assignment.pokemon.rest.models.PokemonStats
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {
    @GET("pokemon/{name}")
    fun pokemonStats(@Path("name") name:String) : Call<PokemonStats>
}