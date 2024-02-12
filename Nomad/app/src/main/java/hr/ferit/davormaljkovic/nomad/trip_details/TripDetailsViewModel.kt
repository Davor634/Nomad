package hr.ferit.davormaljkovic.nomad.trip_details

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.storage.storage
import hr.ferit.davormaljkovic.nomad.data.Trip
import hr.ferit.davormaljkovic.nomad.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow

sealed class DetailsState {
    object Loading : DetailsState()
    data class Success(val state: Trip?, val image: Bitmap) : DetailsState()
}
class DetailsViewModel(
    private val repository: TripRepository = TripRepository()
) : ViewModel() {

    val state = MutableStateFlow<DetailsState>(DetailsState.Loading)

    fun getTripDetails(id: String) {
        var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Trip")
        var storageRef = Firebase.storage.reference
        var image: Bitmap
        var trip: Trip?
        val FIVE_MEGABYTE: Long = 5 * 1024 * 1024

        val tripListener = object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    trip = snapshot.child(id).getValue<Trip>()
                    storageRef.child("images/${Uri.parse(trip!!.thumbnail).lastPathSegment}").getBytes(FIVE_MEGABYTE).addOnSuccessListener {
                        var pito = it
                        image = BitmapFactory.decodeByteArray(pito, 0, pito.size)
                        state.value = DetailsState.Success(trip, image)

                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        database.addValueEventListener(tripListener)
    }
}