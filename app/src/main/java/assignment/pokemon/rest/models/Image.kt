package assignment.pokemon.rest.models

import com.google.gson.annotations.SerializedName

class Image {
    @SerializedName("other")
    val data1 : Data1? = null

    class Data1 {
        @SerializedName("official-artwork")
        val data2 : Data2? = null
    }

    class Data2 {
        val front_default : String? = null
    }
}