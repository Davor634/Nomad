package hr.ferit.davormaljkovic.nomad.data

import kotlinx.serialization.Serializable

@Serializable
data class Lodging(
    var name: String? = null,
    var price: Int? = null
)
