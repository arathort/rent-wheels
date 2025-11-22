package com.example.drivetracker.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arathort.data.FirebaseDataSource
import com.arathort.data.repositories.PaymentRepository
import com.arathort.data.repositories.RentalRepository
import com.arathort.data.repositories.UserRepository
import com.arathort.data.repositories.VehicleRepository
import com.example.drivetracker.ui.adding.AddCarScreen
import com.example.drivetracker.ui.adding.AddTruckScreen
import com.example.drivetracker.ui.auth.LogInScreen
import com.example.drivetracker.ui.auth.SignInScreen
import com.example.drivetracker.ui.commenting.CommentScreen
import com.example.drivetracker.ui.commenting.CommentScreenViewModel
import com.example.drivetracker.ui.order.OrderVehicleScreen
import com.example.drivetracker.ui.statistics.StatisticScreen
import com.example.drivetracker.ui.userInfo.UserInfoScreen
import com.example.drivetracker.ui.vehicleDetails.VehicleDetailsScreen
import com.example.drivetracker.ui.vehicleDetails.VehicleDetailsViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.initialize

@Composable
fun DriveTrackerApp(
    navHostController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navHostController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val currentScreen = RentWheelsScreen.values().find { route?.startsWith(it.name) == true }
        ?: RentWheelsScreen.LogIn

    Firebase.initialize(context = LocalContext.current)
    val rep = VehicleRepository(FirebaseDataSource())
    val rentalRepository = RentalRepository(FirebaseDataSource())
    val userRepository = UserRepository(FirebaseDataSource())
    val paymentRepository = PaymentRepository(FirebaseDataSource())
    val auth = remember { Firebase.auth }

    val commentScreenViewModel = remember { CommentScreenViewModel(auth, rep) }

    NavHost(
        navController = navHostController,
        startDestination = RentWheelsScreen.LogIn.name
    ) {
        composable(RentWheelsScreen.SignIn.name) {
            SignInScreen(
                onLogInClick = { navHostController.navigate(RentWheelsScreen.LogIn.name) },
                auth = auth
            )
        }

        composable(RentWheelsScreen.LogIn.name) {
            LogInScreen(
                onSignInClick = { navHostController.navigate(RentWheelsScreen.SignIn.name) },
                auth = auth,
                onLogInClick = { navHostController.navigate(RentWheelsScreen.OrderVehicles.name) }
            )
        }

        composable(RentWheelsScreen.OrderVehicles.name) {
            OrderVehicleScreen(navHostController)
        }

        composable(
            route = "${RentWheelsScreen.VehicleDetails.name}/{vehicleId}"
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId") ?: return@composable
            val detailsViewModel =
                remember {
                    VehicleDetailsViewModel(
                        rep,
                        userRepository = userRepository,
                        auth = auth,
                        rentalRepository = rentalRepository,
                        paymentRepository = paymentRepository
                    )
                }
            Log.e("My", "id:$vehicleId")
            detailsViewModel.loadVehicleById(vehicleId)

            VehicleDetailsScreen(
                viewModel = detailsViewModel,
                navHostController = navHostController,
                deleteItem = {
                    detailsViewModel.deleteItem()
                    navHostController.navigate(RentWheelsScreen.OrderVehicles.name)
                }
            )
        }

        composable(RentWheelsScreen.AddCar.name) {
            AddCarScreen(
                navHostController = navHostController
            )
        }
        composable(RentWheelsScreen.AddTruck.name) {
            AddTruckScreen(navHostController = navHostController)
        }
        composable(RentWheelsScreen.MyVehicles.name) {
            UserInfoScreen(
                navHostController = navHostController,
                onCarClick = {
                    commentScreenViewModel.setCar(it)
                    navHostController.navigate(RentWheelsScreen.CommentScreen.name)
                },
                onTruckClick = {
                    commentScreenViewModel.setTruck(it)
                    navHostController.navigate(RentWheelsScreen.CommentScreen.name)
                }
            )
        }
        composable(RentWheelsScreen.CommentScreen.name) {
            CommentScreen(navHostController = navHostController)
        }
        composable(RentWheelsScreen.StatsScreen.name) {
            StatisticScreen(
                navHostController = navHostController
            )
        }
    }
}


enum class RentWheelsScreen {
    SignIn,
    LogIn,
    OrderVehicles,
    MyVehicles,
    AddCar,
    AddTruck,
    VehicleDetails,
    TruckDetails,
    CommentScreen,
    StatsScreen
}