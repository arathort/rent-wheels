package com.example.drivetracker.ui.userInfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.drivetracker.R
import com.arathort.data.items.CarItem
import com.arathort.data.items.TruckItem
import com.arathort.data.records.CarRentalRecord
import com.arathort.data.records.TruckRentalRecord
import com.example.drivetracker.ui.RentWheelsScreen
import com.example.drivetracker.ui.order.CustomBottomAppBar

@Composable
fun UserInfoScreen(
    navHostController: NavHostController,
    viewModel: UserInfoViewModel,
    onCarClick: (CarItem) -> Unit,
    onTruckClick: (TruckItem) -> Unit
) {
    val carList by viewModel.carRecords.collectAsState()
    val truckList by viewModel.truckRecords.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCarRecords()
        viewModel.loadTruckRecords()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Інформація про користувача",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp)
            )

            Card {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user_icon),
                        contentDescription = "User image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = viewModel.getUserEmail(),
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    viewModel.exit()
                    navHostController.navigate(RentWheelsScreen.LogIn.name)
                }
            ) {
                Text(text = "Вийти")
            }

            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                items(carList) { record ->
                    DisplayCarRecord(
                        carRecord = record,
                        onFinish = {
                            viewModel.updateCarRecord(record)
                            viewModel.updateCar(record.item)
                            onCarClick(record.item)
                        },
                        isEnd = viewModel.isDateExpired(record.endRentDate)
                    )
                }
                items(truckList) { record ->
                    DisplayTruckRecord(
                        truckRecord = record,
                        onFinish = {
                            viewModel.updateTruckRecord(record)
                            viewModel.updateTruck(record.item)
                            onTruckClick(record.item)
                        },
                        isEnd = viewModel.isDateExpired(record.endRentDate)
                    )
                }
            }

            CustomBottomAppBar(
                navHostController = navHostController,
                isAdmin = viewModel.isAdmin(),
            )
        }
    }
}

@Composable
fun DisplayCarRecord(
    carRecord: CarRentalRecord,
    onFinish: () -> Unit,
    isEnd: Boolean
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${carRecord.item.car.brand} ${carRecord.item.car.model}",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    lineHeight = 28.sp,
                    modifier = Modifier.weight(1f)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Видача: ${carRecord.startRentDate}")
                    Text(text = "Заверш.: ${carRecord.endRentDate}")
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                Text("Завершити")
            }
            if (isEnd) {
                Text(
                    text = "Термін оренди закінчився!",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
fun DisplayTruckRecord(
    truckRecord: TruckRentalRecord,
    onFinish: () -> Unit,
    isEnd: Boolean
) {
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${truckRecord.item.truck.brand} ${truckRecord.item.truck.model}",
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    lineHeight = 28.sp,
                    modifier = Modifier.weight(1f)
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Видача: ${truckRecord.startRentDate}")
                    Text(text = "Заверш.: ${truckRecord.endRentDate}")
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                Text("Завершити")
            }
            if (isEnd) {
                Text(
                    text = "Термін оренди закінчився!",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
