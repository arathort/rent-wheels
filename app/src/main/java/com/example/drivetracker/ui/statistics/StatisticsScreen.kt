package com.example.drivetracker.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.arathort.data.items.CarItem
import com.arathort.data.items.TruckItem
import com.example.drivetracker.ui.RentWheelsScreen
import com.example.drivetracker.ui.order.CustomBottomAppBar

@Composable
fun StatisticScreen(
    viewModel: StatisticScreenViewModel=hiltViewModel(),
    navHostController: NavHostController
) {
    val cars by viewModel.cars.collectAsState()
    val trucks by viewModel.trucks.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Статистика",
                    fontSize = MaterialTheme.typography.headlineLarge.fontSize
                )
                Button(
                    onClick = {
                        viewModel.exit()
                        navHostController.navigate(RentWheelsScreen.LogIn.name)
                    }
                ) {
                    Text(text = "Вийти")
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(300.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 80.dp)
            ) {
                items(cars) { car ->
                    CarStats(carItem = car, viewModel = viewModel)
                }

                items(trucks) { truck ->
                    TruckStats(truckItem = truck, viewModel = viewModel)
                }
            }
        }

        CustomBottomAppBar(navHostController, viewModel.isAdmin())
    }
}

@Composable
fun CarStats(carItem: CarItem, viewModel: StatisticScreenViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${carItem.car.brand} ${carItem.car.model}",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            val statusText = if (carItem.isRented()) {
                "Орендовано: ${viewModel.getCarOwner(carItem)}"
            } else {
                "Вільна"
            }
            Text(text = statusText)
            Text(text = "Кількість оренд: ${viewModel.getNumberOfRent(carItem)}")
        }
    }
}

@Composable
fun TruckStats(truckItem: TruckItem, viewModel: StatisticScreenViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${truckItem.truck.brand} ${truckItem.truck.model}",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            val statusText = if (truckItem.isRented()) {
                "Орендовано: ${viewModel.getTruckOwner(truckItem)}"
            } else {
                "Вільна"
            }
            Text(text = statusText)
            Text(text = "Кількість оренд: ${viewModel.getNumberOfRent(truckItem)}")
        }
    }
}
