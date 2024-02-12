package hr.ferit.davormaljkovic.nomad.home

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.davormaljkovic.nomad.R
import hr.ferit.davormaljkovic.nomad.Routes
import hr.ferit.davormaljkovic.nomad.Routes.getJournalDetailsPath
import hr.ferit.davormaljkovic.nomad.Routes.getTripCreatorPath
import hr.ferit.davormaljkovic.nomad.Routes.getTripDetailsPath
import hr.ferit.davormaljkovic.nomad.data.Current
import hr.ferit.davormaljkovic.nomad.data.Journal
import hr.ferit.davormaljkovic.nomad.data.Trip
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.P)
@Composable fun HomeScreen(
    navController: NavController,
) {
    val viewModel: HomeViewModel = viewModel()
    val state = viewModel.state.collectAsState()

    when(state.value) {
        is HomeState.Loading -> Loading()
        is HomeState.Success -> HomeScreenContent(
            navController = navController,
            state = (state.value as HomeState.Success).state,
            images = (state.value as HomeState.Success).images,
            current = (state.value as HomeState.Success).current,
            journals = (state.value as HomeState.Success).journals,
            journalImages = (state.value as HomeState.Success).journalImages
        )
    }

}

@Composable
fun Loading() {
    
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun HomeScreenContent(
    navController: NavController,
    state: ArrayList<Trip>,
    images: ArrayList<Bitmap>,
    current:ArrayList<Current>,
    journals:ArrayList<Journal>,
    journalImages:ArrayList<Bitmap>
) {

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F5F5))
            .verticalScroll(rememberScrollState(), enabled = true)
    ) {
        ScreenTitle(title = "Nomad")
        for ((i,trip) in state.withIndex()){
            if(trip.tripId.toString() == current.first().tripId.toString()){
                CurrentTrip(navController=navController, trip = trip, image=images[i])
            }
        }

        NavBar(navController = navController, "Trips", state)
        TripSet(state = state, images = images, navController)
        Spacer(modifier = Modifier.height(30.dp))
        NavBar(navController = navController, "Journals", state)
        JournalSet(navController = navController, state = journals, images = journalImages)


    }
}

@Composable fun ScreenTitle(
    title: String
) {
    Box(

        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .height(90.dp)
            .offset(
                y = -20.dp
            )
            .background(color = Color(0xFF386D65))

    ) {
        Text(
            text = title, style = TextStyle(
                color = Color(0xfff6f5f5),
                textAlign = TextAlign.Center,
                lineHeight = 0.36.em,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.01).sp
            ), modifier = Modifier.padding(horizontal = 15.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentTrip(
    navController:NavController,
    trip: Trip?,
    image: Bitmap,
){
    Box(

        contentAlignment = Alignment.TopStart,
        modifier = Modifier
            .fillMaxWidth()
            .offset(
                y = -20.dp
            )
            .background(Color(0xFFF6F5F5))

    ) {

        TripHighlight(trip,image)

        for((index,day) in trip!!.days.withIndex()){
            if(day.date == LocalDate.now().toString()){
                Text(
                    text = "Day ${index}: ${day.name}",
                    color = Color(0xff6d3840),
                    lineHeight = 1.1.em,
                    fontWeight = FontWeight(450),
                    textAlign = TextAlign.Start,
                    style = TextStyle(
                        fontSize = 14.sp,
                        letterSpacing = (-0.01).sp
                    ),
                    modifier = Modifier
                        .align(alignment = Alignment.BottomCenter)
                        .offset(
                            x = 10.dp,
                            y = -40.dp
                        )
                        .fillMaxWidth()
                        .requiredHeight(height = 17.dp)
                )
            }
        }


        LazyRow(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(
                    y = -27.dp
                )
        ){

            for(day in trip!!.days){
                item{ Spacer(modifier = Modifier.width(2.dp))}
                if(day.date == LocalDate.now().toString()){
                    item{
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xFF000000),
                                    shape = RoundedCornerShape(25.dp)
                                )
                                .padding(0.5.dp)
                                .width(65.dp)
                                .height(4.dp)
                                .background(
                                    color = Color(0xff6d3840),
                                    shape = RoundedCornerShape(25.dp)
                                )
                        )
                    }
                }
                else{
                    item{
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 0.5.dp,
                                    color = Color(0xFF000000),
                                    shape = RoundedCornerShape(25.dp)
                                )
                                .padding(0.5.dp)
                                .width(65.dp)
                                .height(4.dp)
                                .background(color = Color.White, shape = RoundedCornerShape(25.dp))
                        )
                    }
                }
            }

        }

        Text(
            text = "See Details",
            color = Color(0xff6d3840),
            textAlign = TextAlign.Center,
            lineHeight = 1.28.em,
            style = TextStyle(
                fontSize = 12.sp,
                letterSpacing = (-0.01).sp
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(
                    y = 230.dp
                )
                .fillMaxWidth()
                .clickable { navController.navigate(getTripDetailsPath(trip.tripId.toString())) }
        )
    }
}

@Composable
fun TripHighlight(
    trip: Trip?,
    image: Bitmap
) {
    Image(
        bitmap = image.asImageBitmap(),
        contentDescription = "",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .height(height = 250.dp)
            .width(width = 800.dp)
            .border(border = BorderStroke(4.dp, Color(0xfff6f5f5)))
    )

    Image(
        painter = painterResource(id = R.drawable.rectangle99),
        contentDescription = "linear gradient",
        modifier = Modifier
            .fillMaxWidth()
            .requiredWidth(width = 400.dp)
            .requiredHeight(height = 250.dp)
    )
    Text(
        text = trip!!.title.toString(),
        color = Color.White,
        textAlign = TextAlign.Start,
        lineHeight = 0.51.em,
        style = TextStyle(
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.01).sp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .offset(
                y = 10.dp,
                x = 10.dp
            )
            .requiredHeight(height = 54.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(
                x = 10.dp,
                y = 40.dp
            )
    ){
        Image(
            painter = painterResource(id = R.drawable.locationpin3navigationmapmapspingpslocation),
            contentDescription = "location-pin-3--navigation-map-maps-pin-gps-location",
            modifier = Modifier
                .requiredSize(size = 15.dp)
        )

        Text(
            text = trip.location.toString(),
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 1.03.em,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.01).sp
            ),
            modifier = Modifier

        )
    }
}


@Composable
fun NavBar(
    navController:NavController,
    type:String,
    state: ArrayList<Trip>
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .offset(
                y = -20.dp
            )
            .background(Color(0xFFF6F5F5))
    ) {

        Text(
            text = type,
            color = Color(0xff6d3840),
            textAlign = TextAlign.Center,
            lineHeight = 0.6.em,
            style = TextStyle(
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.01).sp
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(
                    y = 10.dp
                )
                .requiredWidth(width = 120.dp)
                .requiredHeight(height = 56.dp)
        )
        Text(
            text = "Create new",
            color = Color(0xff6d3840),
            lineHeight = 1.5.em,
            style = TextStyle(
                fontSize = 12.sp,
                letterSpacing = (-0.01).sp
            ),
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .padding(end = 10.dp)
                .requiredWidth(width = 80.dp)
                .clickable {
                    if (type == "Journals") {
                        navController.navigate(
                            "${Routes.SCREEN_TRIP_LIST}/${
                                Uri.encode(
                                    Json.encodeToString(
                                        state
                                    )
                                )
                            }"
                        )
                    } else {
                        navController.navigate(getTripCreatorPath())
                    }
                }
        )

    }
}


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TripSet(
    state: ArrayList<Trip>,
    images: ArrayList<Bitmap>,
    navController: NavController
) {

    var title: String


    LazyRow(
        modifier = Modifier.background(Color(0xFFF6F5F5))
    ){
        for(i in 0 until state.size){
            item {
                title = state[i].title.toString()
                TripCard(state[i].tripId, title, images[i], navController)
            }
        }
    }

}

@Composable
fun JournalSet(
    navController: NavController,
    state: ArrayList<Journal>,
    images: ArrayList<Bitmap>
){
    LazyRow(
        modifier = Modifier.background(Color(0xFFF6F5F5))
    ){
        for ((index, journal) in state.withIndex()){
            item{
                JournalCard(navController = navController, journal = journal)
            }
        }
    }
}

@Composable
fun JournalCard(
    navController: NavController,
    journal: Journal
) {
    Box(
        modifier = Modifier
            .padding(start = 12.dp, bottom = 12.dp)
            .requiredWidth(width = 200.dp)
            .requiredHeight(height = 150.dp)
            .shadow(elevation = 4.dp)
            .background(Color.White)
            .border(3.dp, Color(0xff6d3840), RectangleShape)
            .clickable { navController.navigate(getJournalDetailsPath(journal.tripId!!)) },
        contentAlignment = Alignment.Center
    ){
        Image(
            painter = painterResource(id = R.drawable.background_rl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(5.dp)
            )

        Box(
            modifier = Modifier
                .height(35.dp)
                .width(150.dp)
                .background(Color.White)
                .border(1.dp, Color(0xff6d3840), RectangleShape),
            contentAlignment = Alignment.CenterStart
        ){
            Text(
                text = journal.title!!,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(fontWeight = FontWeight.Bold),

                )
        }

    }
}


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun TripCard(
    tripId: String?,
    title: String,
    image:Bitmap,
    navController: NavController
) {

    Box(

        modifier = Modifier
            .padding(start = 12.dp)
            .requiredWidth(width = 180.dp)
            .requiredHeight(height = 270.dp)
            .shadow(elevation = 4.dp)
            .clickable {
                navController.navigate(getTripDetailsPath(tripId.toString()))
            }

    ) {


        Image(
            bitmap = image.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .requiredWidth(width = 180.dp)
                .clip(shape = RoundedCornerShape(11.dp))
        )

        Image(
            painter = painterResource(id = R.drawable.trip_subtract),
            contentDescription = "Subtract",
            modifier = Modifier
                .requiredWidth(width = 180.dp)
                .requiredHeight(height = 270.dp)
        )

        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 7.29736328125.dp,
                    y = 204.32421875.dp
                )
                .requiredWidth(width = 165.dp)
                .requiredHeight(height = 58.dp)
                .background(color = Color(0xff6d3840))
        )
        Text(
            text = title,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 1.03.em,
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.01).sp
            ),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(
                    x = 21.871826171875.dp,
                    y = 214.dp
                )
                .requiredWidth(width = 135.dp)
                .requiredHeight(height = 44.dp)
        )
    }
        

}


