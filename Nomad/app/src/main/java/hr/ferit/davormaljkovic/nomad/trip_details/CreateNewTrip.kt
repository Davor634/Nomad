package hr.ferit.davormaljkovic.nomad.trip_details

import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import hr.ferit.davormaljkovic.nomad.data.Day
import hr.ferit.davormaljkovic.nomad.data.Lodging
import hr.ferit.davormaljkovic.nomad.data.Transportation
import hr.ferit.davormaljkovic.nomad.data.TransportationType
import hr.ferit.davormaljkovic.nomad.data.Trip
import hr.ferit.davormaljkovic.nomad.data.TripDate
import hr.ferit.davormaljkovic.nomad.repository.TripRepository
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TripCreator(
    navController: NavController
){
    var tripId: String
    var title by remember { mutableStateOf("")}
    var location by remember { mutableStateOf("")}
    var date = TripDate("", "")
    var startDate : LocalDate = now()
    var endDate : LocalDate = now()
    var transportation : Transportation
    var lodging : Lodging
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var days : MutableList<Day> = mutableListOf()



    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFFFFF))
            .verticalScroll(rememberScrollState(), enabled = true)
    )
    {
        title = tripTittle()
        Row(
            modifier = Modifier.offset(
                y = 20.dp
            )
        ){
            Text(
                text = "Start Date",
                color = Color.Black,
                fontSize = 15.sp,
                modifier = Modifier
                    .offset(
                        y = 20.dp,
                        x = -50.dp
                    ))

            Text(
                text = "End Date",
                color = Color.Black,
                fontSize = 15.sp,
                modifier = Modifier
                    .offset(
                        y = 20.dp,
                        x = 45.dp
                    ))
        }
        Row(
            modifier = Modifier
                .offset(
                    y = 25.dp
                )
                .padding(horizontal = 10.dp)
        ) {
            date.startDate = MyDatePickerDialog("Pick Start Date")
            if(date.startDate != "Pick Start Date"){startDate = convertStringToLocalDate(date.startDate.toString())}
            date.endDate = MyDatePickerDialog("Pick End Date")
            if(date.endDate != "Pick End Date"){endDate = convertStringToLocalDate(date.endDate.toString())}
        }
        val period = Period.between(startDate, endDate)
        var dayCount = period.days + 1
        if(dayCount > 0){
            days = MutableList(dayCount){
                    index -> Day(index, startDate.plusDays(index.toLong()).toString(), "Name", "Description")
            }
        }
        location = tripLocation()
        imageUri = tripThumbnail()

        transportation = tripTransportation()
        lodging = tripLodging()
        Spacer(modifier=Modifier.height(100.dp))
        Button(onClick = {
            TripRepository().saveTrip(Trip(null, title, location, days, imageUri.toString(), transportation, lodging, date, false))
            navController.navigate("homePage")
            },

        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Text("Save Trip")
        }


    }


}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tripTittle() : String{

    var text by rememberSaveable { mutableStateOf("")}


     OutlinedTextField(

        value = text,
        onValueChange = {text = it},
        label = { Text(
            "Set Trip Tittle",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()

        )},
         colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
         textStyle = TextStyle(textAlign = TextAlign.Center),
         singleLine = true,
         modifier = Modifier
             .offset(
                 y = 20.dp
             )
             .width(350.dp)
             .height(65.dp)
     )

    return text
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tripLocation(): String {

    var location by rememberSaveable{mutableStateOf("")}

    Row(
        modifier = Modifier.offset(
            y = 50.dp
        )
    ) {
        Text(
            text = "Location",
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier.offset(
                y = 10.dp
            )
        )
        Spacer(modifier = Modifier.width(20.dp))
        OutlinedTextField(
            value = location,
            onValueChange = {
                location = it
            },
            label = {
                Text(
                    "Enter Location",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()

                )
            },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            textStyle = TextStyle(textAlign = TextAlign.Center),
            singleLine = true,
            modifier = Modifier
                .offset(
                    x = 10.dp
                )
                .width(230.dp)
                .height(60.dp)




        )

    }

    return location

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tripTransportation() : Transportation {

    var transportation = Transportation(null,null,null,"")


    var err by rememberSaveable { mutableStateOf(false)}
    var transportationType by rememberSaveable { mutableStateOf("")}
    var transportationPrice by rememberSaveable { mutableStateOf("")}
    var transportationDurationHours by rememberSaveable { mutableStateOf("")}
    var transportationDurationMinutes by rememberSaveable { mutableStateOf("")}

    Row(
        modifier = Modifier.offset(
            y = 60.dp
        )
            .padding(top = 10.dp)
    ) {
        Text(
            text = "Transportation:",
            color = Color.Black,
            fontSize = 20.sp,
            modifier = Modifier.offset(
                y = 10.dp
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .offset(
                    y = 10.dp
                )
                .width(110.dp)
                .height(30.dp)
                .background(Color.White)
                .border(width = 1.dp, color = Color(0xFF000000)),
        ) {
           transportationType = tripDropDownMenu()

            Text(
                modifier = Modifier.offset(
                    x = 35.dp,
                    y = 5.dp
                ),
                text = transportationType
            )
        }

    }

    Row(
        modifier = Modifier
            .offset(
                y = 70.dp
            )
    ){
        Text(
            text = "Price",
            color = Color.Black,
            fontSize = 17.sp,
            modifier = Modifier.offset(
                y = 10.dp,
                x = -35.dp
            )
        )
        Spacer(modifier = Modifier.width(110.dp))
        Text(
            text = "Duration",
            color = Color.Black,
            fontSize = 17.sp,
            modifier = Modifier.offset(
                y = 10.dp,
                x = -20.dp
            )
        )
    }

    Row(
        modifier = Modifier
            .offset(
                y = 80.dp
            )
    ) {

        OutlinedTextField(

            value = transportationPrice,
            onValueChange = {
                transportationPrice = it
            },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(textAlign = TextAlign.Center, platformStyle = PlatformTextStyle(false)),
            singleLine = true,
            modifier = Modifier
                .offset(
                    x = 15.dp
                )
                .width(110.dp)
                .height(55.dp)

        )
        Spacer(modifier = Modifier.width(30.dp))
        OutlinedTextField(

            value = transportationDurationHours,
            onValueChange = {
                transportationDurationHours = it
            },
            label = { Text(
                "Hours",
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()

            )},
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            textStyle = TextStyle(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .width(100.dp)
                .height(55.dp)
        )
        OutlinedTextField(

            value = transportationDurationMinutes,
            onValueChange = {
                transportationDurationMinutes = it
            },
            label = { Text(
                "Minutes",
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()

            )},
            visualTransformation = VisualTransformation.None,
            textStyle = TextStyle(textAlign = TextAlign.Center),
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
                .width(100.dp)
                .height(55.dp)
                .padding(start = 5.dp)
        )

    }

    transportation.type = transportationType
    if(transportationDurationHours != ""){
        transportation.durationHours = transportationDurationHours.toInt()
    }
    if(transportationDurationMinutes != ""){
        transportation.durationMinutes = transportationDurationMinutes.toInt()
    }
    if(transportationPrice != ""){
        transportation.price = transportationPrice.toInt()
    }
    return transportation
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun tripLodging() : Lodging{

    var lodging = Lodging("", null)

    var lodgingName by rememberSaveable{ mutableStateOf("")}
    var lodgingPrice by rememberSaveable{ mutableStateOf("")}

    Text(
        text = "Lodging",
        color = Color.Black,
        fontSize = 20.sp,
        modifier = Modifier.offset(
            y = 80.dp
        )
    )

    Row(
        modifier = Modifier.offset(
            y = 85.dp
        )
    ){

        OutlinedTextField(
            value = lodgingName,
            onValueChange = {
                lodgingName = it
            },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            textStyle = TextStyle(textAlign = TextAlign.Center, platformStyle = PlatformTextStyle(false)),
            singleLine = true,
            label = {
                Text(
                    "Name",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()

                )
            },
            modifier = Modifier
                .width(160.dp)
                .height(55.dp)

        )



        OutlinedTextField(
            value = lodgingPrice,
            onValueChange = {
                lodgingPrice = it
            },
            colors = TextFieldDefaults.textFieldColors(containerColor = Color.White),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(textAlign = TextAlign.Center, platformStyle = PlatformTextStyle(false)),
            singleLine = true,
            label = {
                Text(
                    "Price",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier
                .offset(
                    x = 15.dp
                )
                .width(160.dp)
                .height(55.dp)

        )

    }

    lodging.name = lodgingName
    if(lodgingPrice != ""){
        lodging.price = lodgingPrice.toInt()
    }
    return lodging
}

@Composable
fun tripThumbnail() : Uri? {
    var storage = Firebase.storage
    var storageRef = storage.reference
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),//22
        onResult = { uri ->
            uri?.let {
                imageUri = it
                var file = it
                val tripRef = storageRef.child("images/${file.lastPathSegment}")
                var uploadTask = tripRef.putFile(file)


            }
        }
    )



    Column(
        modifier = Modifier.offset(
            y = 70.dp
        )
    ) {
        imageUri?.let {//22
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
                galleryLauncher.launch("image/*")//22
            }
        ) {
            Text(
                text = "Pick image",
                color = Color.White,
            )
        }
    }

    return imageUri
}





@Composable
fun tripDropDownMenu() : String {
    var str by remember { mutableStateOf("")}
    var expanded by remember { mutableStateOf(false) }
    var transportationType by remember{ mutableStateOf(TransportationType.EMPTY)}


    IconButton(
        onClick = { expanded = !expanded },
        modifier = Modifier.width(110.dp)
        ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More",
            modifier = Modifier.offset(
                x = 42.dp
            )
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        enumValues<TransportationType>().forEach {
            DropdownMenuItem(
                text = { Text(it.str) },
                onClick = {
                    str = it.str
                    expanded = false
                },
                modifier = Modifier.background(Color.White)
            )
        }
    }
    return str
}



private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd-MM-yyyy")
    return formatter.format(Date(millis))
}

@RequiresApi(Build.VERSION_CODES.O)
private fun convertStringToLocalDate(
    input: String
): LocalDate {
    val date = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    return date
}

@RequiresApi(Build.VERSION_CODES.O)
fun localDateToMillis(localDate: LocalDate): Long {
    val localDateTime: LocalDateTime = localDate.atStartOfDay()
    return localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= System.currentTimeMillis()
        }
    })

    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onDateSelected(selectedDate)
                onDismiss()
            }

            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text(text = "Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyDatePickerDialog(
    input:String
) : String{

    var date by remember {
        mutableStateOf(input)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(
            onClick = { showDatePicker = true },
            shape = RectangleShape,
            modifier = Modifier
                .padding(
                    horizontal = 20.dp,
                    vertical = 20.dp
                )
                .width(130.dp)
                .height(45.dp),
            border = BorderStroke(1.dp, Color.Black),
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color.White,
                disabledContentColor = Color.White
            ),


        ) {
            Text(
                text = date,
                fontWeight = FontWeight.Light,
                fontSize = 11.sp
            )
        }
    }

    if (showDatePicker) {
        MyDatePickerDialog(
            onDateSelected = { date = it },
            onDismiss = { showDatePicker = false }
        )
    }

    return date

}











