package hr.ferit.davormaljkovic.nomad.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.storage
import hr.ferit.davormaljkovic.nomad.data.Current
import hr.ferit.davormaljkovic.nomad.data.Journal
import hr.ferit.davormaljkovic.nomad.data.Trip
import hr.ferit.davormaljkovic.nomad.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.internal.wait

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val state: ArrayList<Trip>, val images: ArrayList<Bitmap>, val current:ArrayList<Current>, val journals:ArrayList<Journal>, val journalImages:ArrayList<Bitmap>) : HomeState()
}

class HomeViewModel(
    private val repository: TripRepository = TripRepository()
) : ViewModel() {
    init {
        viewModelScope.launch {
            getTrips()
        }
    }

    val state = MutableStateFlow<HomeState>(HomeState.Loading)

    private fun getTrips() {
        var trips: ArrayList<Trip> = arrayListOf()
        var currentTrip: ArrayList<Current> = arrayListOf()
        var journals:ArrayList<Journal> = arrayListOf()

        var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Trip")
        var currentDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Current")
        var journalDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Journal")

        var storageRef = Firebase.storage.reference
        var images: ArrayList<Bitmap> = arrayListOf()
        var journalImages:ArrayList<Bitmap> = arrayListOf()
        val THREE_MEGABYTE: Long = 3 * 1024 * 1024

        val currentDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (currentSnap in snapshot.children) {
                        val currentData = currentSnap.getValue<Current>()
                        currentTrip.add(currentData!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        val journalDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (journalSnap in snapshot.children) {
                        val journalData = journalSnap.getValue<Journal>()
                        val name = Uri.parse(journalData!!.images!!.first()).lastPathSegment!!.removeSuffix(")")
                        journals.add(journalData)
                        storageRef.child("images/${name}").getBytes(THREE_MEGABYTE).addOnSuccessListener {
                            var temp = it
                            journalImages.add(BitmapFactory.decodeByteArray(temp,0,temp.size))

                        }
                    }
                    Thread.sleep(1000)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        val tripListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(tripSnap in snapshot.children){
                        val trip = tripSnap.getValue<Trip>()
                        val name = Uri.parse(trip!!.thumbnail).lastPathSegment
                        trips.add(trip!!)
                        storageRef.child("images/${name}").getBytes(THREE_MEGABYTE).addOnSuccessListener {
                            var temp = it
                            images.add(BitmapFactory.decodeByteArray(temp, 0, temp.size))
                            state.value = HomeState.Success(trips, images,currentTrip, journals, journalImages)
                        }

                        Thread.sleep(1000)

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }


        currentDatabase.addValueEventListener(currentDataListener)
        database.addValueEventListener(tripListener)
        journalDatabase.addValueEventListener(journalDataListener)

    }

}




