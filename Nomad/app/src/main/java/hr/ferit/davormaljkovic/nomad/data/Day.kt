package hr.ferit.davormaljkovic.nomad.data

import kotlinx.serialization.Serializable
import java.lang.reflect.Constructor
import java.time.LocalDate
import java.util.Date

@Serializable
data class Day(
    val dayId:Int,
    val date: String,
    val name: String,
    val description:String,


){
    constructor(): this(0,"","","")
}
