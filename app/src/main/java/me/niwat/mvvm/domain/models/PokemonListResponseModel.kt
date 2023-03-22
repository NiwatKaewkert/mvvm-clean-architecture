package me.niwat.mvvm.domain.models

import com.google.gson.annotations.SerializedName

data class PokemonListResponseModel(
    @SerializedName("cards")
    var cards: MutableList<Card> = mutableListOf()
)


data class Card(
    @SerializedName("attacks")
    val attacks: MutableList<Attack> = mutableListOf(),
    @SerializedName("hp")
    val hp: Int = 0,
    @SerializedName("id")
    val id: String = "",
    @SerializedName("imageUrl")
    val imageUrl: String = "",
    @SerializedName("isSelected")
    var isSelected: Boolean = false,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("rarity")
    val rarity: Int = 0,
    @SerializedName("weaknesses")
    val weaknesses: MutableList<Weaknesses> = mutableListOf()
)

data class Attack(
    @SerializedName("cost")
    val cost: MutableList<String> = mutableListOf(),
    @SerializedName("damage")
    val damage: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("text")
    val text: String = ""
)

data class Weaknesses(
    @SerializedName("type")
    val type: String = "",
    @SerializedName("value")
    val value: String = ""
)
