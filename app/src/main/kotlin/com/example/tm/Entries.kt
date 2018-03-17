package com.example.tm

import com.google.gson.annotations.SerializedName

data class Sys(val type: String, val linkType: String, val id: String)

data class Entries(val items: Array<Item>, val includes: Includes) {
    infix fun getAssetFor(link: Link) : Entry? {
        return includes.entries.find { it.id == link.sys.id }
    }
}

data class Item(val fields: Field)

data class Field(val title: String, @SerializedName("portraitAsset") val portraitAssetLink: Link,
                 @SerializedName("landscapeAsset") val landscapeAssetLink: Link,
                 @SerializedName("recipeJSON") val recipeJson: Array<Recipe>)

data class Recipe(val ingredients: Array<String>)

data class Link(val sys: Sys)

data class Includes(@SerializedName("Entry") val entries: Array<Entry>)

data class Entry(@SerializedName("_id") val id: String, val fields: Map<String, Any>)
