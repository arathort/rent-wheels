package com.example.drivetracker.ui.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.sharp.AccountCircle
import androidx.compose.material.icons.sharp.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.arathort.data.items.VehicleItem
import com.example.drivetracker.ui.RentWheelsScreen

@Composable
fun OrderVehicleScreen(
    navHostController: NavHostController,
    viewModel: OrderVehicleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            TopVehicleBar(viewModel)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                items(viewModel.getEnableVehicles()) { vehicle ->
                    DisplayVehicle(vehicle) {
                        navHostController.navigate("${RentWheelsScreen.VehicleDetails.name}/${vehicle.id}")
                    }
                }
            }

        }
        CustomBottomAppBar(navHostController, viewModel.isAdmin())
    }
    uiState.navigateTo?.let { route ->
        navHostController.navigate(route) { launchSingleTop = true }
        viewModel.onNavigateDone()
    }
}


@Composable
fun TopVehicleBar(viewModel: OrderVehicleViewModel) {
    val tabs = listOf("Авто", "Вантажівки")
    val uiState = viewModel.uiState.collectAsState().value
    TabRow(selectedTabIndex = if (uiState.isTruck) 1 else 0) {
        tabs.forEachIndexed { index, text ->
            Tab(
                selected = uiState.isTruck == (index != 0),
                onClick = { viewModel.changeVehicleType(index != 0) },
                text = { Text(text) }
            )
        }
    }
}

@Composable
fun CustomBottomAppBar(
    navHostController: NavHostController,
    isAdmin: Boolean,
) {
    var showPopup by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
        BottomAppBar {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navHostController.navigate(RentWheelsScreen.OrderVehicles.name) }) {
                    Icon(Icons.Default.Home, "Home")
                }
                if (isAdmin) {
                    IconButton(onClick = { showPopup = true }) {
                        Icon(Icons.Default.Add, "Add")
                    }
                    IconButton(onClick = { navHostController.navigate(RentWheelsScreen.StatsScreen.name) }) {
                        Icon(Icons.Sharp.List, "Stats")
                    }
                } else {
                    IconButton(onClick = { navHostController.navigate(RentWheelsScreen.MyVehicles.name) }) {
                        Icon(Icons.Sharp.AccountCircle, "Account")
                    }
                }
            }
        }
    }
    if (showPopup) {
        AlertDialog(
            onDismissRequest = { showPopup = false },
            title = { Text("Вибір") },
            text = {
                Column {
                    Text("Виберіть тип транспорту:")
                    Button(onClick = {
                        navHostController.navigate(RentWheelsScreen.AddCar.name); showPopup = false
                    }) {
                        Text("Авто")
                    }
                    Button(onClick = {
                        navHostController.navigate(RentWheelsScreen.AddTruck.name); showPopup =
                        false
                    }) {
                        Text("Вантажівка")
                    }
                }
            },
            confirmButton = {},
            dismissButton = { Button(onClick = { showPopup = false }) { Text("Скасувати") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayVehicle(vehicle: VehicleItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
        onClick = onClick
    ) {
        Row(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(20.dp)) {
                Text(
                    text = "${vehicle.getVehicle().brand} ${vehicle.getVehicle().model}",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    lineHeight = 35.sp
                )
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "★${vehicle.getRating()}")
                    Text(text = "${vehicle.getVehicle().year}р.")
                    Text(text = "${vehicle.price} грн")
                }
            }
        }
    }
}