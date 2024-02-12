package hr.ferit.davormaljkovic.nomad.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import hr.ferit.davormaljkovic.nomad.data.Day
import hr.ferit.davormaljkovic.nomad.data.Journal
import hr.ferit.davormaljkovic.nomad.data.Trip
import hr.ferit.davormaljkovic.nomad.data.TripShort

class TripRepository(
) {
    lateinit var databaseTrip: DatabaseReference
    lateinit var databaseJournal: DatabaseReference
    lateinit var databaseTripShort: DatabaseReference
    private lateinit var trips: ArrayList<TripShort>


    fun saveTrip(
        trip:Trip
    ){
        databaseTrip = FirebaseDatabase.getInstance().getReference("Trip")
        databaseTripShort = FirebaseDatabase.getInstance().getReference("TripShort")

        val tripId = databaseTrip.push().key!!
        trip.tripId = tripId

        databaseTrip.child(tripId).setValue(trip)
        databaseTripShort.child(tripId).setValue(TripShort(tripId, trip.title, trip.thumbnail, /*trip.date*/))

    }

    fun saveCurrent(
        trip:Trip
    ){
        databaseTrip = FirebaseDatabase.getInstance().getReference("Current")
        val currRef = databaseTrip.child("One")
        val updateCurrent = mapOf(
            "tripId" to trip.tripId.toString()
        )
        currRef.updateChildren(updateCurrent)
        Thread.sleep(100)
    }

    fun saveDay(
        day: Day,
        tripId:String
    ){
        val database = FirebaseDatabase.getInstance().getReference("Trip")
        val dayRef = database.child(tripId).child("days").child(day.dayId.toString())
        val updatedDayValues = mapOf(
            "dayId" to day.dayId,
            "date" to day.date,
            "name" to day.name,
            "description" to day.description
        )
        dayRef.updateChildren(updatedDayValues)
    }

    fun saveJournal(
        journal: Journal
    ){
        databaseJournal = FirebaseDatabase.getInstance().getReference("Journal")
        databaseTrip = FirebaseDatabase.getInstance().getReference("Trip")

        val currRef = databaseTrip.child(journal.tripId!!)
        val updateTrip = mapOf(
            "journal" to true
        )
        currRef.updateChildren(updateTrip)

        databaseJournal.child(journal.tripId!!).setValue(journal)

    }


}