package hr.ferit.davormaljkovic.nomad.data

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class TripDate(
    var startDate: String? = null,
    var endDate: String? = null
)
