package hr.ferit.davormaljkovic.nomad.data

enum class TransportationType(val str:String) {
    EMPTY(""),
    PLANE("Plane"),
    CAR("Car"),
    BUS("Bus"),
    BOAT("Boat"),
    TRAIN("Train"),
    BIKE("Bike"),
    FOOT("Foot")
}