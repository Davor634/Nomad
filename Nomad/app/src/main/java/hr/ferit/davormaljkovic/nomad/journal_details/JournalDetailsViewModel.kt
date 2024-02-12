package hr.ferit.davormaljkovic.nomad.journal_details

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
import hr.ferit.davormaljkovic.nomad.data.Journal
import hr.ferit.davormaljkovic.nomad.repository.TripRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class JournalDetailsState {
    object Loading : JournalDetailsState()
    data class Success(val state: Journal?, val images: List<Bitmap>) : JournalDetailsState()
}
class JournalDetailsViewModel(
    private val repository: TripRepository = TripRepository()
) : ViewModel() {

    val state = MutableStateFlow<JournalDetailsState>(JournalDetailsState.Loading)

    fun getJournalDetails(id: String) {
        var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Journal")
        var storageRef = Firebase.storage.reference
        var images: ArrayList<Bitmap> = arrayListOf()
        var journal: Journal?
        val TWE_MEGABYTE: Long = 20 * 1024 * 1024

        val journalListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    journal = snapshot.child(id).getValue<Journal>()

                    val imagesDeferred = mutableListOf<Deferred<Bitmap>>()

                    for (image in journal!!.images!!.withIndex()) {
                        val deferred = CompletableDeferred<Bitmap>()
                        imagesDeferred.add(deferred)

                        storageRef.child("images/${Uri.parse(image.toString()).lastPathSegment!!.removeSuffix(")")}")
                            .getBytes(TWE_MEGABYTE).addOnSuccessListener {
                                val pito = it
                                deferred.complete(BitmapFactory.decodeByteArray(pito, 0, pito.size))
                            }

                    }

                    GlobalScope.launch {
                        val downloadedImages = imagesDeferred.awaitAll()
                        state.value = JournalDetailsState.Success(journal, downloadedImages)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        database.addValueEventListener(journalListener)
    }
}