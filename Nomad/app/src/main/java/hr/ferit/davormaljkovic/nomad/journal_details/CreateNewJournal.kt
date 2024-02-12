package hr.ferit.davormaljkovic.nomad.journal_details


import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import hr.ferit.davormaljkovic.nomad.data.Journal
import hr.ferit.davormaljkovic.nomad.data.Trip
import hr.ferit.davormaljkovic.nomad.repository.TripRepository

class NewJournalActivity : AppCompatActivity() {

    private lateinit var titleState: MutableState<String>
    private lateinit var textState: MutableState<String>
    private lateinit var imagesState: MutableState<List<Uri>>
    private lateinit var thumbnail:MutableState<String>

    var storage = Firebase.storage
    var storageRef = storage.reference


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun JournalScreen(
        navController: NavController,
        trip: Trip
    ) {
        titleState = remember { mutableStateOf("") }
        textState = remember { mutableStateOf("") }
        thumbnail = remember { mutableStateOf("")}
        imagesState = remember { mutableStateOf(emptyList()) }
        var imageUris by remember {
            mutableStateOf<List<Uri>>(emptyList())
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("New Journal") },
                    navigationIcon = {

                    },
                    actions = {
                        IconButton(onClick = {
                            var images:ArrayList<String> = arrayListOf()
                            for (uri in imageUris){
                                var file = uri
                                val tripRef = storageRef.child("images/${file.lastPathSegment}")
                                var uploadTask = tripRef.putFile(file)
                                images += file.toString()

                            }
                            if(titleState.toString() != "" && textState.toString() != ""){
                                TripRepository().saveJournal(Journal(trip.tripId, titleState.value, textState.value, images))
                                navController.navigate("homePage")
                            }

                        }) {
                            Icon(Icons.Filled.Done, contentDescription = null)
                        }
                    }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(50.dp))
                    Text(
                        text = trip.title.toString(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 30.sp

                    )
                    Text(
                        text = trip.location.toString(),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp

                    )
                    Text(
                        text = "${trip.date!!.startDate.toString()}-${trip.date!!.endDate.toString()}",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 15.sp

                    )
                    OutlinedTextField(
                        value = titleState.value,
                        onValueChange = { titleState.value = it },
                        label = { Text("Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = textState.value,
                        onValueChange = { textState.value = it },
                        label = { Text("Text") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .padding(bottom = 16.dp)
                    )


                    LazyColumn {
                        items(imagesState.value) { imageUri ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(bottom = 8.dp)
                            )
                        }

                        item {
                            imageUris = tripThumbnail()
                        }
                    }

                }
            }
        )
    }

    @Composable
    fun tripThumbnail() : List<Uri> {
        var storage = Firebase.storage
        var storageRef = storage.reference
        var imageUris by remember {
            mutableStateOf<List<Uri>>(emptyList())
        }

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
            onResult = { uris ->
                uris?.let {
                    imageUris = it
                    it.forEach { uri ->

                    }
                }
            }
        )

        Column(
            modifier = Modifier.offset(
                y = 20.dp
            )
        ) {
            imageUris.forEach { imageUri ->
                Image(
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RectangleShape)
                        .width(200.dp)
                        .height(200.dp)
                )
            }

            Button(
                onClick = {
                    galleryLauncher.launch("image/*")
                }
            ) {
                Text(
                    text = "Pick images",
                    color = Color.White,
                )
            }
            Spacer(modifier = Modifier.height(50.dp))
        }

        return imageUris
    }


}
