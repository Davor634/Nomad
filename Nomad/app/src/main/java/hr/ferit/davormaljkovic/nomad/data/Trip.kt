package hr.ferit.davormaljkovic.nomad.data

import android.net.Uri
import androidx.annotation.DrawableRes
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Trip(
    var tripId: String? = null,
    val title: String? = null,
    val location: String? = null,
    val days:List<Day> = mutableListOf(),
    val thumbnail: String? = null,
    val transportation:Transportation? = null,
    val lodging: Lodging? = null,
    val date: TripDate? = null,
    val journal:Boolean?=null,
)
