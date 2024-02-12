package hr.ferit.davormaljkovic.nomad.data

import kotlinx.serialization.Serializable

@Serializable
data class Transportation(
    var durationHours:Int? = null,
    var durationMinutes : Int? = null,
    var price:Int? = null,
    var type:String? = null

)
