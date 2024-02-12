package hr.ferit.davormaljkovic.nomad

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import hr.ferit.davormaljkovic.nomad.data.Trip
import hr.ferit.davormaljkovic.nomad.home.HomeScreen
import hr.ferit.davormaljkovic.nomad.journal_details.JournalDetailsPage
import hr.ferit.davormaljkovic.nomad.journal_details.NewJournalActivity
import hr.ferit.davormaljkovic.nomad.trip_details.TripCreator
import hr.ferit.davormaljkovic.nomad.trip_details.TripDetailsPage
import hr.ferit.davormaljkovic.nomad.trip_list.TripListScreen
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


object Routes {

    const val SCREEN_HOME = "homePage"
    const val SCREEN_TRIP_CREATOR = "tripCreator"
    fun getTripCreatorPath(): String { return "tripCreator" }
    const val SCREEN_TRIP_DETAILS = "tripDetails/{tripId}"
    fun getTripDetailsPath(tripId: String): String {
        return "tripDetails/$tripId"
    }
    const val SCREEN_TRIP_LIST="tripList"
    const val SCREEN_JOURNAL_CREATOR = "journalCreator"
    fun getJournalCreatorPath(tripId: String): String{
        return "journalCreator"
    }
    const val SCREEN_JOURNAL_DETAILS = "journalDetails/{tripId}"
    fun getJournalDetailsPath(tripId: String):String{
        return "journalDetails/$tripId"
    }
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavigationController() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Routes.SCREEN_HOME
    ) {
        composable(Routes.SCREEN_HOME) {
            HomeScreen(navController)
        }
        composable(Routes.SCREEN_TRIP_CREATOR) {
            TripCreator(navController)
        }
        composable(
                "${Routes.SCREEN_TRIP_LIST}/{tripsParam}",
                arguments = listOf(
                    navArgument("tripsParam") {
                        type = NavType.StringType
                    }
                )){backStackEntry->
            val tripsParam = backStackEntry.arguments?.getString("tripsParam")
            val trips: ArrayList<Trip> = Json.decodeFromString(tripsParam?:"[]")
            TripListScreen(navController ,trips)
        }



        composable(
            Routes.SCREEN_TRIP_DETAILS,
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("tripId")?.let { idFromArguments ->
                TripDetailsPage(
                    navController = navController,
                    tripId = idFromArguments
                )
            }
        }

        composable(
            Routes.SCREEN_JOURNAL_DETAILS,
            arguments = listOf(
                navArgument("tripId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("tripId")?.let { idFromArguments ->
                JournalDetailsPage(
                    navController = navController,
                    tripId = idFromArguments
                )
            }
        }

        composable(
            "${Routes.SCREEN_JOURNAL_CREATOR}/{tripParam}",
            arguments = listOf(
                navArgument("tripParam") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val tripsParam = backStackEntry.arguments?.getString("tripParam")
            val trip: Trip = Json.decodeFromString(tripsParam?:"{}")
            NewJournalActivity().JournalScreen(navController, trip)
        }
    }
}