package assignment.pokemon.rest.models

import com.google.gson.annotations.SerializedName

class Stat {
    val base_stat = 0
    @SerializedName("stat")
    val name = Name()

    class Name {
        val name = ""
    }
}