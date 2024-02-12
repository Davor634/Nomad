package hr.ferit.davormaljkovic.nomad.trip_details

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.davormaljkovic.nomad.R
import hr.ferit.davormaljkovic.nomad.Routes
import hr.ferit.davormaljkovic.nomad.data.Day
import hr.ferit.davormaljkovic.nomad.data.Lodging
import hr.ferit.davormaljkovic.nomad.data.Transportation
import hr.ferit.davormaljkovic.nomad.data.Trip
import hr.ferit.davormaljkovic.nomad.home.ScreenTitle
import hr.ferit.davormaljkovic.nomad.home.TripHighlight
import hr.ferit.davormaljkovic.nomad.repository.TripRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale


@Composable
fun TripDetailsPage(
    navController: NavController,
    tripId: String
) {
    val viewModel: DetailsViewModel = viewModel()
    val state = viewModel.state.collectAsState()

    LaunchedEffect(
        key1 = "",
        block = {
            viewModel.getTripDetails(tripId)
        }
    )

    when(state.value) {
        is DetailsState.Loading -> DetailsLoading()
        is DetailsState.Success -> TripDetails(
            navController = navController,
            state = (state.value as DetailsState.Success).state!!,
            image = (state.value as DetailsState.Success).image
        )
    }
}

@Composable
fun DetailsLoading() {

}

@Composable
fun TripDetails(
    navController: NavController,
    state : Trip,
    image : Bitmap
    ) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF6F5F5))
            .verticalScroll(rememberScrollState(), enabled = true)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            ScreenTitle(title = "Nomad")
            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(
                        y = (-20).dp
                    )
                    .background(Color(0xFFF6F5F5))
            ) {
                TripHighlight(state,image)
            }
            TripLodgingAndTransportation(state!!.transportation!!, state.lodging!!)
            CyclicDayDisplay(navController,state.days, state.tripId, state)
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    navController.navigate("${Routes.SCREEN_JOURNAL_CREATOR}/${Uri.encode(Json.encodeToString(state))}")
                          },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color(0xFF6D3840))

            ) {
                Text(
                    text = "Create Journal",
                    color= Color.White
                )
            }

        }
    }
}

@Composable
fun TripLodgingAndTransportation(
    transportation: Transportation,
    lodging: Lodging
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .offset(
                y = (-50).dp
            )
            .background(Color(0xFFF6F5F5))

        ,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically

        ) {
        Box(
            modifier = Modifier
                .width(119.dp)
                .height(62.dp)
                .background(Color(0xFF6D3840))
                ){
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(
                            x = 5.dp
                        )
                ) {
                    val duration = "${transportation.durationHours}h ${transportation.durationMinutes}min"
                    val type = transportation.type!!.lowercase(Locale.ROOT)

                    val drawableType = getDrawableResourceId(context, type.toString())

                    RowRow(drawableType, type)
                    RowRow(R.drawable.clock_rl, duration)
                    RowRow(R.drawable.credit_card_rl, transportation.price.toString())

                }
            }
        Box(
            modifier = Modifier
                .width(119.dp)
                .height(62.dp)
                .background(Color(0xFF6D3840))
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(
                        x = 5.dp,
                    ),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                RowRow(R.drawable.bed_rl, lodging.name.toString())
                RowRow(R.drawable.credit_card_rl, lodging.price.toString())
            }

        }
    }

}

@Composable
fun getDrawableResourceId(context: Context, iconName: String): Int {
    return context.resources.getIdentifier(iconName, "drawable", context.packageName)
}

@Composable
fun RowRow(
    icon : Int,
    text : String
           ) {
    Row(

    ){
        Image(
            painter = painterResource(id = icon),
            contentDescription = "location-pin-3--navigation-map-maps-pin-gps-location",
            modifier = Modifier
                .requiredSize(13.dp)
                .offset(
                    y = 2.dp
                )

        )
        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
            color = Color.White,
        )
    }
}



@Composable
fun CyclicDayDisplay(navController: NavController,days: List<Day>, tripId: String?, trip:Trip) {
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf("") }
    var editedDescription by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .offset(
                y = -50.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = " Set as \nCurrent",
                style=MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight(700),
                modifier = Modifier.clickable {
                    TripRepository().saveCurrent(trip)
                    navController.navigate("homePage")
                }
            )
            IconButton(
                onClick = { selectedDayIndex = (selectedDayIndex - 1).coerceAtLeast(0) },
                modifier = Modifier.padding(horizontal =10.dp)
            ) {
                androidx.compose.material3.Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Day")
            }

            Text(text = "Day ${selectedDayIndex + 1}", style = MaterialTheme.typography.headlineSmall)

            IconButton(
                onClick = { selectedDayIndex = (selectedDayIndex + 1).coerceAtMost(days.size - 1) },
                modifier = Modifier.padding(horizontal =10.dp)
            ) {
                androidx.compose.material3.Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Day")
            }

            IconButton(onClick = {
                isEditing = true
                editedName = days[selectedDayIndex].name
                editedDescription = days[selectedDayIndex].description
            }) {
                androidx.compose.material3.Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Day")
            }
        }

        val currentDay = days.getOrNull(selectedDayIndex)
        if (currentDay != null) {
            if (isEditing) {
                EditableDayDetails(
                    editedName = editedName,
                    onNameChange = { editedName = it },
                    editedDescription = editedDescription,
                    onDescriptionChange = { editedDescription = it },
                    onSaveClick = {
                        TripRepository().saveDay(Day(selectedDayIndex, currentDay.date, editedName, editedDescription), tripId.toString())
                        isEditing = false
                    },
                    onCancelClick = { isEditing = false }
                )
            } else {
                Text(text = currentDay.name, style = MaterialTheme.typography.headlineSmall)
                Text(text = currentDay.description, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun EditableDayDetails(
    editedName: String,
    onNameChange: (String) -> Unit,
    editedDescription: String,
    onDescriptionChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        BasicTextField(
            value = editedName,
            onValueChange = { onNameChange(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onSaveClick() }),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(8.dp)
        )

        BasicTextField(
            value = editedDescription,
            onValueChange = { onDescriptionChange(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onSaveClick() }),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { onSaveClick() }) {
                Text("Save")
            }

            TextButton(onClick = { onCancelClick() }) {
                Text("Cancel")
            }
        }
    }
}



