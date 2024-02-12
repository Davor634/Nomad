package hr.ferit.davormaljkovic.nomad.data

data class Journal(
    val tripId: String?=null,
    val title: String?=null,
    val text:String?=null,
    val images:ArrayList<String>?=null

)
