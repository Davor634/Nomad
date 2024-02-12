package hr.ferit.davormaljkovic.nomad.trip_list

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import hr.ferit.davormaljkovic.nomad.Routes
import hr.ferit.davormaljkovic.nomad.data.Trip
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun TripListScreen(
    navController: NavController,
    trips: List<Trip>) {
    LazyColumn {
        item{ Text(
            text = "Pick a Trip\n and create a Journal",
            modifier = Modifier.fillMaxWidth()
                .padding(top=15.dp),
            fontSize = 30.sp,
            textAlign = TextAlign.Center
        )}
        for(trip in trips) {
            if(trip.journal == false){
                item{
                    TripListItem(navController ,trip = trip)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun TripListItem(
    navController: NavController,
    trip: Trip
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Routes.getTripDetailsPath(trip.tripId.toString())) }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF86535A))
                .padding(16.dp)
                .clickable {
                    navController.navigate(
                        "${Routes.SCREEN_JOURNAL_CREATOR}/${
                            Uri.encode(
                                Json.encodeToString(
                                    trip
                                )
                            )
                        }"
                    )
                }
        ) {
            Text(text = trip.title ?: "No Title", fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = trip.location ?: "No Location", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}