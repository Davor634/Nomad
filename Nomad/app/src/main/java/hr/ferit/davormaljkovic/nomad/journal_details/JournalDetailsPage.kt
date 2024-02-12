package hr.ferit.davormaljkovic.nomad.journal_details

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import hr.ferit.davormaljkovic.nomad.data.Journal
import hr.ferit.davormaljkovic.nomad.trip_details.DetailsLoading

@Composable
fun JournalDetailsPage(
    navController: NavController,
    tripId: String
) {
    val viewModel: JournalDetailsViewModel = viewModel()
    val state = viewModel.state.collectAsState()

    LaunchedEffect(
        key1 = "",
        block = {
            viewModel.getJournalDetails(tripId)
        }
    )

    when(state.value) {
        is JournalDetailsState.Loading -> DetailsLoading()
        is JournalDetailsState.Success -> JournalDetails(
            navController = navController,
            state = (state.value as JournalDetailsState.Success).state!!,
            images = (state.value as JournalDetailsState.Success).images
        )
    }
}

@Composable
fun JournalDetails(
    navController: NavController,
    state:Journal,
    images:List<Bitmap>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color(0xFFF6F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color(0xff6d3840)),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = state.title!!,
                fontSize = 40.sp,
                modifier = Modifier
                    .fillMaxWidth(),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        Box() {
            Text(
                text = state.text!!,
                fontSize = 16.sp,
                modifier=Modifier.padding(10.dp),
                textAlign = TextAlign.Justify
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        ImageRow(images = images)
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ImageRow(
    images: List<Bitmap>
) {
    LazyRow(
        content = {
            for(image in images){
                run {
                    item {
                        Box(
                            modifier = Modifier
                                .size(150.dp, 150.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .padding(start = 10.dp)
                                .background(color = Color(0xFFF6F5F5))
                        ){
                            Image(
                                bitmap = image.asImageBitmap(),
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.background),
                                contentScale = ContentScale.Crop

                            )
                        }
                    }
                }
            }
        }
    )
}