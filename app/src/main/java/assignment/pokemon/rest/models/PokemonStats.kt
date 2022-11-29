package assignment.pokemon.rest.models

import com.google.gson.annotations.SerializedName

class PokemonStats {
    val id = 0
    val name = ""
    val base_experience = 0
    val weight = 0
    val height = 0
    val stats = listOf<Stat>()
    val types = listOf<Type>()
    val abilities = listOf<Ability>()
    val moves = listOf<Move>()
    @SerializedName("sprites")
    val image : Image? = null
    val cost : Float get() { return base_experience / 100f * 6 }

    fun getImageUrl() : String? {
        return image?.data1?.data2?.front_default
    }

    fun getTypes() : String {
        val stringBuilder = StringBuilder()
        for (index in types.indices) {
            stringBuilder.append(types[index].type.name)
            if (index != types.lastIndex) {
                stringBuilder.append(", ")
            }
        }
        return stringBuilder.toString()
    }

    fun getAbilities() : String {
        val stringBuilder = StringBuilder()
        for (i in abilities.indices) {
            val ability = abilities[i]
            stringBuilder.append(ability.ability.name)
            if (ability.is_hidden) {
                stringBuilder.append(" (hidden)")
            }
            if (i != abilities.lastIndex) {
                stringBuilder.append(", ")
            }
        }
        return stringBuilder.toString()
    }

    fun getMoves() : String {
        val stringBuilder = StringBuilder()
        for (i in moves.indices) {
            val move = moves[i]
            stringBuilder.append(move.move.name)
            if (i != moves.lastIndex) {
                stringBuilder.append(", ")
            }
        }
        return stringBuilder.toString()
    }
}